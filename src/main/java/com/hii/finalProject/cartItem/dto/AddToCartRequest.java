package com.hii.finalProject.cartItem.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddToCartRequest implements Serializable {
//    private Long id;
    private Long productId;
    private Integer quantity;
}