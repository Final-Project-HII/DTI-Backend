package com.hii.finalProject.salesReport.controller;

import com.hii.finalProject.salesReport.dto.SalesReportDTO;
import com.hii.finalProject.salesReport.service.SalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/sales-report")
//@PreAuthorize("hasRole('ADMIN')")
public class SalesReportController {

    @Autowired
    private SalesReportService salesReportService;

    @GetMapping("/daily")
    public ResponseEntity<Page<SalesReportDTO>> getDailySalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size) {
        Page<SalesReportDTO> report = salesReportService.getDailySalesReport(startDate, LocalDate.from(endDate), PageRequest.of(page, size));
        return ResponseEntity.ok(report);
    }

    @GetMapping("/overall")
    public ResponseEntity<SalesReportDTO> getOverallSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        SalesReportDTO report = salesReportService.getOverallSalesReport(startDate, LocalDate.from(endDate));
        return ResponseEntity.ok(report);
    }
}