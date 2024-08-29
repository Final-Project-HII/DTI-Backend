package com.hii.finalProject.image.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageRequestDto {
    private Long productId;
    private String imageUrl;
}
