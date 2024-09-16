package com.hii.finalProject.order.service;

import com.hii.finalProject.order.dto.OrderDTO;
import com.hii.finalProject.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    OrderDTO createOrder(Long userId);
    OrderDTO getOrderById(Long orderId);
    Page<OrderDTO> getOrdersByUserId(Long userId, Pageable pageable);
    OrderDTO updateOrderStatus(Long orderId, OrderStatus status);
    Page<OrderDTO> getFilteredOrders(Long userId, String status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
