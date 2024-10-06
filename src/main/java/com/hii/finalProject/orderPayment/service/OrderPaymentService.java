package com.hii.finalProject.orderPayment.service;

import com.hii.finalProject.order.dto.OrderDTO;
import com.hii.finalProject.order.service.OrderService;
import com.hii.finalProject.payment.entity.Payment;
import com.hii.finalProject.payment.repository.PaymentRepository;
import com.hii.finalProject.payment.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderPaymentService {
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    public OrderPaymentService(OrderService orderService, PaymentService paymentService, PaymentRepository paymentRepository) {
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }

    public OrderDTO getOrderWithPaymentMethod(Long orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        try {
            Payment payment = paymentRepository.findByOrderId(orderId).orElse(null);
            if (payment != null) {
                orderDTO.setPaymentMethod(payment.getPaymentMethod());
            }
        } catch (RuntimeException e) {
            // Payment not found, leave paymentMethod as null
        }
        return orderDTO;
    }

    public Page<OrderDTO> getOrdersWithPaymentMethod(Long userId, Pageable pageable) {
        Page<OrderDTO> orders = orderService.getOrdersByUserId(userId, pageable);
        List<Long> orderIds = orders.getContent().stream()
                .map(OrderDTO::getId)
                .collect(Collectors.toList());

        Map<Long, Payment> paymentMap = paymentRepository.findByOrderIdIn(orderIds).stream()
                .collect(Collectors.toMap(Payment::getOrderId, p -> p));

        orders.forEach(orderDTO -> {
            Payment payment = paymentMap.get(orderDTO.getId());
            if (payment != null) {
                orderDTO.setPaymentMethod(payment.getPaymentMethod());
            }
        });

        return orders;
    }

    public Page<OrderDTO> getAllOrdersWithPaymentMethod(Pageable pageable) {
        Page<OrderDTO> orders = orderService.getAllOrders(pageable);
        return addPaymentMethodToOrders(orders);
    }

    public Page<OrderDTO> getFilteredOrdersForAdminWithPaymentMethod(String status, Long warehouse, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<OrderDTO> orders = orderService.getFilteredOrdersForAdmin(status, warehouse, startDate, endDate, pageable);
        return addPaymentMethodToOrders(orders);
    }

    private Page<OrderDTO> addPaymentMethodToOrders(Page<OrderDTO> orders) {
        List<Long> orderIds = orders.getContent().stream()
                .map(OrderDTO::getId)
                .collect(Collectors.toList());

        Map<Long, Payment> paymentMap = paymentRepository.findByOrderIdIn(orderIds).stream()
                .collect(Collectors.toMap(Payment::getOrderId, p -> p));

        orders.forEach(orderDTO -> {
            Payment payment = paymentMap.get(orderDTO.getId());
            if (payment != null) {
                orderDTO.setPaymentMethod(payment.getPaymentMethod());
            }
        });

        return orders;
    }
}