package com.hii.finalProject.stockMutation.dto;

import com.hii.finalProject.stockMutation.entity.StockMutationStatus;
import lombok.Data;

@Data
public class StockMutationProcessDto {
    private Long id;
    private String remarks;
    private StockMutationStatus status;
}