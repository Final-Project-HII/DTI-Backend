package com.hii.finalProject.order.dto;

import com.hii.finalProject.orderItem.dto.OrderItemDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private Long userId;
    private List<OrderItemDTO> items;
    private LocalDateTime orderDate;
    private String status;
    private Double totalAmount;
}