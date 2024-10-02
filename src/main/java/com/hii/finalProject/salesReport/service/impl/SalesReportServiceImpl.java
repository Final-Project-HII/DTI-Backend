package com.hii.finalProject.salesReport.service;

import com.hii.finalProject.order.entity.OrderStatus;
import com.hii.finalProject.order.repository.OrderRepository;
import com.hii.finalProject.salesReport.dto.SalesReportDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class SalesReportServiceImpl implements SalesReportService {

    @Autowired
    private OrderRepository orderRepository;

    public Page<SalesReportDTO> getDailySalesReport(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        List<OrderStatus> completedStatuses = Arrays.asList(OrderStatus.delivered, OrderStatus.shipped);
        return orderRepository.getDailySalesReport(startDate, endDate, completedStatuses, pageable);
    }

    public SalesReportDTO getOverallSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<OrderStatus> completedStatuses = Arrays.asList(OrderStatus.delivered, OrderStatus.shipped);
        return orderRepository.getOverallSalesReport(startDate, endDate, completedStatuses);
    }

    @Override
    public Page<SalesReportDTO> getDailySalesReport(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return null;
    }

    @Override
    public SalesReportDTO getOverallSalesReport(LocalDate startDate, LocalDate endDate) {
        return null;
    }
}