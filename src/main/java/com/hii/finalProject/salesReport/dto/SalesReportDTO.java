package com.hii.finalProject.salesReport.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SalesReportDTO {
    private LocalDate date;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Long totalProductsSold;
    private BigDecimal averageOrderValue;

    public SalesReportDTO(LocalDate date, Long totalOrders, BigDecimal totalRevenue, Long totalProductsSold, BigDecimal averageOrderValue) {
        this.date = date;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
        this.totalProductsSold = totalProductsSold;
        this.averageOrderValue = averageOrderValue;
    }
}