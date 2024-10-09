package com.hii.finalProject.stock.controller;

import com.hii.finalProject.auth.helpers.Claims;
import com.hii.finalProject.response.Response;
import com.hii.finalProject.stock.dto.StockDtoRequest;
import com.hii.finalProject.stock.dto.StockDtoResponse;
import com.hii.finalProject.stock.service.StockService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        StockDtoResponse createdStock = stockService.createStock(stockDtoRequest, email);
        return Response.successfulResponse("stock created successfully", createdStock);
    }

    @PreAuthorize("hasAuthority('SCOPE_SUPER') or hasAuthority('SCOPE_ADMIN')")
    @GetMapping
    public ResponseEntity<Response<Page<StockDtoResponse>>> getAllStocks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) Integer minQuantity,
            @RequestParam(required = false) Integer maxQuantity,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            Pageable pageable) {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");

        Page<StockDtoResponse> stocks = stockService.getAllStock(
                search, productId, warehouseId, categoryName,
                minQuantity, maxQuantity, minPrice, maxPrice,
                sortBy, sortDirection, pageable, email);

        return Response.successfulResponse("Stocks successfully fetched", stocks);
    }
    @PreAuthorize("hasAuthority('SCOPE_SUPER') or hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteStock(@PathVariable Long id) {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        stockService.deleteStock(id);
        return Response.successfulResponse("Stock deleted successfully");
    }
    @PreAuthorize("hasAuthority('SCOPE_SUPER') or hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Response<StockDtoResponse>> updateStock(@PathVariable Long id, @RequestBody StockDtoRequest stockDtoRequest) {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        StockDtoResponse updatedStock = stockService.updateStock(id, stockDtoRequest, email);
        return Response.successfulResponse("Stock updated successfully", updatedStock);
    }
}