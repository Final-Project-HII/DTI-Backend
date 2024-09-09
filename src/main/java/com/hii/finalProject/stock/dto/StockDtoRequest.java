package com.hii.finalProject.stock.dto;

import com.hii.finalProject.products.entity.Product;
import com.hii.finalProject.stock.entity.Stock;
import com.hii.finalProject.warehouse.entity.Warehouse;
import lombok.Data;

import java.io.Serializable;
@Data
public class StockDtoRequest implements Serializable {
    private Long productId;
    private Long warehouseId;
    private Integer quantity;

}
