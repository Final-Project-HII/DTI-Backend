package com.hii.finalProject.products.dto;

import com.hii.finalProject.image.dto.ProductImageRequestDto;
import com.hii.finalProject.image.dto.ProductImageResponseDto;
import com.hii.finalProject.image.entity.ProductImage;
import com.hii.finalProject.products.entity.Product;
import com.hii.finalProject.stock.dto.StockDtoProductResponse;
import com.hii.finalProject.stock.dto.StockDtoResponse;
import com.hii.finalProject.stock.entity.Stock;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ProductListDtoResponse {
    private Long id;
    private String name;
    private String description;
    private Integer price;
    private Integer weight;
    private Long categoryId;
    private String categoryName;
    private Integer totalStock;
    private List<ProductImageResponseDto> productImages;
    private List<StockDtoProductResponse> stocks;
    private Instant createdAt;
    private Instant updatedAt;

    //stock
}
