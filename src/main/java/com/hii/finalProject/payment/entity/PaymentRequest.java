package com.hii.finalProject.payment.entity;

import lombok.Data;

import java.util.List;

@Data
public class PaymentRequest {
    private String payment_type;
    private PaymentTransactionDetails transaction_details;
    private BankTransfer bank_transfer;
    private CustomerDetails customer_details;
    private List<ItemDetail> item_details;
    private CustomExpiryOptions custom_expiry;

    @Data
    public static class CustomExpiryOptions {
        private String order_time;
        private String expiry_duration;
        private String unit;
    }
}
