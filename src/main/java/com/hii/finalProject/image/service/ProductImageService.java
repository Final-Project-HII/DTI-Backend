package com.hii.finalProject.image.service;

import com.hii.finalProject.image.dto.ProductImageRequestDto;
import com.hii.finalProject.image.dto.ProductImageResponseDto;
import com.hii.finalProject.image.entity.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {
    ProductImageResponseDto createProductImage(MultipartFile image, ProductImageRequestDto requestDTO);
    void deleteProductImage(Long id);
    public List<ProductImageResponseDto> getProductImage(Long productId);
    ProductImageResponseDto updateProductImage(Long imageId, MultipartFile newImage);
    ProductImageResponseDto getProductImageById(Long id);
    void deleteProductImages(List<ProductImage> images);
    void deleteAllImagesForProduct(Long productId);

}
