package com.hii.finalProject.products.dto;

import com.hii.finalProject.image.dto.ProductImageRequestDto;
import com.hii.finalProject.image.dto.ProductImageResponseDto;
import com.hii.finalProject.image.entity.ProductImage;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

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
    private List<ProductImageResponseDto> productImages;
    private Instant createdAt;
    private Instant updatedAt;

}
