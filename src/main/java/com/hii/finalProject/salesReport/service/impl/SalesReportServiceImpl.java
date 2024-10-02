package com.hii.finalProject.salesReport.service.impl;

import com.hii.finalProject.order.entity.Order;
import com.hii.finalProject.order.entity.OrderStatus;
import com.hii.finalProject.order.repository.OrderRepository;

import com.hii.finalProject.salesReport.dto.SalesReportDTO;
import com.hii.finalProject.salesReport.service.SalesReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class SalesReportServiceImpl implements SalesReportService {

    private static final Logger logger = LoggerFactory.getLogger(SalesReportServiceImpl.class);

    private final OrderRepository orderRepository;

    public SalesReportServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public SalesReportDTO generateDailySalesReport(LocalDate date, OrderStatus saleStatus) {
        logger.info("Generating daily sales report for date: {} and status: {}", date, saleStatus);
        return generateSalesReport(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), saleStatus);
    }

    @Override
    public SalesReportDTO generateMonthlySalesReport(YearMonth yearMonth, OrderStatus saleStatus) {
        logger.info("Generating monthly sales report for month: {} and status: {}", yearMonth, saleStatus);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth().plusDays(1);
        return generateSalesReport(startDate.atStartOfDay(), endDate.atStartOfDay(), saleStatus);
    }

    private SalesReportDTO generateSalesReport(LocalDateTime startDateTime, LocalDateTime endDateTime, OrderStatus saleStatus) {
        List<Order> orders = orderRepository.findByCreatedAtBetweenAndStatusIn(
                startDateTime,
                endDateTime,
                List.of(saleStatus, OrderStatus.delivered)
        );

        logger.info("Found {} orders for the given criteria", orders.size());

        SalesReportDTO report = new SalesReportDTO();
        report.setStartDate(startDateTime.toLocalDate());
        report.setEndDate(endDateTime.toLocalDate().minusDays(1));
        report.setTotalOrders((long) orders.size());
        report.setTotalRevenue(calculateTotalRevenue(orders));
        report.setAverageOrderValue(calculateAverageOrderValue(orders));
        report.setTotalProductsSold(calculateTotalProductsSold(orders));
        report.setTopSellingProduct(findTopSellingProduct(orders));
        report.setTopPerformingWarehouse(findTopPerformingWarehouse(orders));

        logger.info("Generated report: {}", report);

        return report;
    }

    private BigDecimal calculateTotalRevenue(List<Order> orders) {
        return orders.stream()
                .map(Order::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateAverageOrderValue(List<Order> orders) {
        if (orders.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalRevenue = calculateTotalRevenue(orders);
        return totalRevenue.divide(BigDecimal.valueOf(orders.size()), 2, BigDecimal.ROUND_HALF_UP);
    }

    private Long calculateTotalProductsSold(List<Order> orders) {
        return orders.stream()
                .mapToLong(Order::getTotalQuantity)
                .sum();
    }

    private String findTopSellingProduct(List<Order> orders) {
        // Implementation depends on your Order and OrderItem structure
        // This is a placeholder
        return "Top Product";
    }

    private String findTopPerformingWarehouse(List<Order> orders) {
        // Implementation depends on your Order and Warehouse structure
        // This is a placeholder
        return "Top Warehouse";
    }
}