package com.hii.finalProject.payment.service;

import com.hii.finalProject.payment.entity.PaymentRequest;
import com.hii.finalProject.payment.entity.PaymentStatus;
import org.springframework.web.multipart.MultipartFile;

public interface PaymentService {
    String createTransaction(PaymentRequest paymentRequest);
    String createVirtualAccountCode(Long orderId, String bank);
    String getTransactionStatus(String orderId);
    String processManualPayment(Long orderId, String proofImageUrl);
    void updatePaymentStatus(Long orderId, PaymentStatus status);
    void simulatePaymentStatusChange(Long orderId, PaymentStatus newStatus);

}