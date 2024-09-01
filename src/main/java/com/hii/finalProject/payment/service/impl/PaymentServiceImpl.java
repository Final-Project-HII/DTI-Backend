package com.hii.finalProject.payment.service.impl;

import com.hii.finalProject.cart.entity.Cart;
import com.hii.finalProject.cart.service.CartService;
import com.hii.finalProject.order.dto.OrderDTO;
import com.hii.finalProject.order.service.OrderService;
import com.hii.finalProject.orderItem.dto.OrderItemDTO;
import com.hii.finalProject.payment.entity.*;
import com.hii.finalProject.payment.service.PaymentService;
import com.hii.finalProject.users.dto.UserDTO;
import com.hii.finalProject.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

//    @Value("${midtrans.server.key}")
    private String serverKey = "SB-Mid-server-1YIRKrKNSAv83Cq4AdIKPKlB";

//    @Value("${midtrans.api.url}")
    private String apiUrl = "https://api.sandbox.midtrans.com/v2/charge";

    public PaymentServiceImpl(OrderService orderService, UserService userService, CartService cartService) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
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
}