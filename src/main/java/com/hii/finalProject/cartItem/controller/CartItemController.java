package com.hii.finalProject.cartItem.controller;

import com.hii.finalProject.cartItem.dto.AddToCartRequest;
import com.hii.finalProject.cartItem.dto.CartItemDTO;
import com.hii.finalProject.cartItem.dto.UpdateQuantityRequest;
import com.hii.finalProject.cartItem.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/cart-items")
public class CartItemController {

    // Initialize logger
    private static final Logger log = LoggerFactory.getLogger(CartItemController.class);

    private final CartItemService cartItemService;

    @Autowired
    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PostMapping("/add")
    public ResponseEntity<CartItemDTO> addToCart(@RequestBody AddToCartRequest request) {
        // Log the userId for debugging
        log.info("Received user_id: {}", request.getId());

        CartItemDTO addedItem = cartItemService.addToCart(request.getId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(addedItem);
    }

    @DeleteMapping("/{userId}/item/{productId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long userId, @PathVariable Long productId) {
        cartItemService.removeFromCart(userId, productId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/item/{productId}")
    public ResponseEntity<Void> updateCartItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestBody UpdateQuantityRequest request) {
        cartItemService.updateCartItemQuantity(userId, productId, request.getQuantity());
        return ResponseEntity.ok().build();
    }
}
