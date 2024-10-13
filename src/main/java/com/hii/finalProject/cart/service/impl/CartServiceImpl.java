package com.hii.finalProject.cart.service.impl;

import com.hii.finalProject.cart.dto.CartDTO;
import com.hii.finalProject.cart.entity.Cart;
import com.hii.finalProject.cart.repository.CartRepository;
import com.hii.finalProject.cart.service.CartService;
import com.hii.finalProject.cartItem.dto.CartItemDTO;
import com.hii.finalProject.cartItem.entity.CartItem;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.repository.UserRepository;
import com.hii.finalProject.users.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserService userService;

    public CartServiceImpl(CartRepository cartRepository, UserRepository userRepository, RedisTemplate<String, Object> redisTemplate, UserService userService) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.userService = userService;
    }

    @Override
    @Transactional
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
        Cart cart = getCartEntity(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
        clearCartCache(userId);
    }

    @Override
    @Transactional
    public void updateCartTotals(Long userId) {
        Cart cart = getCartEntity(userId);
        int totalPrice = 0;
        int totalWeight = 0;

        for (CartItem item : cart.getItems()) {
            totalPrice += item.getQuantity() * item.getProduct().getPrice();
            totalWeight += item.getQuantity() * item.getProduct().getWeight();
        }

        cart.setTotalPrice(totalPrice);
        cart.setTotalWeight(totalWeight);
        cartRepository.save(cart);
        clearCartCache(userId);
    }

    @Override
    public Integer getCartTotalWeight(String userEmail) {
        Long userId = userService.getUserByEmail(userEmail);
        CartDTO cartDTO = getCartDTO(userId);
        return cartDTO.getTotalWeight();
    }


    private CartDTO convertToDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setUserId(cart.getUser().getId());

        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toList());

        cartDTO.setItems(itemDTOs);

        // Recalculate total price and total weight
        int totalPrice = itemDTOs.stream()
                .mapToInt(CartItemDTO::getTotalPrice)
                .sum();

        int totalWeight = itemDTOs.stream()
                .mapToInt(CartItemDTO::getTotalWeight)
                .sum();

        cartDTO.setTotalPrice(totalPrice);
        cartDTO.setTotalWeight(totalWeight);

        return cartDTO;
    }

    private CartItemDTO convertToCartItemDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setProductId(cartItem.getProduct().getId());
        dto.setProductName(cartItem.getProduct().getName());
        dto.setQuantity(cartItem.getQuantity());
        dto.setPrice(cartItem.getProduct().getPrice());
        dto.setTotalPrice(cartItem.getQuantity() * cartItem.getProduct().getPrice());
        dto.setWeight(cartItem.getProduct().getWeight());
        dto.setTotalWeight(cartItem.getQuantity() * cartItem.getProduct().getWeight());
        return dto;
    }
}