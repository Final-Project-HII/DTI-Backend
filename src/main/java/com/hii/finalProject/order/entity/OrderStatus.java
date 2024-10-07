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

    public static OrderStatus fromString(String status) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.name().equalsIgnoreCase(status)) {
                return orderStatus;
            }
        }
        throw new IllegalArgumentException("No enum constant " + OrderStatus.class.getCanonicalName() + "." + status);
    }
}