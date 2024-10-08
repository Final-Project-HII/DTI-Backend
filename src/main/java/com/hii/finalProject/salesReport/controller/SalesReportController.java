package com.hii.finalProject.salesReport.controller;

import com.hii.finalProject.auth.helpers.Claims;
import com.hii.finalProject.order.dto.OrderDTO;
import com.hii.finalProject.order.entity.OrderStatus;

import com.hii.finalProject.response.Response;
import com.hii.finalProject.salesReport.dto.*;
import com.hii.finalProject.salesReport.service.SalesReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/sales/report")
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

    @GetMapping("/sales/yearly")
    public ResponseEntity<List<MonthlySales>> getYearlySalesReport(@RequestParam int year) {
        List<MonthlySales> yearlySales = salesReportService.generateYearlySalesReport(year);
        return ResponseEntity.ok(yearlySales);
    }
    //indah
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_SUPER') or hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Response<Page<OrderDTO>>> getAllOrders(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderDTO> orders = salesReportService.getAllOrders(warehouseId, customerName, status, month, productId, categoryId, pageable, email);
        return Response.successfulResponse("Orders retrieved successfully", orders);
    }
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('SCOPE_SUPER') or hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Response<Page<SalesSummaryDto>>> getSalesSummary(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<SalesSummaryDto> report = salesReportService.getSalesSummary(warehouseId, month, pageable);
        return Response.successfulResponse("Sales summary report generated successfully", report);
    }

    @GetMapping("/category")
    @PreAuthorize("hasAuthority('SCOPE_SUPER') or hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Response<Page<CategorySalesDto>>> getCategorySales(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<CategorySalesDto> report = salesReportService.getCategorySales(warehouseId, month, pageable);
        return Response.successfulResponse("Category sales report generated successfully", report);
    }

    @GetMapping("/product")
    @PreAuthorize("hasAuthority('SCOPE_SUPER') or hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Response<Page<ProductSalesDto>>> getProductSales(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<ProductSalesDto> report = salesReportService.getProductSales(warehouseId, month, pageable);
        return Response.successfulResponse("Product sales report generated successfully", report);
    }

}