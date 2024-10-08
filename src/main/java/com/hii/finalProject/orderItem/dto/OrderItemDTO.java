package com.hii.finalProject.orderItem.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private Long categoryId;
    private String categoryName;
    private Integer quantity;
    private BigDecimal price;
    private String productSnapshot;
}