package com.hii.finalProject.salesReport.service.impl;

import com.hii.finalProject.order.entity.OrderStatus;
import com.hii.finalProject.order.repository.OrderRepository;
import com.hii.finalProject.salesReport.dto.SalesReportDTO;
import com.hii.finalProject.salesReport.service.SalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Service
public class SalesReportServiceImpl implements SalesReportService {

    private final OrderRepository orderRepository;
    private final List<OrderStatus> SOLD_STATUSES = Arrays.asList(OrderStatus.shipped, OrderStatus.delivered);

    public SalesReportServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Page<SalesReportDTO> getDailySalesReport(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return orderRepository.getDailySalesReport(startDateTime, endDateTime, SOLD_STATUSES, pageable);
    }

    @Override
    public SalesReportDTO getOverallSalesReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return orderRepository.getOverallSalesReport(startDateTime, endDateTime, SOLD_STATUSES);
    }
}