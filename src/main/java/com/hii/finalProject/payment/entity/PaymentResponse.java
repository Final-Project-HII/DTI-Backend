package com.hii.finalProject.payment.entity;

import lombok.Data;

import java.util.List;

@Data
public class PaymentResponse {
    private String status_code;
    private String status_message;
    private String transaction_id;
    private String order_id;
    private String merchant_id;
    private String gross_amount;
    private String currency;
    private String payment_type;
    private String transaction_time;
    private String transaction_status;
    private String fraud_status;
    private List<VaNumber> va_numbers;
    private String expiry_time;
}
