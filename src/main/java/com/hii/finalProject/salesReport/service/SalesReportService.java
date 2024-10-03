package com.hii.finalProject.salesReport.service;

import com.hii.finalProject.order.entity.OrderStatus;

import com.hii.finalProject.salesReport.dto.MonthlySales;
import com.hii.finalProject.salesReport.dto.SalesReportDTO;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface SalesReportService {
    SalesReportDTO generateDailySalesReport(LocalDate date, OrderStatus saleStatus);
    SalesReportDTO generateMonthlySalesReport(YearMonth yearMonth, OrderStatus saleStatus);
    List<MonthlySales> generateYearlySalesReport(int year);
}