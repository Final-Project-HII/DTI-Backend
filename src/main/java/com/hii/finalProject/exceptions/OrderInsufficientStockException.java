package com.hii.finalProject.exceptions;

import java.util.List;

public class OrderInsufficientStockException extends RuntimeException {
    private final List<InsufficientStockItem> insufficientItems;

    public OrderInsufficientStockException(String message, List<InsufficientStockItem> insufficientItems) {
        super(message);
        this.insufficientItems = insufficientItems;
    }

    public List<InsufficientStockItem> getInsufficientItems() {
        return insufficientItems;
    }
}