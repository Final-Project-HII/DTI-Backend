package com.hii.finalProject.products.dto;

import com.hii.finalProject.image.dto.ProductImageRequestDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NewProductRequestDto {
    private String name;
    private String description;
    private String price;
    private Integer weight;
    private Long categoryId;
//    private List<ProductImageRequestDto> productImages;
}
