package com.hii.finalProject.salesReport.service.impl;

import com.hii.finalProject.order.entity.Order;
import com.hii.finalProject.order.entity.OrderStatus;
import com.hii.finalProject.order.repository.OrderRepository;

import com.hii.finalProject.salesReport.dto.MonthlySales;
import com.hii.finalProject.salesReport.dto.SalesReportDTO;
import com.hii.finalProject.salesReport.service.SalesReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;

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
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        return generateSalesReport(startOfDay, endOfDay, saleStatus);
    }

    @Override
    public SalesReportDTO generateMonthlySalesReport(YearMonth yearMonth, OrderStatus saleStatus) {
        logger.info("Generating monthly sales report for month: {} and status: {}", yearMonth, saleStatus);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().plusDays(1).atStartOfDay();
        return generateSalesReport(startOfMonth, endOfMonth, saleStatus);
    }

    private SalesReportDTO generateSalesReport(LocalDateTime start, LocalDateTime end, OrderStatus saleStatus) {
        List<Order> orders = orderRepository.findByCreatedAtBetweenAndStatusIn(
                start,
                end,
                List.of(saleStatus, OrderStatus.delivered)
        );

        logger.info("Found {} orders for the given criteria", orders.size());

        SalesReportDTO report = new SalesReportDTO();
        report.setStartDate(start.toLocalDate());
        report.setEndDate(end.toLocalDate().minusDays(1));
        report.setTotalOrders((long) orders.size());
        report.setTotalRevenue(calculateTotalRevenue(orders));
        report.setAverageOrderValue(calculateAverageOrderValue(orders));
        report.setTotalProductsSold(calculateTotalProductsSold(orders));
//        report.setTopSellingProduct(findTopSellingProduct(orders));
//        report.setTopPerformingWarehouse(findTopPerformingWarehouse(orders));

        logger.info("Generated report: {}", report);

        return report;
    }

    @Override
    public List<MonthlySales> generateYearlySalesReport(int year) {
        logger.info("Generating yearly sales report for year: {}", year);
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Order> orders = orderRepository.findByCreatedAtBetween(
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX)
        );

        Map<Month, MonthlySales> monthlySalesMap = new EnumMap<>(Month.class);

        for (Order order : orders) {
            Month month = order.getCreatedAt().getMonth();
            MonthlySales monthlySales = monthlySalesMap.computeIfAbsent(month,
                    k -> new MonthlySales(month.name(), 0, 0));
            monthlySales.setTotalRevenue(monthlySales.getTotalRevenue() + order.getFinalAmount().doubleValue());
            monthlySales.setTotalOrders(monthlySales.getTotalOrders() + 1);
        }

        List<MonthlySales> result = new ArrayList<>(monthlySalesMap.values());
        result.sort(Comparator.comparing(ms -> Month.valueOf(ms.getMonth())));

        logger.info("Generated yearly report with {} months of data", result.size());

        return result;
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

//    private String findTopSellingProduct(List<Order> orders) {
//        Map<String, Long> productSales = orders.stream()
//                .flatMap(order -> order.getItems().stream())
//                .collect(Collectors.groupingBy(
//                        item -> item.getProduct().getName(),
//                        Collectors.summingLong(item -> item.getQuantity())
//                ));
//
//        return productSales.entrySet().stream()
//                .max(Map.Entry.comparingByValue())
//                .map(Map.Entry::getKey)
//                .orElse("N/A");
//    }

//    private String findTopPerformingWarehouse(List<Order> orders) {
//        Map<String, Long> warehouseSales = orders.stream()
//                .collect(Collectors.groupingBy(
//                        order -> order.getWarehouse().getName(),
//                        Collectors.counting()
//                ));
//
//        return warehouseSales.entrySet().stream()
//                .max(Map.Entry.comparingByValue())
//                .map(Map.Entry::getKey)
//                .orElse("N/A");
//    }
}