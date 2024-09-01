package com.hii.finalProject.order.controller;


import com.hii.finalProject.orderItem.dto.OrderItemDTO;
import com.hii.finalProject.orderItem.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @Autowired
    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping("/{orderItemId}")
    public ResponseEntity<OrderItemDTO> getOrderItem(@PathVariable Long orderItemId) {
        OrderItemDTO orderItemDTO = orderItemService.getOrderItemById(orderItemId);
        return ResponseEntity.ok(orderItemDTO);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItemDTO>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        List<OrderItemDTO> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItems);
    }

    @PutMapping("/{orderItemId}/quantity")
    public ResponseEntity<OrderItemDTO> updateOrderItemQuantity(
            @PathVariable Long orderItemId,
            @RequestParam Integer quantity) {
        OrderItemDTO updatedOrderItem = orderItemService.updateOrderItemQuantity(orderItemId, quantity);
        return ResponseEntity.ok(updatedOrderItem);
    }

    @DeleteMapping("/{orderItemId}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long orderItemId) {
        orderItemService.deleteOrderItem(orderItemId);
        return ResponseEntity.noContent().build();
    }
}