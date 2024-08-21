package com.hii.finalProject.products.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewProductRequestDto {
    private String name;
    private String description;
    private String price;
    private Integer weight;
    private Long categoryId;
}
