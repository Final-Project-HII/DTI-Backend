package com.hii.finalProject.products.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UpdateProductRequestDto {
    private String name;
    private String description;
    private String price;
    private Integer weight;
    private Long categoryId;
    private List<Long> deleteImages;
}
