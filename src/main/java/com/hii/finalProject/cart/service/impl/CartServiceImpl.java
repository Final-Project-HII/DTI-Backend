package com.hii.finalProject.cart.service.impl;

import com.hii.finalProject.cart.dto.CartDTO;
import com.hii.finalProject.cart.entity.Cart;
import com.hii.finalProject.cart.repository.CartRepository;
import com.hii.finalProject.cart.service.CartService;
import com.hii.finalProject.cartItem.dto.CartItemDTO;
import com.hii.finalProject.cartItem.entity.CartItem;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.repository.UserRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public CartServiceImpl(CartRepository cartRepository, UserRepository userRepository, RedisTemplate<String, Object> redisTemplate) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
//    @Cacheable(value = "cartDTOs", key = "#userId")
    public CartDTO getCartDTO(Long userId) {
        Cart cart = getCartEntity(userId);
        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public Cart getCartEntity(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createCartEntity(userId));
    }

    @Override
    @Transactional
//    @CachePut(value = "cartDTOs", key = "#userId")
    public CartDTO createCartDTO(Long userId) {
        Cart cart = createCartEntity(userId);
        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public Cart createCartEntity(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found" + userId));
        Cart newCart = new Cart();
        newCart.setUser(user);
        return cartRepository.save(newCart);
    }

    @Override
    public CartDTO updateCart(Long userId, Cart updatedCart) {
        Cart savedCart = cartRepository.save(updatedCart);
        CartDTO cartDTO = convertToDTO(savedCart);
        redisTemplate.opsForValue().set("cartDTOs::" + userId, cartDTO, 1, TimeUnit.HOURS);
        return cartDTO;
    }

    @Override
    public void clearCartCache(Long userId) {
        redisTemplate.delete("cartDTOs::" + userId);
    }

    @Override
    public void clearCart(Long userId) {

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
        dto.setPrice(Double.valueOf(cartItem.getProduct().getPrice()));
        return dto;
    }
}