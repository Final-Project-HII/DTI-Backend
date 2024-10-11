package com.hii.finalProject.exceptions;

public class InsufficientStockItem {
    private String productName;
    private int requiredQuantity;
    private int availableQuantity;
    private String warehouseName;

    public InsufficientStockItem(String productName, int requiredQuantity, int availableQuantity, String warehouseName) {
        this.productName = productName;
        this.requiredQuantity = requiredQuantity;
        this.availableQuantity = availableQuantity;
        this.warehouseName = warehouseName;
    }

    // Getters and setters

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getRequiredQuantity() {
        return requiredQuantity;
    }

    public void setRequiredQuantity(int requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }
}