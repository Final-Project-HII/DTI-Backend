package com.hii.finalProject.salesReport.service.impl;

import com.hii.finalProject.order.entity.OrderStatus;
import com.hii.finalProject.order.repository.OrderRepository;
import com.hii.finalProject.salesReport.dto.SalesReportDTO;
import com.hii.finalProject.salesReport.service.SalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Page<SalesReportDTO> getDailySalesReport(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        List<OrderStatus> completedStatuses = Arrays.asList(OrderStatus.delivered, OrderStatus.shipped);
        return orderRepository.getDailySalesReport(startDate, endDate, completedStatuses, pageable);
    }

    @Override
    public Page<SalesReportDTO> getDailySalesReport(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return null;
    }

    public SalesReportDTO getOverallSalesReport(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT COUNT(*) as order_count, COALESCE(SUM(final_amount), 0) as total_revenue
            FROM orders
            WHERE created_at::date BETWEEN ?::date AND ?::date
        """;

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                long orderCount = rs.getLong("order_count");
                BigDecimal totalRevenue = rs.getBigDecimal("total_revenue");
                return new SalesReportDTO(orderCount, totalRevenue);
            }, startDate, endDate);
        } catch (Exception e) {
            throw e;
        }
    }

}