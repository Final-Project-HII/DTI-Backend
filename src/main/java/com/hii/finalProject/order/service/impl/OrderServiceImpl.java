package com.hii.finalProject.order.service.impl;

import com.hii.finalProject.address.entity.Address;
import com.hii.finalProject.address.repository.AddressRepository;
import com.hii.finalProject.cart.entity.Cart;
import com.hii.finalProject.cart.service.CartService;
import com.hii.finalProject.courier.entity.Courier;
import com.hii.finalProject.courier.repository.CourierRepository;
import com.hii.finalProject.order.dto.OrderDTO;
import com.hii.finalProject.order.entity.Order;
import com.hii.finalProject.order.entity.OrderStatus;
import com.hii.finalProject.order.repository.OrderRepository;
import com.hii.finalProject.order.service.OrderService;
import com.hii.finalProject.orderItem.dto.OrderItemDTO;
import com.hii.finalProject.orderItem.entity.OrderItem;
import com.hii.finalProject.products.entity.Product;
import com.hii.finalProject.products.repository.ProductRepository;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.repository.UserRepository;
import com.hii.finalProject.warehouse.entity.Warehouse;
import com.hii.finalProject.warehouse.repository.WarehouseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final WarehouseRepository warehouseRepository;
    private final CourierRepository courierRepository;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository,
                            CartService cartService, ProductRepository productRepository,
                            AddressRepository addressRepository, WarehouseRepository warehouseRepository,
                            CourierRepository courierRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
        this.warehouseRepository = warehouseRepository;
        this.courierRepository = courierRepository;
    }

    @Override
    @Transactional
    public OrderDTO createOrder(Long userId, Long warehouseId, Long addressId, Long courierId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + warehouseId));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + addressId));
        Courier courier = courierRepository.findById(courierId)
                .orElseThrow(() -> new RuntimeException("Courier not found with id: " + courierId));

        Cart cart = cartService.getCartEntity(userId);

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot create order with empty cart");
        }

        Order order = new Order();
        order.setUser(user);
        order.setWarehouse(warehouse);
        order.setAddress(address);
        order.setCourier(courier);
        order.setStatus(OrderStatus.pending_payment);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        BigDecimal originalAmount = BigDecimal.ZERO;
        int totalWeight = 0;
        int totalQuantity = 0;

        for (com.hii.finalProject.cartItem.entity.CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(BigDecimal.valueOf(cartItem.getProduct().getPrice()));
            orderItem.setProductSnapshot(createProductSnapshot(cartItem.getProduct()));

            order.getItems().add(orderItem);
            originalAmount = originalAmount.add(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            totalWeight += cartItem.getProduct().getWeight() * cartItem.getQuantity();
            totalQuantity += cartItem.getQuantity();
        }

        order.setOriginalAmount(originalAmount);
        order.setFinalAmount(originalAmount); // You might want to apply discounts or shipping costs here
        order.setTotalWeight(totalWeight);
        order.setTotalQuantity(totalQuantity);

        // Save the order using the standard save method
        Order savedOrder = orderRepository.save(order);

        // Clear the cart after creating the order
        cartService.clearCart(userId);

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
        return convertToDTO(updatedOrder);
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
            orders = orderRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate, pageable);
        } else {
            orders = orderRepository.findByUserId(userId, pageable);
        }

        return orders.map(this::convertToDTO);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setInvoiceId(order.getInvoiceId());
        dto.setUserId(order.getUser().getId());
        dto.setWarehouseId(order.getWarehouse().getId());
        dto.setAddressId(order.getAddress().getId());
        dto.setItems(order.getItems().stream().map(this::convertToOrderItemDTO).collect(Collectors.toList()));
        dto.setOrderDate(order.getCreatedAt());
        dto.setStatus(order.getStatus().name());  // Convert ENUM to string for DTO
        dto.setOriginalAmount(order.getOriginalAmount());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setTotalWeight(order.getTotalWeight());
        dto.setTotalQuantity(order.getTotalQuantity());
        dto.setCourierId(order.getCourier().getId());
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

    private String createProductSnapshot(Product product) {
        // Implement this method to create a JSON representation of the product
        // You might want to use a JSON library like Jackson or Gson
        return ""; // Placeholder
    }
}