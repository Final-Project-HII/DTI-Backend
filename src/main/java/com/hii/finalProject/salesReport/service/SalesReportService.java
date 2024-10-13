package com.hii.finalProject.salesReport.service;


import com.hii.finalProject.order.entity.OrderStatus;

import com.hii.finalProject.salesReport.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface SalesReportService {
    SalesReportDTO generateDailySalesReport(LocalDate date, OrderStatus saleStatus);
    SalesReportDTO generateMonthlySalesReport(YearMonth yearMonth, OrderStatus saleStatus);
    List<MonthlySales> generateYearlySalesReport(int year);
    //indah
    Page<OrderDTO> getAllOrders(Long warehouseId, String customerName, OrderStatus status,
                                YearMonth month, Long productId, Long categoryId,
                                Pageable pageable, String email);

    Page<SalesSummaryDto> getSalesSummary(Long warehouseId, YearMonth month, Pageable pageable);
    Page<CategorySalesDto> getCategorySales(Long warehouseId, YearMonth month, Pageable pageable);
    Page<ProductSalesDto> getProductSales(Long warehouseId, YearMonth month, Pageable pageable);
}