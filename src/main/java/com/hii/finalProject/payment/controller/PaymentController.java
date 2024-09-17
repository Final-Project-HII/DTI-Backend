package com.hii.finalProject.payment.controller;

import com.hii.finalProject.payment.entity.PaymentMethod;
import com.hii.finalProject.payment.entity.PaymentRequest;
import com.hii.finalProject.payment.entity.PaymentStatus;
import com.hii.finalProject.payment.service.PaymentService;
import com.hii.finalProject.paymentProof.service.impl.PaymentProofServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentProofServiceImpl paymentProofServiceImpl;

    public PaymentController(PaymentService paymentService, PaymentProofServiceImpl paymentProofServiceImpl) {
        this.paymentService = paymentService;
        this.paymentProofServiceImpl = paymentProofServiceImpl;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createPayment(
            @RequestParam Long orderId,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String bank,
            @RequestParam(required = false) String proofImageUrl
    ) {
        PaymentMethod method;
        try {
            method = PaymentMethod.valueOf(paymentMethod.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid payment method: " + paymentMethod);
        }

        try {
            String result = paymentService.processPayment(orderId, method, bank, proofImageUrl);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create payment: " + e.getMessage());
        }
    }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<String> getPaymentStatus(@PathVariable Long orderId) {
        try {
            PaymentStatus status = paymentService.getTransactionStatus(orderId);
            return ResponseEntity.ok(status.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve payment status");
        }
    }

    @PostMapping("/{orderId}/approve-proof")
    public ResponseEntity<String> approvePaymentProof(@PathVariable Long orderId) {
        try {
            paymentService.updatePaymentStatus(orderId, PaymentStatus.COMPLETED);
            return ResponseEntity.ok("Payment proof approved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to approve payment proof");
        }
    }

    @PostMapping("/{orderId}/reject-proof")
    public ResponseEntity<String> rejectPaymentProof(@PathVariable Long orderId) {
        try {
            paymentService.updatePaymentStatus(orderId, PaymentStatus.FAILED);
            return ResponseEntity.ok("Payment proof rejected successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reject payment proof");
        }
    }

    @PostMapping("/approve-payment/{paymentId}")
    public ResponseEntity<String> approvePayment(@PathVariable Long paymentId) {
        try {
            // Update the payment status to COMPLETED
            paymentService.updatePaymentStatus(paymentId, PaymentStatus.COMPLETED);
            return ResponseEntity.ok("Payment approved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to approve payment");
        }
    }

    @PostMapping("/midtrans-callback")
    public ResponseEntity<String> handleMidtransCallback(@RequestBody String callbackPayload) {
        try {
            paymentService.processMidtransCallback(callbackPayload);
            return ResponseEntity.ok("Callback processed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process Midtrans callback: " + e.getMessage());
        }
    }


}