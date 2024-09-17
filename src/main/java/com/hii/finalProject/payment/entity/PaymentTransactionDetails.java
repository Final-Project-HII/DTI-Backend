package com.hii.finalProject.payment.entity;

import lombok.Data;

@Data
public class PaymentTransactionDetails {
    private String order_id;
    private int gross_amount;
}