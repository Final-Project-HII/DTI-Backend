package com.hii.finalProject.orderItem.service;

import com.hii.finalProject.orderItem.dto.OrderItemDTO;

import java.util.List;

public interface OrderItemService {
    OrderItemDTO getOrderItemById(Long orderItemId);
    List<OrderItemDTO> getOrderItemsByOrderId(Long orderId);
    OrderItemDTO updateOrderItemQuantity(Long orderItemId, Integer quantity);
    void deleteOrderItem(Long orderItemId);
}