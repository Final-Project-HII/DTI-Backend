package com.hii.finalProject.salesReport.service.impl;

import com.hii.finalProject.order.entity.Order;
import com.hii.finalProject.order.entity.OrderStatus;
import com.hii.finalProject.order.repository.OrderRepository;
import com.hii.finalProject.salesReport.dto.SalesReportDTO;
import com.hii.finalProject.salesReport.service.SalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SalesReportServiceImpl implements SalesReportService {

    private final OrderRepository orderRepository;

    @Autowired
    public SalesReportServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public SalesReportDTO generateDailySalesReport(LocalDate date, OrderStatus saleStatus) {
        List<Order> orders = orderRepository.findByCreatedAtBetweenAndStatusIn(
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay(),
                List.of(saleStatus, OrderStatus.delivered)
        );

        SalesReportDTO report = new SalesReportDTO();
        report.setDate(date);
        report.setTotalOrders((long) orders.size());
        report.setTotalRevenue(calculateTotalRevenue(orders));
        report.setAverageOrderValue(calculateAverageOrderValue(orders));
        report.setTotalProductsSold(calculateTotalProductsSold(orders));
        report.setTopSellingProduct(findTopSellingProduct(orders));
        report.setTopPerformingWarehouse(findTopPerformingWarehouse(orders));

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
        return totalRevenue.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP);
    }

    private Long calculateTotalProductsSold(List<Order> orders) {
        return orders.stream()
                .mapToLong(Order::getTotalQuantity)
                .sum();
    }

    private String findTopSellingProduct(List<Order> orders) {
        Map<String, Long> productSales = orders.stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getProduct().getName(),
                        Collectors.summingLong(item -> item.getQuantity())
                ));

        return productSales.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    private String findTopPerformingWarehouse(List<Order> orders) {
        Map<String, Long> warehouseSales = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getWarehouse().getName(),
                        Collectors.counting()
                ));

        return warehouseSales.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }
}