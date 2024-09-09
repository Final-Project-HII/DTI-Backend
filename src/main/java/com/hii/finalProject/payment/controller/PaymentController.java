package com.hii.finalProject.payment.controller;

import com.hii.finalProject.payment.entity.PaymentRequest;
import com.hii.finalProject.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

//    @PostMapping("/create")
//    public String createPayment(@RequestBody PaymentRequest paymentRequest) {
//        return paymentService.createTransaction(paymentRequest);
//
//    }

}