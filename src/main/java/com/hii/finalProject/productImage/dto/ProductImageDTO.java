package com.hii.finalProject.productImage.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductImageDTO {
    private Long id;
    private Integer productId;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}