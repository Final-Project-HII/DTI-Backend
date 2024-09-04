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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService){
        this.productService = productService;
    }
    @PostMapping("/create")
    public ResponseEntity<ProductListDtoResponse> createProduct(
            @ModelAttribute NewProductRequestDto productRequestDTO,
            @RequestParam("productImages") List<MultipartFile> productImages) {
        ProductListDtoResponse createdProduct = productService.createProduct(productRequestDTO, productImages);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }
    private boolean isValidImageExtension(String fileName) {
        String[] validExtensions = {".jpg", ".jpeg", ".png", ".gif"};
        return Arrays.stream(validExtensions).anyMatch(ext -> fileName.toLowerCase().endsWith(ext));
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
    public ResponseEntity<ProductListDtoResponse> updateProduct(
            @PathVariable Long id,
            @RequestPart(value = "product") String updateProductRequestDtoJson,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) {
        // Convert JSON string to UpdateProductRequestDto object
        UpdateProductRequestDto updateProductRequestDto = convertJsonToObject(updateProductRequestDtoJson, UpdateProductRequestDto.class);
        ProductListDtoResponse updatedProduct = productService.updateProduct(id, updateProductRequestDto, newImages);
        return ResponseEntity.ok(updatedProduct);
    }
    @DeleteMapping("/delete/{id}")
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

//DENGAN CUSTOM EXCEPTION
//package com.hii.finalProject.products.controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hii.finalProject.products.dto.NewProductRequestDto;
//import com.hii.finalProject.products.dto.ProductListDtoResponse;
//import com.hii.finalProject.products.dto.UpdateProductRequestDto;
//import com.hii.finalProject.products.service.ProductService;
//import com.hii.finalProject.exceptions.DataNotFoundException;
//import com.hii.finalProject.response.Response;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.Arrays;
//import java.util.List;
//
//@RestController
//@RequestMapping("/product")
//public class ProductController {
//    private final ProductService productService;
//
//    public ProductController(ProductService productService) {
//        this.productService = productService;
//    }
//
//    @PostMapping("/create")
//    public ResponseEntity<Response<ProductListDtoResponse>> createProduct(
//            @ModelAttribute NewProductRequestDto productRequestDTO,
//            @RequestParam("productImages") List<MultipartFile> productImages) {
//        try {
//            ProductListDtoResponse createdProduct = productService.createProduct(productRequestDTO, productImages);
//            return Response.successfulResponse("Product created successfully", createdProduct);
//        } catch (Exception e) {
//            return Response.failedResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error creating product: " + e.getMessage());
//        }
//    }
//
//    @GetMapping
//    public ResponseEntity<Response<Page<ProductListDtoResponse>>> getAllProducts(
//            @RequestParam(required = false) String search,
//            @RequestParam(required = false) String categoryName,
//            @RequestParam(defaultValue = "id") String sortBy,
//            @RequestParam(defaultValue = "asc") String sortDirection,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        try {
//            PageRequest pageable = PageRequest.of(page, size);
//            Page<ProductListDtoResponse> products = productService.getAllProducts(search, categoryName, sortBy, sortDirection, pageable);
//            return Response.successfulResponseWithPage(HttpStatus.OK.value(), "Products retrieved successfully", products, products.getTotalPages(), products.getTotalElements(), page);
//        } catch (Exception e) {
//            return Response.failedResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving products: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Response<ProductListDtoResponse>> getProductById(@PathVariable Long id) {
//        try {
//            ProductListDtoResponse product = productService.getProductById(id);
//            return Response.successfulResponse("Product retrieved successfully", product);
//        } catch (DataNotFoundException e) {
//            return Response.failedResponse(e.getHttpStatus().value(), e.getMessage());
//        } catch (Exception e) {
//            return Response.failedResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving product: " + e.getMessage());
//        }
//    }
//
//    @PutMapping(value = "/update/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseEntity<Response<ProductListDtoResponse>> updateProduct(
//            @PathVariable Long id,
//            @RequestPart(value = "product") String updateProductRequestDtoJson,
//            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) {
//        try {
//            UpdateProductRequestDto updateProductRequestDto = convertJsonToObject(updateProductRequestDtoJson, UpdateProductRequestDto.class);
//            ProductListDtoResponse updatedProduct = productService.updateProduct(id, updateProductRequestDto, newImages);
//            return Response.successfulResponse("Product updated successfully", updatedProduct);
//        } catch (DataNotFoundException e) {
//            return Response.failedResponse(e.getHttpStatus().value(), e.getMessage());
//        } catch (Exception e) {
//            return Response.failedResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error updating product: " + e.getMessage());
//        }
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<Response<Void>> deleteProduct(@PathVariable Long id) {
//        try {
//            productService.deleteProduct(id);
//            return Response.successfulResponse("Product deleted successfully");
//        } catch (DataNotFoundException e) {
//            return Response.failedResponse(e.getHttpStatus().value(), e.getMessage());
//        } catch (Exception e) {
//            return Response.failedResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting product: " + e.getMessage());
//        }
//    }
//
//    private <T> T convertJsonToObject(String json, Class<T> clazz) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            return objectMapper.readValue(json, clazz);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Error converting JSON to object", e);
//        }
//    }
//
//    private boolean isValidImageExtension(String fileName) {
//        String[] validExtensions = {".jpg", ".jpeg", ".png", ".gif"};
//        return Arrays.stream(validExtensions).anyMatch(ext -> fileName.toLowerCase().endsWith(ext));
//    }
//}
