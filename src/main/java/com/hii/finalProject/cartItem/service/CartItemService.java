package com.hii.finalProject.cartItem.service;

import com.hii.finalProject.cartItem.dto.CartItemDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

public interface CartItemService {
    CartItemDTO addToCart(Long userId, Long productId, Integer quantity);

    void removeFromCart(Long userId, Long productId);

    CartItemDTO updateCartItemQuantity(Long userId, Long productId, Integer quantity);

//    @Cacheable(value = "cartItems", key = "#cartItemId", unless = "#result == null")
    CartItemDTO getCartItemById(Long cartItemId);
}