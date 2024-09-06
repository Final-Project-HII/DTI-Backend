package com.hii.finalProject.stock.controller;

import com.hii.finalProject.response.Response;
import com.hii.finalProject.stock.dto.StockDtoRequest;
import com.hii.finalProject.stock.dto.StockDtoResponse;
import com.hii.finalProject.stock.entity.Stock;
import com.hii.finalProject.stock.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {


    private StockService stockService;
    public StockController(StockService stockService){
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<Response<StockDtoResponse>> createStock(@RequestBody StockDtoRequest stockDtoRequest) {
        StockDtoResponse createdStock = stockService.createStock(stockDtoRequest);
        return Response.successfulResponse("stock created successfully", createdStock);
    }

//    @GetMapping("/warehouse")
//    public ResponseEntity<List<Stock>> getStocksByWarehouse(@RequestParam String email) {
//        List<Stock> stocks = stockService.findByWarehouse(email);
//        return new ResponseEntity<>(stocks, HttpStatus.OK);
//    }

    @GetMapping
    public ResponseEntity<Response<List<StockDtoResponse>>> getAllStocks() {
        List<StockDtoResponse> stocks = stockService.getAllStock();
        return Response.successfulResponse("Stocks successfully fetched", stocks);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteStock(@PathVariable Long id) {
        stockService.deleteStock(id);
        return Response.successfulResponse("Stock deleted successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<StockDtoResponse>> updateStock(@PathVariable Long id, @RequestBody StockDtoRequest stockDtoRequest) {
        StockDtoResponse updatedStock = stockService.updateStock(id, stockDtoRequest);
        return Response.successfulResponse("Stock updated successfully", updatedStock);
    }
}