package com.hii.finalProject.salesReport.controller;

import com.hii.finalProject.order.entity.OrderStatus;

import com.hii.finalProject.salesReport.dto.SalesReportDTO;
import com.hii.finalProject.salesReport.service.SalesReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/reports")
public class SalesReportController {

    private final SalesReportService salesReportService;

    public SalesReportController(SalesReportService salesReportService) {
        this.salesReportService = salesReportService;
    }

    @GetMapping("/sales/daily")
    public ResponseEntity<SalesReportDTO> getDailySalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "SHIPPED") OrderStatus saleStatus) {
        SalesReportDTO report = salesReportService.generateDailySalesReport(date, saleStatus);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/sales/monthly")
    public ResponseEntity<SalesReportDTO> getMonthlySalesReport(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
            @RequestParam(defaultValue = "SHIPPED") OrderStatus saleStatus) {
        SalesReportDTO report = salesReportService.generateMonthlySalesReport(yearMonth, saleStatus);
        return ResponseEntity.ok(report);
    }
}