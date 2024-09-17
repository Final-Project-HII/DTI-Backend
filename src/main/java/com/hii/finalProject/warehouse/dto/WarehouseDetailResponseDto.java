package com.hii.finalProject.warehouse.dto;


import com.hii.finalProject.stock.dto.StockDtoResponse;
import com.hii.finalProject.stock.dto.StockDtoWarehouseResponse;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class WarehouseDetailResponseDto {
    private Long id;
    private String name;
    private String addressLine;
    private Long cityId;
    private String postalCode;
    private Float lat;
    private Float lon;
    private List<StockDtoWarehouseResponse> stocks;
    private Instant createdAt;
    private Instant updatedAt;

}
