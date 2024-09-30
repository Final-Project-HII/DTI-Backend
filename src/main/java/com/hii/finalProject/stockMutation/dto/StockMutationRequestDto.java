package com.hii.finalProject.stockMutation.dto;

import lombok.Data;

@Data
public class StockMutationRequestDto {
    private Long productId;
    private Long originWarehouseId;
    private Long destinationWarehouseId;
    private Integer quantity;
}