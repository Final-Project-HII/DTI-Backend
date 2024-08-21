package com.hii.finalProject.cartItem.dto;

import lombok.Data;

@Data
public class AddToCartRequest {
    private Long id;
    private Long productId;
    private Integer quantity;
}