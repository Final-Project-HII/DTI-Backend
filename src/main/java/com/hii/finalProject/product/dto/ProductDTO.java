package com.hii.finalProject.product.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long id;
    private Long categoryId;
    private String name;
    private String description;
    private Double price;
    private Integer weight;
}