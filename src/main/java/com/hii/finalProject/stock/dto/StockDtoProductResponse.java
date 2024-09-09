package com.hii.finalProject.stock.dto;

import com.hii.finalProject.stock.entity.Stock;
import lombok.Data;

@Data
public class StockDtoProductResponse {
    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private Integer quantity;

    public static StockDtoProductResponse convertFromStock(Stock stock) {
        StockDtoProductResponse responseDto = new StockDtoProductResponse();
        responseDto.setId(stock.getId());
        responseDto.setWarehouseId(stock.getWarehouse().getId());
        responseDto.setWarehouseName(stock.getWarehouse().getName());
        responseDto.setQuantity(stock.getQuantity());
        return responseDto;
    }

}
