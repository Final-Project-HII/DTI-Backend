package com.hii.finalProject.image.controller;

import com.hii.finalProject.image.dto.ProductImageRequestDto;
import com.hii.finalProject.image.dto.ProductImageResponseDto;
import com.hii.finalProject.image.service.ProductImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/product/image")
public class ProductImageController {
    private final ProductImageService productImageService;
    public ProductImageController(ProductImageService productImageService){
        this.productImageService = productImageService;
    }

    @PostMapping
    public ResponseEntity<ProductImageResponseDto> createProductImage(@RequestBody MultipartFile image, ProductImageRequestDto requestDto){
        ProductImageResponseDto responseDto = productImageService.createProductImage(image, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductImage(@PathVariable Long id) {
        productImageService.deleteProductImage(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
