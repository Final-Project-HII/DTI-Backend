package com.hii.finalProject.products.service;

import com.hii.finalProject.image.dto.ProductImageResponseDto;
import com.hii.finalProject.products.dto.NewProductRequestDto;
import com.hii.finalProject.products.dto.ProductListDtoResponse;
import com.hii.finalProject.products.dto.UpdateProductRequestDto;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    ProductListDtoResponse createProduct(NewProductRequestDto productRequestDTO, List<MultipartFile> productImages);
    Page<ProductListDtoResponse> getAllProducts(String search, String categoryName, String sortBy, String sortDirection, Pageable pageable);
    ProductListDtoResponse getProductById(Long id);
    ProductListDtoResponse updateProduct(Long id, UpdateProductRequestDto updateProductRequestDto, List<MultipartFile> newImages);
    void deleteProduct(Long id);

    @Transactional
    void reduceStock(Long productId, int quantity);
}
