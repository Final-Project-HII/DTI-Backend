package com.hii.finalProject.salesReport.service;

import com.hii.finalProject.salesReport.dto.SalesReportDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface SalesReportService {
    Page<SalesReportDTO> getDailySalesReport(LocalDate startDate, LocalDate endDate, Pageable pageable);
    SalesReportDTO getOverallSalesReport(LocalDate startDate, LocalDate endDate);
}