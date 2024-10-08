package com.hii.finalProject.payment.service;

import com.hii.finalProject.payment.entity.Payment;
import com.hii.finalProject.payment.entity.PaymentMethod;
import com.hii.finalProject.payment.entity.PaymentRequest;
import com.hii.finalProject.payment.entity.PaymentStatus;

public interface PaymentService {
    String createTransaction(PaymentRequest paymentRequest);

    String processPayment(Long orderId, PaymentMethod paymentMethod, String bank, String proofImageUrl);

    PaymentStatus getTransactionStatus(Long orderId);

    Payment getPaymentByOrderId(Long orderId);

    void processMidtransCallback(String callbackPayload) throws Exception;

    void updatePaymentStatus(Long orderId, PaymentStatus status);

    void simulatePaymentStatusChange(Long orderId, PaymentStatus newStatus);
}