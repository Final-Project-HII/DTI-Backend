package com.hii.finalProject.productImage.controller;

import com.hii.finalProject.productImage.dto.ProductImageDTO;
import com.hii.finalProject.productImage.service.ProductImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-images")
public class ProductImageController {
    private final ProductImageService productImageService;

    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductImageDTO>> getImagesByProductId(@PathVariable Integer productId) {
        return ResponseEntity.ok(productImageService.getImagesByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<ProductImageDTO> addProductImage(@RequestBody ProductImageDTO productImageDTO) {
        return ResponseEntity.ok(productImageService.addProductImage(productImageDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductImage(@PathVariable Long id) {
        productImageService.deleteProductImage(id);
        return ResponseEntity.noContent().build();
    }
}