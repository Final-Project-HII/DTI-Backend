package com.hii.finalProject.salesReport.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
public class CategorySalesDto {
    private YearMonth month;
    private Long warehouseId;
    private String warehouseName;
    private Long categoryId;
    private String categoryName;
    private BigDecimal totalGrossRevenue;
    private int totalOrders;
}