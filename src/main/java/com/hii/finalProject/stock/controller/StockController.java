package com.hii.finalProject.stock.controller;

import com.hii.finalProject.auth.helpers.Claims;
import com.hii.finalProject.response.Response;
import com.hii.finalProject.stock.dto.StockDtoRequest;
import com.hii.finalProject.stock.dto.StockDtoResponse;
import com.hii.finalProject.stock.entity.Stock;
import com.hii.finalProject.stock.service.StockService;
import com.hii.finalProject.stockMutation.dto.StockMutationRequestDto;
import com.hii.finalProject.stockMutation.dto.StockMutationResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {


    private StockService stockService;
    public StockController(StockService stockService){
        this.stockService = stockService;
    }

//    @PreAuthorize("hasAuthority('SCOPE_SUPER') or hasAuthority('SCOPE_ADMIN')")
    @PostMapping
    public ResponseEntity<Response<StockDtoResponse>> createStock(@RequestBody StockDtoRequest stockDtoRequest) {
//        var claims = Claims.getClaimsFromJwt();
//        var email = (String) claims.get("sub");
        StockDtoResponse createdStock = stockService.createStock(stockDtoRequest);
        return Response.successfulResponse("stock created successfully", createdStock);
    }

//    @GetMapping("/warehouse")
//    public ResponseEntity<List<Stock>> getStocksByWarehouse(@RequestParam String email) {
//        List<Stock> stocks = stockService.findByWarehouse(email);
//        return new ResponseEntity<>(stocks, HttpStatus.OK);
//    }

//    @PreAuthorize("hasAuthority('SCOPE_SUPER') or hasAuthority('SCOPE_ADMIN')")
    @GetMapping
    public ResponseEntity<Response<List<StockDtoResponse>>> getAllStocks() {
//        var claims = Claims.getClaimsFromJwt();
//        var email = (String) claims.get("sub");
        List<StockDtoResponse> stocks = stockService.getAllStock();
        return Response.successfulResponse("Stocks successfully fetched", stocks);
    }


//    @PreAuthorize("hasAuthority('SCOPE_SUPER') or hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteStock(@PathVariable Long id) {
//        var claims = Claims.getClaimsFromJwt();
//        var email = (String) claims.get("sub");
        stockService.deleteStock(id);
        return Response.successfulResponse("Stock deleted successfully");
    }

//    @PreAuthorize("hasAuthority('SCOPE_SUPER') or hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Response<StockDtoResponse>> updateStock(@PathVariable Long id, @RequestBody StockDtoRequest stockDtoRequest) {
//        var claims = Claims.getClaimsFromJwt();
//        var email = (String) claims.get("sub");
        StockDtoResponse updatedStock = stockService.updateStock(id, stockDtoRequest);
        return Response.successfulResponse("Stock updated successfully", updatedStock);
    }
}