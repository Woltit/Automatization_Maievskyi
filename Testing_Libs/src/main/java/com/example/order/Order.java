package com.example.order;

import java.math.BigDecimal;

public class Order {
    private String id;
    private String itemCode;
    private int quantity;
    private BigDecimal totalAmount;
    private String status;
    private String message;

    public Order(String id, String itemCode, int quantity, BigDecimal totalAmount) {
        this.id = id;
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    public String getId() {
        return id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
