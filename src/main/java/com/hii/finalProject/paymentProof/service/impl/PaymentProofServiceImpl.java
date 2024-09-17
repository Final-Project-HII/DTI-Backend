package com.hii.finalProject.paymentProof.service.impl;

import com.hii.finalProject.payment.entity.Payment;
import com.hii.finalProject.payment.repository.PaymentRepository;
import com.hii.finalProject.paymentProof.Repository.PaymentProofRepository;
import com.hii.finalProject.paymentProof.entity.PaymentProof;
import com.hii.finalProject.paymentProof.service.PaymentProofService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentProofServiceImpl implements PaymentProofService {
    private final PaymentProofRepository paymentProofRepository;
    private final PaymentRepository paymentRepository;

    public PaymentProofServiceImpl(PaymentProofRepository paymentProofRepository, PaymentRepository paymentRepository) {
        this.paymentProofRepository = paymentProofRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void savePaymentProof(Long paymentId, String proofUrl) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        PaymentProof paymentProof = new PaymentProof();
        paymentProof.setPayment(payment);
        paymentProof.setPaymentProofUrl(proofUrl);
        paymentProof.setCreatedAt(LocalDateTime.now());
        paymentProof.setUpdatedAt(LocalDateTime.now());

        paymentProofRepository.save(paymentProof);
    }
}
