package com.hii.finalProject.payment.entity;

import com.hii.finalProject.paymentProof.entity.PaymentProof;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PaymentProof paymentProof;

    @Column(name = "virtual_account_bank")
    private String virtualAccountBank;

    @Column(name = "virtual_account_number")
    private String virtualAccountNumber;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;

    public void setExpirationTimeWithOffset(OffsetDateTime offsetDateTime) {
        this.expirationTime = offsetDateTime != null ? offsetDateTime.toLocalDateTime() : null;
    }

    // New method to get expiration time with offset
    public OffsetDateTime getExpirationTimeWithOffset() {
        return this.expirationTime != null
                ? this.expirationTime.atOffset(ZoneOffset.UTC)
                : null;
    }
}