package com.hii.finalProject.exceptions;

public class InsufficientStockException extends RuntimeException {
    private final int availableQuantity;

    public InsufficientStockException(String message, int availableQuantity) {
        super(message);
        this.availableQuantity = availableQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}
