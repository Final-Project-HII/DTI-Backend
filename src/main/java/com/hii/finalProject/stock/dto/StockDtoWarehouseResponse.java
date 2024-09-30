package com.hii.finalProject.stock.dto;

import com.hii.finalProject.image.dto.ProductImageResponseDto;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class StockDtoWarehouseResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Long categoryId;
    private String categoryName;


}
