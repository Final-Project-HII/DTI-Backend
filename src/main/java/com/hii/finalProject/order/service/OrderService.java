package com.hii.finalProject.order.service;

import com.hii.finalProject.order.dto.OrderDTO;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(Long userId);
    OrderDTO getOrderById(Long orderId);
    List<OrderDTO> getOrdersByUserId(Long userId);
    OrderDTO updateOrderStatus(Long orderId, String status);
}