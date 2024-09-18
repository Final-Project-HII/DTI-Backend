package com.hii.finalProject.paymentProof.Repository;

import com.hii.finalProject.paymentProof.entity.PaymentProof;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentProofRepository extends JpaRepository<PaymentProof, Long> {
}