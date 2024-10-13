package com.hii.finalProject.stockMutation.dto;

import com.hii.finalProject.stockMutation.entity.MutationType;
import com.hii.finalProject.stockMutation.entity.StockMutationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockMutationResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private Long originWarehouseId;
    private String originWarehouseName;
    private Long destinationWarehouseId;
    private String destinationWarehouseName;
    private Integer quantity;
    private StockMutationStatus status;
    private Long loginWarehouseId;
    private MutationType mutationType;
    private String remarks;
    private String requestedBy;
    private String handledBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}