package com.hii.finalProject.stockMutation.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.time.YearMonth;
import java.util.List;

@Data
public class StockSummaryReportDto {
    private YearMonth month;
    private Long warehouseId;
    private String warehouseName;
    private Page<ProductSummaryDto> productSummaries;
    private long totalAddition;
    private long totalReduction;
    private long endingStock;
}