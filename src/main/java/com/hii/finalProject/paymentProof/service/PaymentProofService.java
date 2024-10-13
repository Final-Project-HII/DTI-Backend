package com.hii.finalProject.paymentProof.service;

import com.hii.finalProject.payment.entity.Payment;
import com.hii.finalProject.paymentProof.entity.PaymentProof;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentProofService {
    void savePaymentProof(Long paymentId, String proofUrl);

    @Transactional(readOnly = true)
    PaymentProof getPaymentProofByPayment(Payment payment);
}
