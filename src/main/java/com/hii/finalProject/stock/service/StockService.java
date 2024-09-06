package com.hii.finalProject.stock.service;

import com.hii.finalProject.stock.dto.StockDtoRequest;
import com.hii.finalProject.stock.dto.StockDtoResponse;
import com.hii.finalProject.stock.entity.Stock;

import java.util.List;

public interface StockService {
    StockDtoResponse createStock(StockDtoRequest stockDtoRequest);
//    List<Stock> findByWarehouse(String email);
    List<StockDtoResponse> getAllStock();
    void deleteStock(Long id);
    StockDtoResponse updateStock(Long id, StockDtoRequest stockDtoRequest);
}
