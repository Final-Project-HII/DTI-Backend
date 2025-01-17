package com.hii.finalProject.cart.controller;

import com.hii.finalProject.auth.helpers.Claims;
import com.hii.finalProject.cart.dto.CartDTO;
import com.hii.finalProject.cart.service.CartService;
import com.hii.finalProject.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ResponseEntity<CartDTO> getCart() {
        String userEmail = Claims.getClaimsFromJwt().get("sub").toString();
        Long userId = userService.getUserByEmail(userEmail);
        CartDTO cartDTO = cartService.getCartDTO(userId);
        return ResponseEntity.ok(cartDTO);
    }

    @GetMapping("/total-weight")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ResponseEntity<Integer> getCartTotalWeight() {
        String userEmail = Claims.getClaimsFromJwt().get("sub").toString();
        Integer totalWeight = cartService.getCartTotalWeight(userEmail);
        return ResponseEntity.ok(totalWeight);
    }
}