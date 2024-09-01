//package com.hii.finalProject.payment.service.impl;
//
//import com.hii.finalProject.payment.entity.BankTransfer;
//import com.hii.finalProject.payment.entity.CustomerDetails;
//import com.hii.finalProject.payment.entity.ItemDetail;
//import com.hii.finalProject.payment.entity.PaymentRequest;
//import com.hii.finalProject.payment.service.PaymentService;
//import jakarta.transaction.Transactional;
//import lombok.extern.java.Log;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.ArrayList;
//import java.util.Base64;
//import java.util.List;
//
//@Log
//@Service
//public class PaymentServiceImpl implements PaymentService {
//    private TransactionService transactionService;
//    public PaymentServiceImpl(TransactionService transactionService) {
//        this.transactionService = transactionService;
//    }
//    @Value("${midtrans.server.key}")
//    private String serverKey;
//
//    @Value("${midtrans.api.url}")
//    private String apiUrl;
//
//    @Override
//    public String createTransaction(PaymentRequest paymentRequest) {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((serverKey + ":").getBytes()));
//        headers.set("Content-Type", "application/json");
//        log.info("ini serverkeynya: " + Base64.getEncoder().encodeToString((serverKey + ":").getBytes()));
//
//        HttpEntity<PaymentRequest> request = new HttpEntity<>(paymentRequest, headers);
//        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
//
//        return response.getBody();
//    }
//
//    @Override
//    @Transactional
//    public String createVirtualAccountCode(String bookingCode, String bank){
//        TransactionResponse transaction = transactionService.getTransactionResponseByBookingCode(bookingCode);
//
//        String PaymentType = "bank_transfer";
//        PaymentTransactionDetails paymentTransactionDetails = new PaymentTransactionDetails();
//        paymentTransactionDetails.setOrder_id(transaction.getBookingCode());
//        paymentTransactionDetails.setGross_amount(transaction.getFinalPrice().intValue());
//
//        BankTransfer bankTransfer = new BankTransfer();
//        bankTransfer.setBank(bank);
//
//        CustomerDetails customerDetails = new CustomerDetails();
//        customerDetails.setFirst_name(transaction.getFirstName());
//        customerDetails.setLast_name(transaction.getLastName());
//        customerDetails.setEmail(transaction.getUsers().getEmail());
//        customerDetails.setPhone(transaction.getMobileNumber());
//
//        List<ItemDetail> itemDetailsList = new ArrayList<>();
//        ItemDetail itemDetail = new ItemDetail();
//        itemDetail.setName(transaction.getProperties().getName());
//        itemDetail.setQuantity(1);
//        itemDetail.setPrice(transaction.getFinalPrice().intValue());
//        itemDetailsList.add(itemDetail);
//
//        PaymentRequest paymentRequest = new PaymentRequest();
//        paymentRequest.setPayment_type(PaymentType);
//        paymentRequest.setTransaction_details(paymentTransactionDetails);
//        paymentRequest.setBank_transfer(bankTransfer);
//        paymentRequest.setCustomer_details(customerDetails);
//        paymentRequest.setItem_details(itemDetailsList);
//
//        return createTransaction(paymentRequest);
//    }
//}
