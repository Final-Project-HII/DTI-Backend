package com.hii.finalProject.cartItem.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CartItemDTO implements Serializable {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Integer price;
    private Integer totalPrice;
    private Integer weight;
    private Integer totalWeight;
}
