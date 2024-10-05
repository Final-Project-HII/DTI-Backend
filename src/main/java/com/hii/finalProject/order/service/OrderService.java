package com.hii.finalProject.order.service;

import com.hii.finalProject.order.dto.OrderDTO;
import com.hii.finalProject.order.entity.OrderStatus;
import com.hii.finalProject.payment.entity.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface OrderService {
    OrderDTO createOrder(Long userId, Long warehouseId, Long addressId, Long courierId);

    OrderDTO getOrderById(Long orderId);
    Page<OrderDTO> getOrdersByUserId(Long userId, Pageable pageable);
    OrderDTO updateOrderStatus(Long orderId, OrderStatus status);
    OrderDTO cancelOrder(Long orderId) throws IllegalStateException;

    @Transactional
    OrderDTO markOrderAsDelivered(Long orderId) throws IllegalStateException;

    Page<OrderDTO> getFilteredOrders(Long userId, String status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    OrderDTO shipOrder(Long orderId);

    Page<OrderDTO> getAllOrders(Pageable pageable);

//    Page<OrderDTO> getFilteredOrdersForAdmin(String status, Long warehouseId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    Page<OrderDTO> getFilteredOrdersForAdmin(Long warehouseId, Pageable pageable);
}