package com.hii.finalProject.products.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hii.finalProject.products.dto.NewProductRequestDto;
import com.hii.finalProject.products.dto.ProductListDtoResponse;
import com.hii.finalProject.products.dto.UpdateProductRequestDto;
import com.hii.finalProject.products.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
 
@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService){
        this.productService = productService;
    }
    @PostMapping("/create")
//    @PreAuthorize("hasAuthority('SCOPE_SUPER')")
    public ResponseEntity<ProductListDtoResponse> createProduct(
            @ModelAttribute NewProductRequestDto productRequestDTO,
            @RequestParam("productImages") List<MultipartFile> productImages) {
        ProductListDtoResponse createdProduct = productService.createProduct(productRequestDTO, productImages);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<Page<ProductListDtoResponse>> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String categoryName,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<ProductListDtoResponse> products = productService.getAllProducts(search, categoryName, sortBy, sortDirection, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductListDtoResponse> getProductById(@PathVariable Long id) {
        ProductListDtoResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PutMapping(value = "/update/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    @PreAuthorize("hasAuthority('SCOPE_SUPER')")
    public ResponseEntity<ProductListDtoResponse> updateProduct(
            @PathVariable Long id,
            @RequestPart(value = "product") String updateProductRequestDtoJson,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) {
        UpdateProductRequestDto updateProductRequestDto = convertJsonToObject(updateProductRequestDtoJson, UpdateProductRequestDto.class);
        ProductListDtoResponse updatedProduct = productService.updateProduct(id, updateProductRequestDto, newImages);
        return ResponseEntity.ok(updatedProduct);
    }
    @DeleteMapping("/delete/{id}")
//    @PreAuthorize("hasAuthority('SCOPE_SUPER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    private <T> T convertJsonToObject(String json, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to object", e);
        }
    }
}
