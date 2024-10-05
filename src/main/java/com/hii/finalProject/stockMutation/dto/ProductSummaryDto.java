package com.hii.finalProject.stockMutation.dto;

import lombok.Data;

@Data
public class ProductSummaryDto {
    private Long productId;
    private String productName;
    private long totalAddition;
    private long totalReduction;
    private long endingStock;
}
