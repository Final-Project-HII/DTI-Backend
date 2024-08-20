package com.hii.finalProject.cartItem.dto;

import lombok.Data;

@Data
public class AddToCartRequest {
    private Long userId;
    private Long productId;
    private Integer quantity;
}