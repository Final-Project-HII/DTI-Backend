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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final PaymentService paymentService;

    public OrderController(OrderService orderService, UserService userService, PaymentService paymentService) {
        this.orderService = orderService;
        this.userService = userService;
        this.paymentService = paymentService;
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
    public ResponseEntity<Response<OrderDTO>> getOrder(@PathVariable Long orderId) {
        try {
            OrderDTO orderDTO = orderService.getOrderById(orderId);
            Payment payment = paymentService.getPaymentByOrderId(orderId);

            // Set payment method in OrderDTO
            orderDTO.setPaymentMethod(payment.getPaymentMethod());

            return Response.successfulResponse("Order fetched successfully", orderDTO);
        } catch (Exception e) {
            return Response.failedResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "We are unable to process your request at this time, please try again later.");
        }
    }


    @GetMapping
    public ResponseEntity<Response<Page<OrderDTO>>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        String userEmail = Claims.getClaimsFromJwt().get("sub").toString();
        Long userId = userService.getUserByEmail(userEmail);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderDTO> orders = orderService.getOrdersByUserId(userId, pageRequest);
        return Response.successfulResponse("orders succesfull fetched", orders);
    }

    @GetMapping("/filtered")
    public ResponseEntity<Page<OrderDTO>> getFilteredOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        String userEmail = Claims.getClaimsFromJwt().get("sub").toString();
        Long userId = userService.getUserByEmail(userEmail);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderDTO> orders = orderService.getFilteredOrders(userId, status, startDate, endDate, pageRequest);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        OrderDTO updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/admin/all")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<Page<OrderDTO>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderDTO> orders = orderService.getAllOrders(pageRequest);
        return Response.successfulResponse("All orders successfully fetched", orders);
    }

    @GetMapping("/admin/filtered")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<Page<OrderDTO>>> getFilteredOrdersForAdmin(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderDTO> orders = orderService.getFilteredOrdersForAdmin(status, warehouseId, startDate, endDate, pageRequest);
        return Response.successfulResponse("Filtered orders successfully fetched", orders);
    }

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