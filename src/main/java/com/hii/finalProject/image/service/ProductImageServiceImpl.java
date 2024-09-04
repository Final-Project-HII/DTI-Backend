package com.hii.finalProject.image.service;

import com.hii.finalProject.cloudinary.CloudinaryService;
import com.hii.finalProject.image.dto.ProductImageRequestDto;
import com.hii.finalProject.image.dto.ProductImageResponseDto;
import com.hii.finalProject.image.entity.ProductImage;
import com.hii.finalProject.image.repository.ProductImageRepository;
import com.hii.finalProject.products.entity.Product;
import com.hii.finalProject.products.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;
    public ProductImageServiceImpl(ProductImageRepository productImageRepository, ProductRepository productRepository, CloudinaryService cloudinaryService){
        this.productImageRepository = productImageRepository;
        this.productRepository = productRepository;
        this.cloudinaryService = cloudinaryService;

    }

    @Override
    public ProductImageResponseDto createProductImage(MultipartFile image, ProductImageRequestDto requestDTO) {
        Optional<Product> productOptional = productRepository.findById(requestDTO.getProductId());
        if (!productOptional.isPresent()) {
            throw new IllegalArgumentException("Product not found");
        }
        String imageUrl = cloudinaryService.uploadFile(image, "product_images");


        ProductImage productImage = new ProductImage();
        productImage.setProduct(productOptional.get());
        productImage.setImageUrl(requestDTO.getImageUrl());
        productImage = productImageRepository.save(productImage);

        return mapToResponseDTO(productImage);
    }

    @Override
    @Transactional
    public void deleteProductImage(Long id) {
        ProductImage productImage = productImageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ProductImage not found"));

        try {
            cloudinaryService.deleteImage(productImage.getImageUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }

        productImageRepository.delete(productImage);
    }
    @Override
    @Transactional
    public void deleteProductImages(List<ProductImage> images) {
        for (ProductImage image : images) {
            try {
                cloudinaryService.deleteImage(image.getImageUrl());
            } catch (IOException e) {
                // Log the error but continue with deletion from database
                e.printStackTrace();
            }
        }
        productImageRepository.deleteAll(images);
    }
    @Transactional
    public void deleteAllImagesForProduct(Long productId) {
        List<ProductImage> images = productImageRepository.findByProductId(productId);
        for (ProductImage image : images) {
            try {
                cloudinaryService.deleteImage(image.getImageUrl());
            } catch (IOException e) {
                // Log the error but continue with database deletion
                System.err.println("Error deleting image from Cloudinary: " + e.getMessage());
            }
        }
        productImageRepository.deleteByProductId(productId);
    }

    @Override
    public List<ProductImageResponseDto> getProductImage(Long productId) {
        List<ProductImage> productImages = productImageRepository.findByProductId(productId);
        return productImages.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    @Override
    public ProductImageResponseDto updateProductImage(Long imageId, MultipartFile newImage) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("ProductImage not found"));

        // Hapus gambar lama dari Cloudinary
        try {
            cloudinaryService.deleteImage(productImage.getImageUrl());
        } catch (IOException e) {
            throw new RuntimeException("Error deleting old image from Cloudinary", e);
        }

        // Upload gambar baru ke Cloudinary
        String newPublicId = cloudinaryService.uploadFile(newImage, "product_images");

        // Update publicId gambar
        productImage.setImageUrl(newPublicId);
        productImage = productImageRepository.save(productImage);

        return mapToResponseDTO(productImage);
    }
    @Override
    public ProductImageResponseDto getProductImageById(Long id) {
        ProductImage productImage = productImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductImage not found with id: " + id));
        return mapToResponseDTO(productImage);
    }


    private ProductImageResponseDto mapToResponseDTO(ProductImage productImage) {
        ProductImageResponseDto responseDTO = new ProductImageResponseDto();
        responseDTO.setId(productImage.getId());
        responseDTO.setProductId(productImage.getProduct().getId());
        responseDTO.setImageUrl(cloudinaryService.generateUrl(productImage.getImageUrl()));
        responseDTO.setCreatedAt(productImage.getCreatedAt());
        responseDTO.setUpdatedAt(productImage.getUpdatedAt());
        return responseDTO;
    }
}
