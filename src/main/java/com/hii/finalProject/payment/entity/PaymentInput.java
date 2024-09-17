package com.hii.finalProject.payment.entity;

import lombok.Data;

import java.util.List;

@Data
public class PaymentInput {
    private String payment_type;
    private PaymentTransactionDetails transaction_details;
    private BankTransfer bank_transfer;
    private CustomerDetails customer_details;
    private List<ItemDetail> item_details;
}
