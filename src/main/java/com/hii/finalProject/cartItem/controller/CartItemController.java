package com.hii.finalProject.cartItem.controller;

import com.hii.finalProject.auth.helpers.Claims;
import com.hii.finalProject.cartItem.dto.AddToCartRequest;
import com.hii.finalProject.cartItem.dto.CartItemDTO;
import com.hii.finalProject.cartItem.dto.UpdateQuantityRequest;
import com.hii.finalProject.cartItem.service.CartItemService;
import com.hii.finalProject.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/cart-items")
public class CartItemController {

    private static final Logger log = LoggerFactory.getLogger(CartItemController.class);

    private final CartItemService cartItemService;
    private final UserService userService;

    @Autowired
    public CartItemController(CartItemService cartItemService, UserService userService) {
        this.cartItemService = cartItemService;
        this.userService = userService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ResponseEntity<CartItemDTO> addToCart(@RequestBody AddToCartRequest request) {
        String userEmail = Claims.getClaimsFromJwt().get("sub").toString();
        Long userId = userService.getUserByEmail(userEmail);
        CartItemDTO addedItem = cartItemService.addToCart(userId, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(addedItem);
    }

    @DeleteMapping("/item/{productId}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long productId) {
        String userEmail = Claims.getClaimsFromJwt().get("sub").toString();
        Long userId = userService.getUserByEmail(userEmail);
        cartItemService.removeFromCart(userId, productId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/item/{productId}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ResponseEntity<Void> updateCartItemQuantity(
            @PathVariable Long productId,
            @RequestBody UpdateQuantityRequest request) {
        String userEmail = Claims.getClaimsFromJwt().get("sub").toString();
        Long userId = userService.getUserByEmail(userEmail);
        cartItemService.updateCartItemQuantity(userId, productId, request.getQuantity());
        return ResponseEntity.ok().build();
    }
}