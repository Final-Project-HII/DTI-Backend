package com.hii.finalProject.salesReport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySales {
    private String month;
    private double totalRevenue;
    private int totalOrders;
}
