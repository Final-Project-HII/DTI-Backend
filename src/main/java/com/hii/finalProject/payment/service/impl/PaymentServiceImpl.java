package com.hii.finalProject.payment.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hii.finalProject.cart.entity.Cart;
import com.hii.finalProject.cart.service.CartService;
import com.hii.finalProject.order.dto.OrderDTO;
import com.hii.finalProject.order.entity.OrderStatus;
import com.hii.finalProject.order.service.OrderService;
import com.hii.finalProject.orderItem.dto.OrderItemDTO;
import com.hii.finalProject.payment.entity.*;
import com.hii.finalProject.payment.repository.PaymentRepository;
import com.hii.finalProject.payment.service.PaymentService;
import com.hii.finalProject.paymentProof.Repository.PaymentProofRepository;
import com.hii.finalProject.paymentProof.entity.PaymentProof;
import com.hii.finalProject.users.dto.UserDTO;
import com.hii.finalProject.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    private final PaymentProofRepository paymentProofRepository;
    private final ObjectMapper jacksonObjectMapper;
    private final TaskScheduler taskScheduler;

    private String serverKey = "SB-Mid-server-1YIRKrKNSAv83Cq4AdIKPKlB";
    private String apiUrl  = "https://api.sandbox.midtrans.com/v2/charge";

    public PaymentServiceImpl(OrderService orderService, UserService userService, CartService cartService,
                              RestTemplateBuilder restTemplateBuilder, PaymentRepository paymentRepository,
                              PaymentProofRepository paymentProofRepository, ObjectMapper jacksonObjectMapper, TaskScheduler taskScheduler) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
        this.restTemplate = restTemplateBuilder.build();
        this.paymentRepository = paymentRepository;
        this.paymentProofRepository = paymentProofRepository;
        this.jacksonObjectMapper = jacksonObjectMapper;
        this.taskScheduler = taskScheduler;
    }

    @Override
    public String createTransaction(PaymentRequest paymentRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((serverKey + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PaymentRequest> request = new HttpEntity<>(paymentRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);

        return response.getBody();
    }

    @Override
    @Transactional
    public String processPayment(Long orderId, PaymentMethod paymentMethod, String bank, String proofImageUrl) {
        OrderDTO order = orderService.getOrderById(orderId);
        Optional<UserDTO> user = userService.getUserById(order.getUserId());

        if (user.isEmpty()) {
            throw new RuntimeException("User not found for order: " + orderId);
        }

        order.setPaymentMethod(paymentMethod);
        orderService.updateOrder(order);

        String result;

        if (paymentMethod == PaymentMethod.PAYMENT_GATEWAY) {
            PaymentRequest paymentRequest = createPaymentRequest(order, user.get(), bank);
            String transactionResponse = createTransaction(paymentRequest);

            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setAmount(order.getFinalAmount());
            payment.setPaymentMethod(PaymentMethod.PAYMENT_GATEWAY);
            payment.setStatus(PaymentStatus.PENDING);
            payment.setName("Midtrans Payment for Order " + orderId);
            payment.setCreatedAt(LocalDateTime.now());
            schedulePaymentExpirationCheck(orderId, LocalDateTime.now().plusHours(1));

            try {
                JsonNode responseJson = jacksonObjectMapper.readTree(transactionResponse);
                if (responseJson.has("va_numbers") && responseJson.get("va_numbers").isArray()) {
                    JsonNode vaNumbers = responseJson.get("va_numbers").get(0);
                    String vaBank = vaNumbers.get("bank").asText();
                    String vaNumber = vaNumbers.get("va_number").asText();

                    payment.setVirtualAccountBank(vaBank);
                    payment.setVirtualAccountNumber(vaNumber);
                }

                if (responseJson.has("expiry_time")) {
                    String expiryTime = responseJson.get("expiry_time").asText();
                    payment.setExpiryTime(LocalDateTime.parse(expiryTime));
                }
            } catch (Exception e) {
                throw new RuntimeException("Error parsing Midtrans response: " + e.getMessage());
            }


            paymentRepository.save(payment);

            result = transactionResponse;
        } else if (paymentMethod == PaymentMethod.PAYMENT_PROOF) {
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setAmount(order.getFinalAmount());
            payment.setPaymentMethod(PaymentMethod.PAYMENT_PROOF);
            payment.setStatus(PaymentStatus.PENDING);
            payment.setName("Manual Payment for Order " + orderId);
            payment.setCreatedAt(LocalDateTime.now());

            Payment savedPayment = paymentRepository.save(payment);

            PaymentProof paymentProof = new PaymentProof();
            paymentProof.setPayment(savedPayment);
            paymentProof.setPaymentProofUrl(proofImageUrl);
            paymentProof.setCreatedAt(LocalDateTime.now());
            paymentProof.setUpdatedAt(LocalDateTime.now());

            paymentProofRepository.save(paymentProof);

            orderService.updateOrderStatus(orderId, OrderStatus.pending_payment);

            result = "Manual payment proof received and being processed for order: " + orderId;
        } else {
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        }

        try {
            Cart cart = cartService.getCartEntity(order.getUserId());
            cart.getItems().clear();
            cartService.updateCart(order.getUserId(), cart);
            log.info("Cart cleared for user: " + order.getUserId() + " after payment initiation for order: " + orderId);
        } catch (Exception e) {
            log.warning("Failed to clear cart for user: " + order.getUserId() + " after payment initiation. Error: " + e.getMessage());
        }

        return result;
    }

    private void schedulePaymentExpirationCheck(Long orderId, LocalDateTime expiryTime) {
        taskScheduler.schedule(() -> {
            Payment payment = getPaymentByOrderId(orderId);
            if (payment.getStatus() == PaymentStatus.PENDING) {
                updatePaymentStatus(orderId, PaymentStatus.FAILED);
                orderService.updateOrderStatus(orderId, OrderStatus.cancelled);
            }
        }, expiryTime.toInstant(ZoneOffset.UTC));
    }

    @Override
    public PaymentStatus getTransactionStatus(Long orderId) {
        Optional<Payment> paymentOptional = paymentRepository.findByOrderId(orderId);

        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();

            if (payment.getPaymentMethod() == PaymentMethod.PAYMENT_GATEWAY) {
                return getMidtransTransactionStatus(orderId);
            } else if (payment.getPaymentMethod() == PaymentMethod.PAYMENT_PROOF) {
                return payment.getStatus();
            }
        }

        return PaymentStatus.FAILED;
    }

    @Override
    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }

    private PaymentStatus getMidtransTransactionStatus(Long orderId) {
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
                String transactionStatus = root.path("transaction_status").asText();

                return mapMidtransStatus(transactionStatus);
            } catch (Exception e) {
                log.warning("Error parsing Midtrans response: " + e.getMessage());
                return PaymentStatus.FAILED;
            }
        }

        return PaymentStatus.FAILED;
    }

    @Override
    @Transactional
    public void processMidtransCallback(String callbackPayload) throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(callbackPayload);

            String orderId = root.path("order_id").asText();
            String transactionStatus = root.path("transaction_status").asText();
            String grossAmount = root.path("gross_amount").asText();

            log.info("Processing Midtrans callback for order: " + orderId + " with status: " + transactionStatus);

            Payment payment = paymentRepository.findByOrderId(Long.parseLong(orderId))
                    .orElseGet(() -> createNewPayment(Long.parseLong(orderId), new BigDecimal(grossAmount)));

            PaymentStatus paymentStatus = mapMidtransStatus(transactionStatus);
            payment.setStatus(paymentStatus);
            paymentRepository.save(payment);

            OrderStatus orderStatus = mapToOrderStatus(paymentStatus);
            orderService.updateOrderStatus(Long.parseLong(orderId), orderStatus);

            log.info("Successfully processed Midtrans callback for order: " + orderId);
        } catch (Exception e) {
            log.severe("Error processing Midtrans callback: " + e.getMessage());
            throw new Exception("Failed to process Midtrans callback: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void updatePaymentStatus(Long orderId, PaymentStatus status) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));

        payment.setStatus(status);
        paymentRepository.save(payment);

        OrderStatus orderStatus = mapToOrderStatus(status);
        orderService.updateOrderStatus(orderId, orderStatus);
    }

    @Override
    @Transactional
    public void simulatePaymentStatusChange(Long orderId, PaymentStatus newStatus) {
        updatePaymentStatus(orderId, newStatus);
    }

    private Payment createNewPayment(Long orderId, BigDecimal amount) {
        log.info("Creating new payment record for order: " + orderId);
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setPaymentMethod(PaymentMethod.PAYMENT_GATEWAY);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setName("Midtrans Payment for Order " + orderId);
        payment.setCreatedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    private PaymentStatus mapMidtransStatus(String transactionStatus) {
        switch (transactionStatus) {
            case "capture":
            case "settlement":
                return PaymentStatus.COMPLETED;
            case "pending":
                return PaymentStatus.PENDING;
            case "deny":
            case "cancel":
            case "expire":
                return PaymentStatus.FAILED;
            case "refund":
                return PaymentStatus.REFUNDED;
            default:
                return PaymentStatus.FAILED;
        }
    }

    private OrderStatus mapToOrderStatus(PaymentStatus paymentStatus) {
        switch (paymentStatus) {
            case COMPLETED:
                return OrderStatus.confirmation;
            case PENDING:
                return OrderStatus.pending_payment;
            case FAILED:
            case REFUNDED:
                return OrderStatus.cancelled;
            default:
                throw new IllegalArgumentException("Unknown payment status: " + paymentStatus);
        }
    }

    private PaymentRequest createPaymentRequest(OrderDTO order, UserDTO user, String bank) {
        String paymentType = "bank_transfer";
        PaymentTransactionDetails paymentTransactionDetails = new PaymentTransactionDetails();
        paymentTransactionDetails.setOrder_id(order.getId().toString());
        paymentTransactionDetails.setGross_amount(order.getOriginalAmount().intValue());

        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setBank(bank);

        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setFirst_name(user.getName());
        customerDetails.setLast_name("");
        customerDetails.setEmail(user.getEmail());
        customerDetails.setPhone(user.getPhoneNumber());

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

        PaymentRequest.CustomExpiryOptions customExpiry = new PaymentRequest.CustomExpiryOptions();
        customExpiry.setOrder_time(LocalDateTime.now().toString());
        customExpiry.setExpiry_duration("1");
        customExpiry.setUnit("hour");
        paymentRequest.setCustom_expiry(customExpiry);

        return paymentRequest;
    }
}