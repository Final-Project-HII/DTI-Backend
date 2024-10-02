package com.hii.finalProject.salesReport.service;

import com.hii.finalProject.order.entity.OrderStatus;

import com.hii.finalProject.salesReport.dto.SalesReportDTO;

import java.time.LocalDate;

public interface SalesReportService {
    SalesReportDTO generateDailySalesReport(LocalDate date, OrderStatus saleStatus);
}