package com.hii.finalProject.products.service;

import com.hii.finalProject.categories.dto.CategoriesResponseDto;
import com.hii.finalProject.categories.entity.Categories;
import com.hii.finalProject.categories.repository.CategoriesRepository;
import com.hii.finalProject.products.dto.NewProductRequestDto;
import com.hii.finalProject.products.dto.ProductListDtoResponse;
import com.hii.finalProject.products.entity.Product;
import com.hii.finalProject.products.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

@Service
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final CategoriesRepository categoriesRepository;
//    private final ModelMapper modelMapper;
    public ProductServiceImpl(ProductRepository productRepository, CategoriesRepository categoriesRepository){
        this.productRepository = productRepository;
        this.categoriesRepository = categoriesRepository;
    }

    @Override
    public Page<ProductListDtoResponse> getAllProducts(String search, Long categoryId, String sortBy, String sortDirection, Pageable pageable) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isEmpty()) {
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%")
//                        cb.like(cb.lower(root.get("categoryName")), "%" + search.toLowerCase() + "%")
                ));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("categories").get("id"), categoryId));
            }

            // If no predicates, return null (which means no filtering)
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Product> products = productRepository.findAll(spec, pageableWithSort);
        return products.map(this::mapToProductListDtoResponse);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private ProductListDtoResponse mapToProductListDtoResponse(Product product) {
        ProductListDtoResponse responseDto = new ProductListDtoResponse();
        responseDto.setId(product.getId());
        responseDto.setName(product.getName());
        responseDto.setDescription(product.getDescription());
        responseDto.setPrice(product.getPrice());
        responseDto.setWeight(product.getWeight());
        // Check if the product has a category before accessing its properties
        if (product.getCategories() != null) {
            responseDto.setCategoryId(product.getCategories().getId());
            responseDto.setCategoryName(product.getCategories().getName());
        } else {
            // Set default values or leave as null depending on your requirements
            responseDto.setCategoryId(null);
            responseDto.setCategoryName("Uncategorized");
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
        // Note: Category is set separately in the createProduct method
        return product;
    }

    @Override
    public ProductListDtoResponse createProduct(NewProductRequestDto productRequestDTO) {
        Product product = mapToProduct(productRequestDTO);
        Categories categories = categoriesRepository.findById(productRequestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategories(categories);
        Product savedProduct = productRepository.save(product);
        return mapToProductListDtoResponse(savedProduct);
    }
}


