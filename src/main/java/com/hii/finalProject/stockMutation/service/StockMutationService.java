package com.hii.finalProject.stockMutation.service;


//import com.hii.finalProject.stockMutation.dto.StockDetailReportDto;
import com.hii.finalProject.stockMutation.dto.*;
import com.hii.finalProject.stockMutation.entity.StockMutationStatus;
import com.hii.finalProject.stockMutationJournal.entity.StockMutationJournal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public interface StockMutationService {
//    StockMutationResponseDto createManualMutation(StockMutationRequestDto requestDto, String requestedBy);
//    StockMutationResponseDto processMutation(Long mutationId, String handledBy);
//    List<StockMutationResponseDto> getPendingMutations();
//    StockMutationResponseDto getMutationById(Long mutationId);
StockMutationResponseDto createManualMutation(StockMutationRequestDto request, String username);
StockMutationResponseDto processMutation(StockMutationProcessDto processDto, String handledBy);
StockMutationResponseDto getMutationById(Long mutationId);
//List<StockMutationResponseDto> getAllStock();
List<StockMutationResponseDto> getMutationByUser(String userEmail);
//Page<StockMutationResponseDto> getStockMutations(Specification<StockMutation> spec, Pageable pageable);
Page<StockMutationResponseDto> getAllStockMutations(
        String email,
        Long originWarehouseId,
        Long destinationWarehouseId,
        String productName,
//        String status,
        StockMutationStatus status,
        LocalDateTime createdAtStart,
        LocalDateTime createdAtEnd,
        LocalDateTime updatedAtStart,
        LocalDateTime updatedAtEnd,
        String sortBy,
        String sortDirection,
        Pageable pageable
);
Page<StockMutationJournalDto> getStockMutationJournals(
            Long warehouseId,
            String productName,
            StockMutationJournal.MutationType mutationType,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String email,
            Pageable pageable
    );
StockReportDto getStockReport(Long warehouseId, YearMonth month, Pageable pageable);
}
