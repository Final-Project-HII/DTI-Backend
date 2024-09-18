package com.hii.finalProject.order.dto;

import com.hii.finalProject.orderItem.dto.OrderItemDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String invoiceId;
    private Long userId;
    private Long warehouseId;
    private Long addressId;
    private List<OrderItemDTO> items;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal originalAmount;
    private BigDecimal finalAmount;
    private Integer totalWeight;
    private Integer totalQuantity;
    private Long courierId;
    private String warehouseName;
    private String courierName;
    private String originCity;
    private String destinationCity;
}