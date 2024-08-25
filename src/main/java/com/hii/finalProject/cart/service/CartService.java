package com.hii.finalProject.cart.service;

import com.hii.finalProject.cart.dto.CartDTO;
import com.hii.finalProject.cart.entity.Cart;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

public interface CartService {
    @Cacheable(value = "cartDTOs", key = "#userId")
    CartDTO getCartDTO(Long userId);

    Cart getCartEntity(Long userId);

    @CachePut(value = "cartDTOs", key = "#userId")
    CartDTO createCartDTO(Long userId);

    Cart createCartEntity(Long userId);

//    void clearCart(Long userId);

    CartDTO updateCart(Long userId, Cart updatedCart);
}