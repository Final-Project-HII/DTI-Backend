package com.hii.finalProject.stockMutation.dto;

//import com.hii.finalProject.stockMutation.entity.StockMutationStatus;
import com.hii.finalProject.image.dto.ProductImageResponseDto;
import com.hii.finalProject.image.entity.ProductImage;
import com.hii.finalProject.stockMutation.entity.MutationType;
import com.hii.finalProject.stockMutation.entity.StockMutationStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
//    private String status;
    private MutationType mutationType;
    private String remarks;
    private String requestedBy;
    private String handledBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}