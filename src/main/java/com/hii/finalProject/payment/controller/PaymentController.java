package com.hii.finalProject.payment.controller;

import com.hii.finalProject.payment.entity.PaymentRequest;
import com.hii.finalProject.payment.entity.PaymentStatus;
import com.hii.finalProject.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createPayment(@RequestParam Long orderId, @RequestParam String bank) {
        try {
            String response = paymentService.createVirtualAccountCode(orderId, bank);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating payment: " + e.getMessage());
        }
    }

    @GetMapping("/status/{orderId}")
    public ResponseEntity<Map<String, String>> checkPaymentStatus(@PathVariable String orderId) {
        try {
            String transactionStatus = paymentService.getTransactionStatus(orderId);
            return ResponseEntity.ok(Map.of("transaction_status", transactionStatus));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/manual")
    public ResponseEntity<String> processManualPayment(@RequestParam Long orderId, @RequestParam String proofImageUrl) {
        try {
            String response = paymentService.processManualPayment(orderId, proofImageUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing manual payment: " + e.getMessage());
        }
    }

    @PostMapping("/simulate-status")
    public ResponseEntity<String> simulatePaymentStatusChange(@RequestParam Long orderId, @RequestParam PaymentStatus newStatus) {
        try {
            paymentService.simulatePaymentStatusChange(orderId, newStatus);
            return ResponseEntity.ok("Payment status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating payment status: " + e.getMessage());
        }
    }

//    @PostMapping("/create")
//    public String createPayment(@RequestBody PaymentRequest paymentRequest) {
//        return paymentService.createTransaction(paymentRequest);
//
//    }

}