package com.hii.finalProject.stockMutation.service;


import com.hii.finalProject.stock.dto.StockDtoResponse;
import com.hii.finalProject.stock.entity.Stock;
import com.hii.finalProject.stockMutation.dto.StockMutationProcessDto;
import com.hii.finalProject.stockMutation.dto.StockMutationRequestDto;
import com.hii.finalProject.stockMutation.dto.StockMutationResponseDto;
import com.hii.finalProject.stockMutation.entity.StockMutation;
import com.hii.finalProject.stockMutation.entity.StockMutationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
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
        String status,
        LocalDateTime createdAtStart,
        LocalDateTime createdAtEnd,
        LocalDateTime updatedAtStart,
        LocalDateTime updatedAtEnd,
        String sortBy,
        String sortDirection,
        Pageable pageable
);

}
