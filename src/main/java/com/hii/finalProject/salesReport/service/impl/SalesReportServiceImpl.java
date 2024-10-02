package com.hii.finalProject.salesReport.service.impl;

import com.hii.finalProject.order.entity.OrderStatus;
import com.hii.finalProject.order.repository.OrderRepository;
import com.hii.finalProject.salesReport.dto.SalesReportDTO;
import com.hii.finalProject.salesReport.service.SalesReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class SalesReportServiceImpl implements SalesReportService {

    private OrderRepository orderRepository;
    private JdbcTemplate jdbcTemplate;

    // Daily sales report using JPQL query from the repository
    @Override
    public Page<SalesReportDTO> getDailySalesReport(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        List<OrderStatus> completedStatuses = Arrays.asList(OrderStatus.delivered, OrderStatus.shipped);
        return orderRepository.getDailySalesReport(startDate, endDate, completedStatuses, pageable);
    }

    // Implementing the daily sales report for LocalDate (if needed)
    @Override
    public Page<SalesReportDTO> getDailySalesReport(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();  // Include the entire end date
        return getDailySalesReport(startDateTime, endDateTime, pageable);
    }

    // Overall sales report using JdbcTemplate
    @Override
    public SalesReportDTO getOverallSalesReport(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COUNT(*) as order_count, COALESCE(SUM(final_amount), 0) as total_revenue " +
                "FROM developmentfp.orders " + // Adding schema reference here
                "WHERE created_at >= ? AND created_at < ?";

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                long orderCount = rs.getLong("order_count");
                BigDecimal totalRevenue = rs.getBigDecimal("total_revenue");

                return new SalesReportDTO(orderCount, totalRevenue);
            }, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());  // Include orders until the end date
        } catch (Exception e) {
            throw new RuntimeException("Error generating sales report: " + e.getMessage(), e);
        }
    }

}