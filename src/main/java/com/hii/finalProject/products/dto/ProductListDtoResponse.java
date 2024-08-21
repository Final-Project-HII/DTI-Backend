package com.hii.finalProject.products.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ProductListDtoResponse {
    private Long id;
    private String name;
    private String description;
    private Integer price;
    private Integer weight;
    private Long categoryId;
    private String categoryName;
    private Instant createdAt;
    private Instant updatedAt;

}
