package com.hii.finalProject.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hii.finalProject.orderItem.dto.OrderItemDTO;
import com.hii.finalProject.payment.entity.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderDate;
    private String status;
    private BigDecimal originalAmount;
    private BigDecimal finalAmount;
    private Integer totalWeight;
    private Integer totalQuantity;
    private Long courierId;
    private BigDecimal shippingCost;
    private String warehouseName;
    private String courierName;
    private String originCity;
    private String destinationCity;
    private PaymentMethod paymentMethod;

}