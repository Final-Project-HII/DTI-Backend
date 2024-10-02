package com.hii.finalProject.salesReport.controller;

import com.hii.finalProject.order.entity.OrderStatus;
import com.hii.finalProject.salesReport.dto.SalesReportDTO;
import com.hii.finalProject.salesReport.service.SalesReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/reports")
public class SalesReportController {

    private final SalesReportService salesReportService;

    public SalesReportController(SalesReportService salesReportService) {
        this.salesReportService = salesReportService;
    }

    @GetMapping("/sales")
    public ResponseEntity<SalesReportDTO> getDailySalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "SHIPPED") OrderStatus saleStatus) {
        SalesReportDTO report = salesReportService.generateDailySalesReport(date, saleStatus);
        return ResponseEntity.ok(report);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleDateFormatException(MethodArgumentTypeMismatchException ex) {
        if (ex.getCause() instanceof DateTimeParseException) {
            return ResponseEntity.badRequest().body("Invalid date format. Please use ISO date format (YYYY-MM-DD).");
        }
        return ResponseEntity.badRequest().body("Invalid argument: " + ex.getMessage());
    }
}