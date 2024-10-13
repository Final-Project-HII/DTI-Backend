package com.hii.finalProject.stockMutation.dto;

import lombok.Data;

import java.util.List;

@Data
public class StockReportDto {
    private StockSummaryReportDto summary;
    private List<StockMutationJournalDto> details;
}
