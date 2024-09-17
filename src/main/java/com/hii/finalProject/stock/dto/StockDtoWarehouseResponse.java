package com.hii.finalProject.stock.dto;

import lombok.Data;

@Data
public class StockDtoWarehouseResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Long categoryId;
    private String categoryName;
}
