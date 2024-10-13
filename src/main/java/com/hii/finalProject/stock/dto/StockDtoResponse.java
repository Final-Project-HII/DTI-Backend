package com.hii.finalProject.stock.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class StockDtoResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private Integer quantity;
    private Long categoryId;
    private String categoryName;
    private String productImageUrl;
    private Integer price;
    private Integer weight;
    private Instant createdAt;
    private Instant updatedAt;
    private Long loginWarehouseId;
}
