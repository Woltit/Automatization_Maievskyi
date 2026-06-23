package com.example.order;

import java.math.BigDecimal;

public interface PaymentGateway {
    boolean processPayment(String orderId, BigDecimal amount);
}
