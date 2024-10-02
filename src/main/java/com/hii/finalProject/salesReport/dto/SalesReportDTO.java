package com.hii.finalProject.salesReport.dto;

import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SalesReportDTO {
    private LocalDate date;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Long totalProductsSold;
    private BigDecimal averageOrderValue;

    private JdbcTemplate jdbcTemplate;

    public SalesReportDTO(long totalOrders, BigDecimal totalRevenue) {
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
    }
}