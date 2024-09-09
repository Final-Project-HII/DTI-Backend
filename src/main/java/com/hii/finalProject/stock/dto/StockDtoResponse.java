package com.hii.finalProject.stock.dto;

import lombok.Data;

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
}
