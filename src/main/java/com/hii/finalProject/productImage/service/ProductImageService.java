package com.hii.finalProject.productImage.service;

import com.hii.finalProject.productImage.dto.ProductImageDTO;
import java.util.List;

public interface ProductImageService {
    List<ProductImageDTO> getImagesByProductId(Integer productId);
    ProductImageDTO addProductImage(ProductImageDTO productImageDTO);
    void deleteProductImage(Long id);
}