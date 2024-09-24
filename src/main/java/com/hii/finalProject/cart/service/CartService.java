package com.hii.finalProject.cart.service;

import com.hii.finalProject.cart.dto.CartDTO;
import com.hii.finalProject.cart.entity.Cart;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

public interface CartService {
    CartDTO getCartDTO(Long userId);
    Cart getCartEntity(Long userId);
    CartDTO createCartDTO(Long userId);
    Cart createCartEntity(Long userId);
    CartDTO updateCart(Long userId, Cart updatedCart);
    void clearCartCache(Long userId);
    void clearCart(Long userId);
    void updateCartTotals(Long userId);
    Integer getCartTotalWeight(String userEmail);
}