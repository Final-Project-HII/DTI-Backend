package com.hii.finalProject.orderPayment.service;

import com.hii.finalProject.order.dto.OrderDTO;
import com.hii.finalProject.order.service.OrderService;
import com.hii.finalProject.payment.entity.Payment;
import com.hii.finalProject.payment.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class OrderPaymentService {
    private final OrderService orderService;
    private final PaymentService paymentService;

    public OrderPaymentService(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    public OrderDTO getOrderWithPaymentMethod(Long orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        try {
            Payment payment = paymentService.getPaymentByOrderId(orderId);
            orderDTO.setPaymentMethod(payment.getPaymentMethod());
        } catch (RuntimeException e) {
            // Payment not found, leave paymentMethod as null
        }
        return orderDTO;
    }
}