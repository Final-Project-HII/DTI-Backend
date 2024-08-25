package com.hii.finalProject.cartItem.service.impl;

import com.hii.finalProject.cart.entity.Cart;
import com.hii.finalProject.cart.service.CartService;
import com.hii.finalProject.cartItem.dto.CartItemDTO;
import com.hii.finalProject.cartItem.entity.CartItem;
import com.hii.finalProject.cartItem.repository.CartItemRepository;
import com.hii.finalProject.cartItem.service.CartItemService;
import com.hii.finalProject.product.entity.Product;
import com.hii.finalProject.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public CartItemServiceImpl(CartItemRepository cartItemRepository, CartService cartService,
                               ProductRepository productRepository, RedisTemplate<String, Object> redisTemplate) {
        this.cartItemRepository = cartItemRepository;
        this.cartService = cartService;
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public CartItemDTO addToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = cartService.getCartEntity(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    cart.addItem(newItem);
                    return newItem;
                });

        cartItem.setQuantity(cartItem.getQuantity() + quantity);

        cartItemRepository.save(cartItem);

        return convertToDTO(cartItem);
    }

    @Override
    @Transactional
    @CacheEvict(value = "cartItems", key = "#userId + ':' + #productId")
    public void removeFromCart(Long userId, Long productId) {
        Cart cart = cartService.getCartEntity(userId);
        boolean itemRemoved = cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        if (itemRemoved) {
            cartItemRepository.deleteByCartIdAndProductId(cart.getId(), productId);
        } else {
            throw new RuntimeException("CartItem not found for removal");
        }
    }

    @Override
    @Transactional
    @CachePut(value = "cartItems", key = "#userId + ':' + #productId")
    public CartItemDTO updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        Cart cart = cartService.getCartEntity(userId);

        CartItem updatedItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .map(item -> {
                    item.setQuantity(quantity);
                    return cartItemRepository.save(item);
                })
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        return convertToDTO(updatedItem);
    }

    @Override
    @Cacheable(value = "cartItems", key = "#cartItemId", unless = "#result == null")
    public CartItemDTO getCartItemById(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
        return convertToDTO(cartItem);
    }

    private CartItemDTO convertToDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setProductId(cartItem.getProduct().getId());
        dto.setProductName(cartItem.getProduct().getName());
        dto.setQuantity(cartItem.getQuantity());
        dto.setPrice(cartItem.getProduct().getPrice());
        return dto;
    }
}