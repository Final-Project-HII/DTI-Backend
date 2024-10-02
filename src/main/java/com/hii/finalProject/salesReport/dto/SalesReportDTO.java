package com.hii.finalProject.salesReport.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SalesReportDTO {
    @JsonProperty("total_orders")
    private final long totalOrders;

    @JsonProperty("total_revenue")
    private final BigDecimal totalRevenue;

    public SalesReportDTO(long totalOrders, BigDecimal totalRevenue) {
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
    }

    // Getters
    public long getTotalOrders() {
        return totalOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
}