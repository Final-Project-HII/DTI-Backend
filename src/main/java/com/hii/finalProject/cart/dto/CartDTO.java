package com.hii.finalProject.cart.dto;

import com.hii.finalProject.cartItem.dto.CartItemDTO;
import lombok.Data;
import java.util.List;

@Data
public class CartDTO {
    private Long id;
    private Long userId;
    private List<CartItemDTO> items;
    private Integer totalPrice;
    private Integer totalWeight;
}