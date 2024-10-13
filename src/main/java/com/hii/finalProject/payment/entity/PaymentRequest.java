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
    private CustomExpiry custom_expiry;

    @Data
    public static class CustomExpiry {
        private Integer expiry_duration;
        // Note: We're not including a 'unit' field here, as it seems your friend's implementation uses only an integer for duration
    }

}
