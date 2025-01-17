package com.hii.finalProject.order.service.impl;

import com.hii.finalProject.address.entity.Address;
import com.hii.finalProject.address.repository.AddressRepository;
import com.hii.finalProject.cart.entity.Cart;
import com.hii.finalProject.cart.service.CartService;
import com.hii.finalProject.courier.entity.Courier;
import com.hii.finalProject.courier.repository.CourierRepository;
import com.hii.finalProject.courier.service.CourierService;
import com.hii.finalProject.exceptions.InsufficientStockException;
import com.hii.finalProject.exceptions.InsufficientStockItem;
import com.hii.finalProject.exceptions.OrderInsufficientStockException;
import com.hii.finalProject.exceptions.OrderProcessingException;
import com.hii.finalProject.order.dto.OrderDTO;
import com.hii.finalProject.order.entity.Order;
import com.hii.finalProject.order.entity.OrderStatus;
import com.hii.finalProject.order.repository.OrderRepository;
import com.hii.finalProject.order.service.OrderService;
import com.hii.finalProject.orderItem.dto.OrderItemDTO;
import com.hii.finalProject.orderItem.entity.OrderItem;
import com.hii.finalProject.payment.entity.Payment;
import com.hii.finalProject.payment.repository.PaymentRepository;
import com.hii.finalProject.products.entity.Product;
import com.hii.finalProject.products.repository.ProductRepository;
import com.hii.finalProject.stock.service.StockService;
import com.hii.finalProject.stockMutation.service.AutoStockMutationService;
import com.hii.finalProject.stockMutationJournal.entity.StockMutationJournal;
import com.hii.finalProject.stockMutationJournal.repository.StockMutationJournalRepository;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.repository.UserRepository;
import com.hii.finalProject.warehouse.entity.Warehouse;
import com.hii.finalProject.warehouse.repository.WarehouseRepository;
import com.hii.finalProject.warehouse.service.WarehouseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final CourierService courierService;
    private final StockService stockService;
    private final WarehouseService warehouseService;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final WarehouseRepository warehouseRepository;
    private final CourierRepository courierRepository;
    private static final String INVOICE_PREFIX = "INV";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final AtomicInteger sequence = new AtomicInteger(1);
    private final PaymentRepository paymentRepository;
    private final AutoStockMutationService autoStockMutationService;
    private final StockMutationJournalRepository stockMutationJournalRepository;
    private final Random random = new Random();

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository,
                            CartService cartService, ProductRepository productRepository,
                            AddressRepository addressRepository, WarehouseRepository warehouseRepository,
                            CourierRepository courierRepository, CourierService courierService, StockService stockService, WarehouseService warehouseService, PaymentRepository paymentRepository, AutoStockMutationService autoStockMutationService, StockMutationJournalRepository stockMutationJournalRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
        this.warehouseRepository = warehouseRepository;
        this.courierRepository = courierRepository;
        this.courierService = courierService;
        this.stockService = stockService;
        this.warehouseService = warehouseService;
        this.paymentRepository = paymentRepository;
        this.autoStockMutationService = autoStockMutationService;
        this.stockMutationJournalRepository = stockMutationJournalRepository;
    }

    @Override
    @Transactional
    public OrderDTO createOrder(Long userId, Long warehouseId, Long addressId, Long courierId) {

        if (hasPendingOrder(userId)) {
            throw new OrderProcessingException("You have a pending order. Please complete your previous transaction first.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + addressId));
        Courier courier = courierRepository.findById(courierId)
                .orElseThrow(() -> new RuntimeException("Courier not found with id: " + courierId));

        Warehouse nearestWarehouse = warehouseRepository.findNearestWarehouse(address.getLon(), address.getLat());
        if (nearestWarehouse == null) {
            throw new RuntimeException("No warehouse found");
        }

        Cart cart = cartService.getCartEntity(userId);

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot create order with empty cart");
        }

        Order order = new Order();
        order.setUser(user);
        String invoiceId = generateInvoiceId();
        order.setInvoiceId(invoiceId);
        order.setWarehouse(nearestWarehouse);
        order.setAddress(address);
        order.setCourier(courier);
        order.setStatus(OrderStatus.pending_payment);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setPaymentMethod(null);

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
            totalQuantity += cartItem.getQuantity();
            totalWeight += cartItem.getProduct().getWeight() * cartItem.getQuantity();
        }

        order.setOriginalAmount(originalAmount);
        order.setTotalQuantity(totalQuantity);
        order.setTotalWeight(totalWeight);

        //Get shipping cost directly from courier service
        Integer shippingCost = courierService.getCourierPrice(courierId);
        order.setShippingCost(BigDecimal.valueOf(shippingCost));

        // Calculate final amount
        BigDecimal finalAmount = originalAmount.add(BigDecimal.valueOf(shippingCost));
        order.setFinalAmount(finalAmount);

        Order savedOrder = orderRepository.save(order);

        return convertToDTO(savedOrder);
    }

    private boolean hasPendingOrder(Long userId) {
        return orderRepository.existsByUserIdAndStatus(userId, OrderStatus.pending_payment);
    }
    //


    private String generateInvoiceId() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DATE_FORMATTER);
        String randomPart = String.format("%04d", random.nextInt(10000));
        return INVOICE_PREFIX + "-" + datePart + "-" + randomPart;
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
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        OrderStatus oldStatus = order.getStatus();
        log.info("Updating order status for order {}: {} -> {}", orderId, oldStatus, newStatus);

        if (newStatus == OrderStatus.shipped) {
            log.info("Order {} is shipped. Reducing stock.", orderId);
            List<InsufficientStockItem> insufficientItems = new ArrayList<>();
            for (OrderItem item : order.getItems()) {
                try {
                    stockService.reduceStock(item.getProduct().getId(), order.getWarehouse().getId(), item.getQuantity());
                    log.info("Reduced stock for product {} in warehouse {} by {}",
                            item.getProduct().getId(), order.getWarehouse().getId(), item.getQuantity());
                } catch (InsufficientStockException e) {
                    insufficientItems.add(new InsufficientStockItem(
                            item.getProduct().getName(),
                            item.getQuantity(),
                            e.getAvailableQuantity(),
                            order.getWarehouse().getName()
                    ));
                }
            }
            if (!insufficientItems.isEmpty()) {
                throw new OrderInsufficientStockException("Insufficient stock for some items", insufficientItems);
            }
        }
        // If the order is being confirmed, we should ensure it has a payment method
        if (newStatus == OrderStatus.confirmation && order.getPaymentMethod() == null) {
            Payment payment = paymentRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
            order.setPaymentMethod(payment.getPaymentMethod());
            recordStockMutationJournal(order);
        } else if (newStatus == OrderStatus.process && oldStatus != OrderStatus.process) {
            autoStockMutationService.processOrderAndCreateMutationIfNeeded(order);
        }
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        Order updatedOrder = orderRepository.save(order);
        log.info("Order {} status updated successfully", orderId);
        return convertToDTO(updatedOrder);
    }
    private void recordStockMutationJournal(Order order) {
        for (OrderItem item : order.getItems()) {
            StockMutationJournal journal = new StockMutationJournal();
            journal.setWarehouse(order.getWarehouse());
            journal.setMutationType(StockMutationJournal.MutationType.OUT);
            journal.setCreatedAt(LocalDateTime.now());
            stockMutationJournalRepository.save(journal);
        }
    }

    private void handleOrderCancellation(Order order, OrderStatus oldStatus) {
        if (oldStatus == OrderStatus.shipped || oldStatus == OrderStatus.delivered) {
            throw new IllegalStateException("Cannot cancel an order that has been shipped or delivered");
        }

        if (oldStatus == OrderStatus.confirmation || oldStatus == OrderStatus.process) {
            for (OrderItem item : order.getItems()) {
                stockService.returnStock(item.getProduct().getId(), order.getWarehouse().getId(), item.getQuantity());
            }
        }
    }


    @Override
    @Transactional
    public OrderDTO cancelOrder(Long orderId) throws IllegalStateException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (order.getStatus() == OrderStatus.shipped || order.getStatus() == OrderStatus.delivered) {
            throw new IllegalStateException("Cannot cancel an order that has been shipped or delivered");
        }

        if (order.getStatus() == OrderStatus.confirmation || order.getStatus() == OrderStatus.process) {
            returnStockForOrder(order);
        }

        order.setStatus(OrderStatus.cancelled);
        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    private void returnStockForOrder(Order order) {
        for (OrderItem item : order.getItems()) {
            stockService.returnStock(
                    item.getProduct().getId(),
                    order.getWarehouse().getId(),
                    item.getQuantity()
            );
        }
    }

    @Override
    @Transactional
    public OrderDTO markOrderAsDelivered(Long orderId) throws IllegalStateException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.shipped) {
            throw new IllegalStateException("Only shipped orders can be marked as delivered");
        }

        order.setStatus(OrderStatus.delivered);
        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    private void handleOrderConfirmation(Order order) {
        if (order.getWarehouse() == null) {
            Warehouse nearestWarehouse = warehouseService.findNearestWarehouse(order.getUser().getEmail());
            order.setWarehouse(nearestWarehouse);
        }

        List<String> insufficientStockItems = new ArrayList<>();

        for (OrderItem item : order.getItems()) {
            try {
                stockService.reduceStock(item.getProduct().getId(), order.getWarehouse().getId(), item.getQuantity());
            } catch (InsufficientStockException e) {
                insufficientStockItems.add(item.getProduct().getName() + " (Ordered: " + item.getQuantity() + ")");
            }
        }

        if (!insufficientStockItems.isEmpty()) {
            // Rollback the entire operation
            throw new OrderProcessingException("Insufficient stock for items: " + String.join(", ", insufficientStockItems));
        }
    }


    @Override
    public Page<OrderDTO> getAdminOrders(String status, Long warehouseId, LocalDate date, Pageable pageable) {
        Specification<Order> spec = Specification.where(null);

        if (status != null && !status.isEmpty()) {
            try {
                OrderStatus orderStatus = OrderStatus.fromString(status);
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), orderStatus));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid order status: " + status);
            }
        }
        if (warehouseId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("warehouse").get("id"), warehouseId));
        }
        if (date != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.function("DATE", LocalDate.class, root.get("createdAt")), date));
        }
        Page<Order> orders = orderRepository.findAll(spec, pageable);
        return orders.map(this::convertToDTO);
    }


    private OrderStatus convertToOrderStatus(String status) {
        if (status == null || status.isEmpty()) {
            return null;
        }
        try {
            return OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid order status: {}", status);
            return null;
        }
    }

    @Override
    @Transactional
    public OrderDTO updateOrder(OrderDTO orderDTO) {
        Order order = orderRepository.findById(orderDTO.getId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderDTO.getId()));

        // Update fields as necessary
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        // ... update other fields as needed ...

        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }


    @Override
    @Transactional
    public OrderDTO shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.process) {
            throw new IllegalStateException("Order must be in 'process' status to be shipped");
        }

        autoStockMutationService.processOrderAndCreateMutationIfNeeded(order);

        try {
            for (OrderItem item : order.getItems()) {
                stockService.reduceStock(item.getProduct().getId(), order.getWarehouse().getId(), item.getQuantity());
            }
        } catch (InsufficientStockException e) {
            throw new OrderProcessingException("Failed to ship order due to insufficient stock after auto mutation");
        }

        order.setStatus(OrderStatus.shipped);
        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Override
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(this::convertToDTO);
    }

    @Override
    @Transactional
    public void cancelUnpaidOrders() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Order> unpaidOrders = orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.pending_payment, oneHourAgo);

        for (Order order : unpaidOrders) {
            try {
                cancelOrder(order.getId());
                log.info("Automatically cancelled unpaid order: {}", order.getId());
            } catch (Exception e) {
                log.error("Failed to cancel unpaid order: {}", order.getId(), e);
            }
        }
    }

    @Override
    @Transactional
    public void autoUpdateShippedOrders() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Order> shippedOrders = orderRepository.findByStatusAndUpdatedAtBefore(OrderStatus.shipped, sevenDaysAgo);

        for (Order order : shippedOrders) {
            try {
                order.setStatus(OrderStatus.delivered);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
                log.info("Automatically updated order {} from shipped to delivered", order.getId());
            } catch (Exception e) {
                log.error("Failed to auto-update order: {}", order.getId(), e);
            }
        }
    }

    @Override
    public Page<OrderDTO> getUserOrders(Long userId, String status, LocalDate date, Pageable pageable) {
        Specification<Order> spec = Specification.where((root, query, cb) -> cb.equal(root.get("user").get("id"), userId));

        if (status != null && !status.isEmpty()) {
            try {
                OrderStatus orderStatus = OrderStatus.fromString(status);
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), orderStatus));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid order status: " + status);
            }
        }

        if (date != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.function("DATE", LocalDate.class, root.get("createdAt")), date));
        }

        Page<Order> orders = orderRepository.findAll(spec, pageable);
        return orders.map(this::convertToDTO);
    }


    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setInvoiceId(order.getInvoiceId());
        dto.setUserId(order.getUser().getId());
        dto.setWarehouseId(order.getWarehouse().getId());
        dto.setWarehouseName(order.getWarehouse().getName());
        dto.setAddressId(order.getAddress().getId());
        dto.setItems(order.getItems().stream().map(this::convertToOrderItemDTO).collect(Collectors.toList()));
        dto.setOrderDate(order.getCreatedAt().toLocalDate());
        dto.setStatus(order.getStatus().name());
        dto.setOriginalAmount(order.getOriginalAmount());
        dto.setShippingCost(order.getShippingCost());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setTotalWeight(order.getTotalWeight());
        dto.setTotalQuantity(order.getTotalQuantity());
        dto.setCourierId(order.getCourier().getId());
        dto.setCourierName(order.getCourier().getCourier());
        dto.setOriginCity(order.getWarehouse().getCity().getName());
        dto.setDestinationCity(order.getAddress().getCity().getName());

        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        if (payment != null) {
            dto.setPaymentMethod(payment.getPaymentMethod());
        }
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
