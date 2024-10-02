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
        logger.info("Generating sales report for date: {} and status: {}", date, saleStatus);

        List<Order> orders = orderRepository.findByCreatedAtBetweenAndStatusIn(
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay(),
                List.of(saleStatus, OrderStatus.delivered)
        );

        logger.info("Found {} orders for the given criteria", orders.size());

        SalesReportDTO report = new SalesReportDTO();
        report.setDate(date);
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