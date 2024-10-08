package com.hii.finalProject.order.controller;

import com.hii.finalProject.auth.helpers.Claims;
import com.hii.finalProject.exceptions.OrderProcessingException;
import com.hii.finalProject.order.dto.OrderDTO;
import com.hii.finalProject.order.entity.OrderStatus;
import com.hii.finalProject.order.service.OrderService;
import com.hii.finalProject.payment.entity.Payment;
import com.hii.finalProject.payment.service.PaymentService;
import com.hii.finalProject.response.Response;
import com.hii.finalProject.users.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Response<OrderDTO>> createOrder(
            @RequestParam Long addressId,
            @RequestParam Long courierId) {
        try {
            String userEmail = Claims.getClaimsFromJwt().get("sub").toString();
            Long userId = userService.getUserByEmail(userEmail);
            Long placeholderWarehouseId = null;
            OrderDTO orderDTO = orderService.createOrder(userId, placeholderWarehouseId, addressId, courierId);
            return Response.successfulResponse("Order created successfully", orderDTO);
        } catch (OrderProcessingException e) {
            return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            return Response.failedResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "We are unable to process your request at this time, please try again later.");
        }
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderDTO);
    }


    @GetMapping
    public ResponseEntity<Response<Page<OrderDTO>>> getUserOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        String userEmail = Claims.getClaimsFromJwt().get("sub").toString();
        Long userId = userService.getUserByEmail(userEmail);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderDTO> orders = orderService.getUserOrders(userId, status, date, pageRequest);
        return Response.successfulResponse("Orders successfully fetched", orders);
    }


    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }



    @GetMapping("/admin")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<Page<OrderDTO>>> getAdminOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long warehouse,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderDTO> orders = orderService.getAdminOrders(status, warehouse, date, pageRequest);
        return Response.successfulResponse("Orders successfully fetched", orders);
    }

    //////
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Response<OrderDTO>> cancelOrder(@PathVariable Long orderId) {
        try {
            OrderDTO canceledOrder = orderService.cancelOrder(orderId);
            return Response.successfulResponse("Order canceled successfully", canceledOrder);
        } catch (IllegalStateException e) {
            return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            return Response.failedResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "We are unable to process your request at this time, please try again later.");
        }
    }

    @PutMapping("/{orderId}/deliver")
    public ResponseEntity<Response<OrderDTO>> markOrderAsDelivered(@PathVariable Long orderId) {
        try {
            OrderDTO deliveredOrder = orderService.markOrderAsDelivered(orderId);
            return Response.successfulResponse("Order marked as delivered successfully", deliveredOrder);
        } catch (IllegalStateException e) {
            return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            return Response.failedResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "We are unable to process your request at this time, please try again later.");
        }
    }
}