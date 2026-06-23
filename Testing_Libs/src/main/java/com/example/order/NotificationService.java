package com.example.order;

public interface NotificationService {
    void sendEmail(String orderId, String message);

    void logOrderProcessed(String orderId);
}
