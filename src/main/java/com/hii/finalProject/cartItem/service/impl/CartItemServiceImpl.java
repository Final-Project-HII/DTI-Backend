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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;

    @Autowired
    public CartItemServiceImpl(CartItemRepository cartItemRepository, CartService cartService,
                               ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartService = cartService;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public CartItemDTO addToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = cartService.getCart(userId);
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
    public void removeFromCart(Long userId, Long productId) {
        Cart cart = cartService.getCart(userId);

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        cartItemRepository.deleteByCartIdAndProductId(cart.getId(), productId);
    }

    @Override
    @Transactional
    public void updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        Cart cart = cartService.getCart(userId);

        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    cartItemRepository.save(item);
                });
    }

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