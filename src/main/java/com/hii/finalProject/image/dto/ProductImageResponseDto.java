package com.hii.finalProject.image.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ProductImageResponseDto {
    private Long id;
    private Long productId;
    private String imageUrl;
    private Instant createdAt;
    private Instant updatedAt;
}
