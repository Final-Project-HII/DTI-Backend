package com.hii.finalProject.payment.controller;

import com.hii.finalProject.payment.entity.Payment;
import com.hii.finalProject.payment.entity.PaymentMethod;
import com.hii.finalProject.payment.entity.PaymentRequest;
import com.hii.finalProject.payment.entity.PaymentStatus;
import com.hii.finalProject.payment.service.PaymentService;
import com.hii.finalProject.paymentProof.entity.PaymentProof;
import com.hii.finalProject.paymentProof.service.PaymentProofService;
import com.hii.finalProject.paymentProof.service.impl.PaymentProofServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentProofService paymentProofService;

    public PaymentController(PaymentService paymentService, PaymentProofService paymentProofService) {
        this.paymentService = paymentService;
        this.paymentProofService = paymentProofService;
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
    public ResponseEntity<?> getPaymentStatus(@PathVariable Long orderId) {
        try {
            Payment payment = paymentService.getPaymentByOrderId(orderId);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", payment.getOrderId());
            response.put("amount", payment.getAmount());
            response.put("paymentMethod", payment.getPaymentMethod().toString());
            response.put("status", payment.getStatus().toString());
            response.put("createdAt", payment.getCreatedAt());

            if (payment.getPaymentMethod() == PaymentMethod.PAYMENT_PROOF) {
                PaymentProof paymentProof = payment.getPaymentProof();
                if (paymentProof != null) {
                    response.put("paymentProofUrl", paymentProof.getPaymentProofUrl());
                }
            } else if (payment.getPaymentMethod() == PaymentMethod.PAYMENT_GATEWAY) {
                LocalDateTime expirationTime = payment.getCreatedAt().plusHours(1);
                response.put("expirationTime", expirationTime);

                if (payment.getVirtualAccountBank() != null && payment.getVirtualAccountNumber() != null) {
                    Map<String, String> vaInfo = new HashMap<>();
                    vaInfo.put("bank", payment.getVirtualAccountBank());
                    vaInfo.put("va_number", payment.getVirtualAccountNumber());
                    response.put("va_numbers", new Map[]{vaInfo});
                }
            }

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", 404);
            errorResponse.put("message", "Payment not found for order: " + orderId);
            errorResponse.put("success", false);
            return ResponseEntity.status(404).body(errorResponse);
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