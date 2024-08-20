package com.hii.finalProject.cart.service;

import com.hii.finalProject.cart.dto.CartDTO;
import com.hii.finalProject.cart.entity.Cart;

public interface CartService {
    CartDTO getCartDTO(Long userId);
    Cart getCart(Long userId);
    Cart createCart(Long userId);
}