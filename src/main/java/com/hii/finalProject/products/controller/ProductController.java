package com.hii.finalProject.products.controller;

import com.hii.finalProject.products.dto.NewProductRequestDto;
import com.hii.finalProject.products.dto.ProductListDtoResponse;
import com.hii.finalProject.products.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping("/create")
    public ResponseEntity<ProductListDtoResponse> createProduct(@RequestBody NewProductRequestDto productRequestDTO) {
        ProductListDtoResponse createdProduct = productService.createProduct(productRequestDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ProductListDtoResponse>> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<ProductListDtoResponse> products = productService.getAllProducts(search, categoryId, sortBy, sortDirection, pageable);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
