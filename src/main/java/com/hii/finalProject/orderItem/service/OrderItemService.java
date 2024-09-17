package com.hii.finalProject.orderItem.service;

import com.hii.finalProject.orderItem.dto.OrderItemDTO;

import java.util.List;

public interface OrderItemService {
    OrderItemDTO getOrderItemById(Long orderItemId);
    List<OrderItemDTO> getOrderItemsByOrderId(Long orderId);
    OrderItemDTO updateOrderItem(Long orderItemId, OrderItemDTO orderItemDTO);
    void deleteOrderItem(Long orderItemId);
}