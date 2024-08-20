package com.hii.finalProject.cart.service.impl;

import com.hii.finalProject.cart.dto.CartDTO;
import com.hii.finalProject.cart.entity.Cart;
import com.hii.finalProject.cart.repository.CartRepository;
import com.hii.finalProject.cart.service.CartService;
import com.hii.finalProject.cartItem.dto.CartItemDTO;
import com.hii.finalProject.cartItem.entity.CartItem;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Cacheable(value = "carts", key = "#userId")
    public CartDTO getCartDTO(Long userId) {
        Cart cart = getCart(userId);
        return convertToDTO(cart);
    }

    @Override
    @Cacheable(value = "carts", key = "#userId")
    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));
    }

    @Override
    @Transactional
    @Cacheable(value = "carts", key = "#userId")
    public Cart createCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart newCart = new Cart();
        newCart.setUser(user);
        return cartRepository.save(newCart);
    }

    private CartDTO convertToDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setUserId(cart.getUser().getId());
        cartDTO.setItems(cart.getItems().stream().map(this::convertToCartItemDTO).toList());
        return cartDTO;
    }

    private CartItemDTO convertToCartItemDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setProductId(cartItem.getProduct().getId());
        dto.setProductName(cartItem.getProduct().getName());
        dto.setQuantity(cartItem.getQuantity());
        dto.setPrice(cartItem.getProduct().getPrice());
        return dto;
    }
}