package com.hii.finalProject.order.service.impl;

import com.hii.finalProject.cart.entity.Cart;
import com.hii.finalProject.cart.service.CartService;
import com.hii.finalProject.order.dto.OrderDTO;
import com.hii.finalProject.order.entity.Order;
import com.hii.finalProject.order.entity.OrderStatus;
import com.hii.finalProject.order.repository.OrderRepository;
import com.hii.finalProject.order.service.OrderService;
import com.hii.finalProject.orderItem.dto.OrderItemDTO;
import com.hii.finalProject.orderItem.entity.OrderItem;
import com.hii.finalProject.payment.entity.PaymentStatus;
import com.hii.finalProject.payment.service.PaymentService;
import com.hii.finalProject.products.repository.ProductRepository;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository,
                            CartService cartService, ProductRepository productRepository, @Lazy PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.productRepository = productRepository;
        this.paymentService = paymentService;
    }

    @Override
    @Transactional
    public OrderDTO createOrder(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Cart cart = cartService.getCartEntity(userId);

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot create order with empty cart");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PAYMENT_PENDING);

        double totalAmount = 0;

        for (com.hii.finalProject.cartItem.entity.CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(Double.valueOf(cartItem.getProduct().getPrice()));

            order.getItems().add(orderItem);
            totalAmount += orderItem.getPrice() * orderItem.getQuantity();
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        return convertToDTO(savedOrder);
    }

    @Override
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        return convertToDTO(order);
    }

    @Override
    public Page<OrderDTO> getOrdersByUserId(Long userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.map(this::convertToDTO);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        updateOrderStatusByPaymentStatus(orderId);

        return convertToDTO(updatedOrder);
    }

    @Transactional
    public void updateOrderStatusByPaymentStatus(Long orderId) {
        PaymentStatus paymentStatus = paymentService.getTransactionStatus(orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (paymentStatus == PaymentStatus.COMPLETED) {
            order.setStatus(OrderStatus.PAYMENT_SUCCESS);
            orderRepository.save(order);
        }
    }

    @Override
    public Page<OrderDTO> getFilteredOrders(Long userId, String status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<Order> orders;

        if (status != null && !status.isEmpty()) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orders = orderRepository.findByUserIdAndStatus(userId, orderStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid order status: " + status);
            }
        } else if (startDate != null && endDate != null) {
            orders = orderRepository.findByUserIdAndOrderDateBetween(userId, startDate, endDate, pageable);
        } else {
            orders = orderRepository.findByUserId(userId, pageable);
        }

        return orders.map(this::convertToDTO);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setItems(order.getItems().stream().map(this::convertToOrderItemDTO).collect(Collectors.toList()));
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount());
        return dto;
    }

    private OrderItemDTO convertToOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setProductId(orderItem.getProduct().getId());
        dto.setProductName(orderItem.getProduct().getName());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        return dto;
    }
}