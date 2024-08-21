package com.hii.finalProject.products.service;

import com.hii.finalProject.products.dto.NewProductRequestDto;
import com.hii.finalProject.products.dto.ProductListDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductListDtoResponse createProduct(NewProductRequestDto productRequestDTO);
//    ProductDetailResponseDTO getProductById(Long id);
    Page<ProductListDtoResponse> getAllProducts(String search, Long categoryId, String sortBy, String sortDirection, Pageable pageable);
//    ProductDetailResponseDTO updateProduct(Long id, UpdateProductRequestDTO productRequestDTO);
    void deleteProduct(Long id);
//    List<ProductResponseDTO> searchProducts(String keyword);
}
