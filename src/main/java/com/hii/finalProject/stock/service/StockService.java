package com.hii.finalProject.stock.service;

import com.hii.finalProject.stock.dto.StockDtoRequest;
import com.hii.finalProject.stock.dto.StockDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockService {
    StockDtoResponse createStock(StockDtoRequest stockDtoRequest, String email);
    Page<StockDtoResponse> getAllStock(
            String search,
            Long productId,
            Long warehouseId,
            String categoryName,
            Integer minQuantity,
            Integer maxQuantity,
            Double minPrice,
            Double maxPrice,
            String sortBy,
            String sortDirection,
            Pageable pageable,
            String email
    );
    void deleteStock(Long id);
    StockDtoResponse updateStock(Long id, StockDtoRequest stockDtoRequest, String email);
    void reduceStock(Long productId, Long warehouseId, int quantity);
    void returnStock(Long productId, Long warehouseId, int quantity);
}
