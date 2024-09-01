package com.hii.finalProject.payment.service;

import com.hii.finalProject.payment.entity.PaymentRequest;

public interface PaymentService {
    String createTransaction(PaymentRequest paymentRequest) ;
    String createVirtualAccountCode(String bookingCode, String bank);
}