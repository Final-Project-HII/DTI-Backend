package com.hii.finalProject.cartItem.service;

import com.hii.finalProject.cartItem.dto.CartItemDTO;

public interface CartItemService {
    CartItemDTO addToCart(Long userId, Long productId, Integer quantity);
    void removeFromCart(Long userId, Long productId);
    void updateCartItemQuantity(Long userId, Long productId, Integer quantity);
}
