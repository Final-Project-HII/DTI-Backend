package com.hii.finalProject.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class ProductDTO {
    private Long id;
    private Long categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer weight;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
