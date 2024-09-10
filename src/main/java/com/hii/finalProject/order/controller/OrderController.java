package com.hii.finalProject.order.controller;

import com.hii.finalProject.auth.helpers.Claims;
import com.hii.finalProject.order.dto.OrderDTO;
import com.hii.finalProject.order.service.OrderService;
import com.hii.finalProject.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder() {
        String userEmail = Claims.getClaimsFromJwt().get("sub").toString();
        Long userId = userService.getUserByEmail(userEmail);
        OrderDTO orderDTO = orderService.createOrder(userId);
        return ResponseEntity.ok(orderDTO);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderDTO);
    }

    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        String userEmail = Claims.getClaimsFromJwt().get("sub").toString();
        Long userId = userService.getUserByEmail(userEmail);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderDTO> orders = orderService.getOrdersByUserId(userId, pageRequest);
        return ResponseEntity.ok(orders);
    }


    @GetMapping("/filtered")
    public ResponseEntity<Page<OrderDTO>> getFilteredOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        String userEmail = Claims.getClaimsFromJwt().get("sub").toString();
        Long userId = userService.getUserByEmail(userEmail);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderDTO> orders = orderService.getFilteredOrders(userId, status, startDate, endDate, pageRequest);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }
}