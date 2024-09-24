package com.hii.finalProject.products.service;

import com.hii.finalProject.categories.entity.Categories;
import com.hii.finalProject.categories.repository.CategoriesRepository;
import com.hii.finalProject.cloudinary.CloudinaryService;
import com.hii.finalProject.exceptions.UniqueNameViolationException;
import com.hii.finalProject.image.dto.ProductImageRequestDto;
import com.hii.finalProject.image.dto.ProductImageResponseDto;
import com.hii.finalProject.image.entity.ProductImage;
import com.hii.finalProject.image.repository.ProductImageRepository;
import com.hii.finalProject.image.service.ProductImageService;
import com.hii.finalProject.products.dto.NewProductRequestDto;
import com.hii.finalProject.products.dto.ProductListDtoResponse;
import com.hii.finalProject.products.dto.UpdateProductRequestDto;
import com.hii.finalProject.products.entity.Product;
import com.hii.finalProject.products.repository.ProductRepository;
import com.hii.finalProject.stock.dto.StockDtoProductResponse;
import com.hii.finalProject.stock.entity.Stock;
import com.hii.finalProject.stock.repository.StockRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.Predicate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final CategoriesRepository categoriesRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductImageService productImageService;
    private final ProductImageRepository productImageRepository;
    private final EntityManager entityManager;
    private final StockRepository stockRepository;

    public ProductServiceImpl(StockRepository stockRepository, ProductRepository productRepository, EntityManager entityManager,  ProductImageRepository productImageRepository, CategoriesRepository categoriesRepository, CloudinaryService cloudinaryService, ProductImageService productImageService){

        this.productRepository = productRepository;
        this.categoriesRepository = categoriesRepository;
        this.cloudinaryService = cloudinaryService;
        this.productImageService = productImageService;
        this.productImageRepository = productImageRepository;
        this.entityManager = entityManager;
        this.stockRepository = stockRepository;
    }

    @Override
    public Page<ProductListDtoResponse> getAllProducts(String search, String categoryName, String sortBy, String sortDirection, Pageable pageable) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isEmpty()) {
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("categories").get("name")), search.toLowerCase()+"%")
                ));
            }
            if (categoryName != null && !categoryName.isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("categories").get("name")), categoryName.toLowerCase()));
            }

            // If no predicates, return null (which means no filtering)
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Product> products = productRepository.findAll(spec, pageableWithSort);
        return products.map(this::getPublicProductList);
    }
    @Override
    @Transactional
    public ProductListDtoResponse updateProduct(Long id, UpdateProductRequestDto updateProductRequestDto, List<MultipartFile> newImages) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Update product fields
        product.setName(updateProductRequestDto.getName());
        product.setDescription(updateProductRequestDto.getDescription());
        product.setPrice(Integer.parseInt(updateProductRequestDto.getPrice()));
        product.setWeight(updateProductRequestDto.getWeight());

        if (updateProductRequestDto.getCategoryId() != null) {
            Categories newCategory = categoriesRepository.findById(updateProductRequestDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategories(newCategory);
        }

        // Handle image deletions
        List<Long> imagesToDelete = updateProductRequestDto.getDeleteImages();
        if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
            List<ProductImage> imagesToRemove = new ArrayList<>();
            for (ProductImage image : product.getProductImages()) {
                if (imagesToDelete.contains(image.getId())) {
                    try {
                        cloudinaryService.deleteImage(image.getImageUrl());
                        imagesToRemove.add(image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            product.getProductImages().removeAll(imagesToRemove);
            productImageService.deleteProductImages(imagesToRemove);
        }

        // Add new images
        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile newImage : newImages) {
                String imageUrl = cloudinaryService.uploadFile(newImage, "product_images");
                ProductImageRequestDto imageDto = new ProductImageRequestDto();
                imageDto.setProductId(product.getId());
                imageDto.setImageUrl(imageUrl);
                ProductImageResponseDto savedImage = productImageService.createProductImage(newImage, imageDto);
                ProductImage productImage = new ProductImage();
                productImage.setId(savedImage.getId());
                productImage.setProduct(product);
                productImage.setImageUrl(savedImage.getImageUrl());
                product.getProductImages().add(productImage);
            }
        }

        Product updatedProduct = productRepository.save(product);
        return getPublicProductList(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Create a new list to avoid ConcurrentModificationException
        List<ProductImage> imagesToDelete = new ArrayList<>(product.getProductImages());

        // Delete associated images from Cloudinary and database
        for (ProductImage image : imagesToDelete) {
            try {
                cloudinaryService.deleteImage(image.getImageUrl());
            } catch (IOException e) {
                System.err.println("Error deleting image from Cloudinary: " + e.getMessage());
            }
            productImageRepository.delete(image);
        }

        // Clear the product's image collection
        product.getProductImages().clear();

        // Flush changes to ensure image deletions are persisted
        entityManager.flush();

        // Delete the product
        productRepository.delete(product);

        // Flush again to ensure product deletion is persisted
        entityManager.flush();

        // Double-check if the product is actually deleted
        if (productRepository.existsById(id)) {
            // If the product still exists, use the deleteByIdAndFlush method
            productRepository.deleteByIdAndFlush(id);
        }

        System.out.println("Product and associated images deleted for id: " + id);
    }

    @Override
    public ProductListDtoResponse createProduct(NewProductRequestDto productRequestDTO, List<MultipartFile> productImages) {
        // Validasi input
        if (productRequestDTO.getName() == null || productRequestDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }

        String productName = productRequestDTO.getName().trim();

        // Cek apakah nama product sudah ada
        if (productRepository.existsByNameIgnoreCase(productName)) {
            throw new UniqueNameViolationException("Product", productName);
        }
        Product product = mapToProduct(productRequestDTO);
        Categories categories = categoriesRepository.findById(productRequestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategories(categories);

        Product savedProduct = productRepository.save(product);

        List<ProductImageResponseDto> savedImages = new ArrayList<>();

        for (MultipartFile image : productImages) {
            String imageUrl = cloudinaryService.uploadFile(image, "product_images");
            ProductImageRequestDto imageDto = new ProductImageRequestDto();
            imageDto.setProductId(savedProduct.getId());
            imageDto.setImageUrl(imageUrl);
            savedImages.add(productImageService.createProductImage(image, imageDto));
        }
        return getPublicProductList(savedProduct);
    }

    @Override
    public ProductListDtoResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        List<ProductImageResponseDto> images = productImageService.getProductImage(id);
        List<Stock> stocks = stockRepository.findByProduct(product);
        return getPublicProductList(product, images, stocks);
    }


    private ProductListDtoResponse getPublicProductList(Product product) {
        List<ProductImageResponseDto> images = productImageService.getProductImage(product.getId());
        List<Stock> stocks = stockRepository.findByProduct(product);
        return getPublicProductList(product, images, stocks);
    }

    private ProductListDtoResponse getPublicProductList(Product product, List<ProductImageResponseDto> images, List<Stock> stocks) {
        ProductListDtoResponse responseDto = new ProductListDtoResponse();
        responseDto.setId(product.getId());
        responseDto.setName(product.getName());
        responseDto.setDescription(product.getDescription());
        responseDto.setPrice(product.getPrice());
        responseDto.setWeight(product.getWeight());
        responseDto.setProductImages(images);
        // Convert Stock to StockDto
        List<StockDtoProductResponse> stockDtos = stocks.stream()
                .map(StockDtoProductResponse::convertFromStock)
                .collect(Collectors.toList());
        // Calculate total stock
        Integer totalStock = stockDtos.stream()
                .map(StockDtoProductResponse::getQuantity)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);

        responseDto.setTotalStock(totalStock > 0 ? totalStock : 0);

        responseDto.setStocks(stockDtos);
        if (product.getCategories() != null) {
            responseDto.setCategoryId(product.getCategories().getId());
            responseDto.setCategoryName(product.getCategories().getName());
        }
        responseDto.setCreatedAt(product.getCreatedAt());
        responseDto.setUpdatedAt(product.getUpdatedAt());
        return responseDto;
    }

    private Product mapToProduct(NewProductRequestDto requestDto) {
        Product product = new Product();
        product.setName(requestDto.getName());
        product.setDescription(requestDto.getDescription());
        product.setPrice(Integer.parseInt(requestDto.getPrice()));
        product.setWeight(requestDto.getWeight());

        return product;
    }

}


