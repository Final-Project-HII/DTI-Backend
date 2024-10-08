package com.hii.finalProject.salesReport.dto;

import lombok.Data;
import org.springframework.data.relational.core.sql.In;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
public class ProductSalesDto {
    private YearMonth month;
    private Long warehouseId;
    private String warehouseName;
    private Long productId;
    private String productName;
    private Long categoryId;
    private String categoryName;
    private BigDecimal totalGrossRevenue;
    private int totalQuantity;
    private BigDecimal productPrice;
}
