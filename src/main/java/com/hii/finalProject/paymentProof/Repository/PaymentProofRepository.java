package com.hii.finalProject.paymentProof.Repository;

import com.hii.finalProject.payment.entity.Payment;
import com.hii.finalProject.paymentProof.entity.PaymentProof;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentProofRepository extends JpaRepository<PaymentProof, Long> {
    Optional<PaymentProof> findByPayment(Payment payment);
}