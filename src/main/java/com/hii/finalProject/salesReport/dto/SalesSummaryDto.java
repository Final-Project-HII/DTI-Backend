package com.hii.finalProject.salesReport.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
public class SalesSummaryDto {
    private YearMonth month;
    private Long warehouseId;
    private String warehouseName;
    private BigDecimal totalGrossRevenue;
    private int totalOrders;
}
