package com.hii.finalProject.salesReport.service;

import com.hii.finalProject.salesReport.dto.SalesReportDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface SalesReportService {
    // Daily sales report using JPQL query from the repository
    Page<SalesReportDTO> getDailySalesReport(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<SalesReportDTO> getDailySalesReport(LocalDate startDate, LocalDate endDate, Pageable pageable);
    SalesReportDTO getOverallSalesReport(LocalDate startDate, LocalDate endDate);
}