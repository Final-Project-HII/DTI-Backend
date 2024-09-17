package com.hii.finalProject.stockMutation.dto;

import lombok.Data;

@Data
public class StockMutationRequestDto {
    private Long productId;
    private Long originWarehouseId;
    private Long destinationWarehouseId;
    private Integer quantity;
//    private String status;

//    private String status;
//    private String mutationType;
//    private String remarks;
//    private String requestedBy;
//    private Integer handledBy;
}