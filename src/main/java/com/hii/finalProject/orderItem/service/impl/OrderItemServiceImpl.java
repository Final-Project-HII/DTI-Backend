package com.hii.finalProject.orderItem.service.impl;

import com.hii.finalProject.order.repository.OrderRepository;
import com.hii.finalProject.orderItem.dto.OrderItemDTO;
import com.hii.finalProject.orderItem.entity.OrderItem;
import com.hii.finalProject.orderItem.repository.OrderItemRepository;
import com.hii.finalProject.orderItem.service.OrderItemService;
import com.hii.finalProject.products.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository,
                                OrderRepository orderRepository,
                                ProductRepository productRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public OrderItemDTO getOrderItemById(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found with id: " + orderItemId));
        return convertToDTO(orderItem);
    }

    @Override
    public List<OrderItemDTO> getOrderItemsByOrderId(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderItems.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderItemDTO updateOrderItem(Long orderItemId, OrderItemDTO orderItemDTO) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found with id: " + orderItemId));

        orderItem.setQuantity(orderItemDTO.getQuantity());
        orderItem.setPrice(orderItemDTO.getPrice());
        orderItem.setProductSnapshot(orderItemDTO.getProductSnapshot());

        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
        return convertToDTO(updatedOrderItem);
    }

    @Override
    @Transactional
    public void deleteOrderItem(Long orderItemId) {
        orderItemRepository.deleteById(orderItemId);
    }

    private OrderItemDTO convertToDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setOrderId(orderItem.getOrder().getId());
        dto.setProductId(orderItem.getProduct().getId());
        dto.setProductName(orderItem.getProduct().getName());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        dto.setProductSnapshot(orderItem.getProductSnapshot());
        return dto;
    }
}