package com.hii.finalProject.stock.dto;

import com.hii.finalProject.stock.entity.Stock;
import lombok.Data;

@Data
public class StockDtoProductResponse {
    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private Integer quantity;



}
