package com.hii.finalProject.payment.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hii.finalProject.cart.entity.Cart;
import com.hii.finalProject.cart.service.CartService;
import com.hii.finalProject.order.dto.OrderDTO;
import com.hii.finalProject.order.entity.Order;
import com.hii.finalProject.order.service.OrderService;
import com.hii.finalProject.orderItem.dto.OrderItemDTO;
import com.hii.finalProject.payment.entity.*;
import com.hii.finalProject.payment.repository.PaymentRepository;
import com.hii.finalProject.payment.service.PaymentService;
import com.hii.finalProject.users.dto.UserDTO;
import com.hii.finalProject.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Log
@Service
public class PaymentServiceImpl implements PaymentService {
    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;
    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;

//    @Value("${midtrans.server.key}")
    private String serverKey = "SB-Mid-server-1YIRKrKNSAv83Cq4AdIKPKlB";

//    @Value("${midtrans.api.url}")
    private String apiUrl  = "https://api.sandbox.midtrans.com/v2/charge";

    public PaymentServiceImpl(OrderService orderService, UserService userService, CartService cartService, RestTemplateBuilder restTemplateBuilder, PaymentRepository paymentRepository) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
        this.restTemplate = restTemplateBuilder.build();
        this.paymentRepository = paymentRepository;
    }

    @Override
    public String createTransaction(PaymentRequest paymentRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((serverKey + ":").getBytes()));
        headers.set("Content-Type", "application/json");

        HttpEntity<PaymentRequest> request = new HttpEntity<>(paymentRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);

        return response.getBody();
    }

    @Override
    @Transactional
    public String createVirtualAccountCode(Long orderId, String bank) {
        OrderDTO order = orderService.getOrderById(orderId);
        Optional<UserDTO> user = userService.getUserById(order.getUserId());

        if (user.isEmpty()) {
            throw new RuntimeException("User not found for order: " + orderId);
        }

        String paymentType = "bank_transfer";
        PaymentTransactionDetails paymentTransactionDetails = new PaymentTransactionDetails();
        paymentTransactionDetails.setOrder_id(order.getId().toString());
        paymentTransactionDetails.setGross_amount(order.getTotalAmount().intValue());

        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setBank(bank);

        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setFirst_name(user.get().getName());
        customerDetails.setLast_name("");
        customerDetails.setEmail(user.get().getEmail());
        customerDetails.setPhone(user.get().getPhoneNumber());

        List<ItemDetail> itemDetailsList = new ArrayList<>();
        for (OrderItemDTO item : order.getItems()) {
            ItemDetail itemDetail = new ItemDetail();
            itemDetail.setId(item.getProductId().toString());
            itemDetail.setName(item.getProductName());
            itemDetail.setPrice(item.getPrice().intValue());
            itemDetail.setQuantity(item.getQuantity());
            itemDetailsList.add(itemDetail);
        }

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPayment_type(paymentType);
        paymentRequest.setTransaction_details(paymentTransactionDetails);
        paymentRequest.setBank_transfer(bankTransfer);
        paymentRequest.setCustomer_details(customerDetails);
        paymentRequest.setItem_details(itemDetailsList);

        String transactionResponse = createTransaction(paymentRequest);

        // Clear the cart after successful VA generation
        Cart cart = cartService.getCartEntity(order.getUserId());
        cart.getItems().clear();
        cartService.updateCart(order.getUserId(), cart);

        return transactionResponse;
    }


    @Override
    public String getTransactionStatus(String orderId) {
        String url = "https://api.sandbox.midtrans.com/v2/" + orderId + "/status";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((serverKey + ":").getBytes()));
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                return root.path("transaction_status").asText();
            } catch (Exception e) {
                throw new RuntimeException("Error parsing Midtrans response", e);
            }
        } else {
            throw new RuntimeException("Error fetching transaction status from Midtrans");
        }
    }

    @Override
    @Transactional
    public String processManualPayment(Long orderId, String proofImageUrl) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(BigDecimal.valueOf(orderDTO.getTotalAmount()));
        payment.setPaymentMethod(PaymentMethod.PAYMENT_PROOF);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setName("Manual Payment for Order " + orderId);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setProofImageUrl(proofImageUrl);

        paymentRepository.save(payment);

        // Update order status
        orderService.updateOrderStatus(orderId, "PAYMENT_PENDING");

        return "Manual payment proof received and being processed for order: " + orderId;
    }

    @Override
    @Transactional
    public void updatePaymentStatus(Long orderId, PaymentStatus status) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));

        payment.setStatus(status);
        paymentRepository.save(payment);

        String orderStatus = status == PaymentStatus.COMPLETED ? "PAID" : "PAYMENT_FAILED";
        orderService.updateOrderStatus(orderId, orderStatus);
    }

    @Override
    @Transactional
    public void simulatePaymentStatusChange(Long orderId, PaymentStatus newStatus) {
        updatePaymentStatus(orderId, newStatus);
    }
}