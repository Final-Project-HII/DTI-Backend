package com.hii.finalProject.order.entity;

public enum OrderStatus {
    pending_payment,
    confirmation,
    process,
    shipped,
    delivered,
    cancelled;
//
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}