package com.hii.finalProject.scheduler;

import com.hii.finalProject.order.service.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderCancellationScheduler {

    private final OrderService orderService;

    public OrderCancellationScheduler(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void cancelUnpaidOrders() {
        orderService.cancelUnpaidOrders();
    }
}