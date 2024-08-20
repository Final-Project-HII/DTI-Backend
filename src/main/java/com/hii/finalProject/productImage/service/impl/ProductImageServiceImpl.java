package com.hii.finalProject.productImage.service.impl;

import com.hii.finalProject.productImage.dto.ProductImageDTO;
import com.hii.finalProject.productImage.entity.ProductImage;
import com.hii.finalProject.productImage.repository.ProductImageRepository;
import com.hii.finalProject.productImage.service.ProductImageService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;

    public ProductImageServiceImpl(ProductImageRepository productImageRepository) {
        this.productImageRepository = productImageRepository;
    }

    @Override
    public List<ProductImageDTO> getImagesByProductId(Integer productId) {
        return productImageRepository.findByProductId(productId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductImageDTO addProductImage(ProductImageDTO productImageDTO) {
        ProductImage productImage = convertToEntity(productImageDTO);
        ProductImage savedProductImage = productImageRepository.save(productImage);
        return convertToDTO(savedProductImage);
    }

    @Override
    public void deleteProductImage(Long id) {
        productImageRepository.deleteById(id);
    }

    private ProductImageDTO convertToDTO(ProductImage productImage) {
        ProductImageDTO dto = new ProductImageDTO();
        BeanUtils.copyProperties(productImage, dto);
        return dto;
    }

    private ProductImage convertToEntity(ProductImageDTO dto) {
        ProductImage productImage = new ProductImage();
        BeanUtils.copyProperties(dto, productImage);
        return productImage;
    }
}