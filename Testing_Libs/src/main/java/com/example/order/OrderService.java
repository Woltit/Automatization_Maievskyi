package com.example.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private final WarehouseService warehouseService;
    private final PaymentGateway paymentGateway;
    private final NotificationService notificationService;

    private final List<Order> orderHistory = new ArrayList<>();

    public OrderService(WarehouseService warehouseService, PaymentGateway paymentGateway,
            NotificationService notificationService) {
        this.warehouseService = warehouseService;
        this.paymentGateway = paymentGateway;
        this.notificationService = notificationService;
    }

    public void processOrder(Order order) {
        // 1. Перевірка наявності товару
        if (!warehouseService.isAvailable(order.getItemCode(), order.getQuantity())) {
            order.setStatus("FAILED");
            order.setMessage("Item not available in warehouse");
            return;
        }

        // 2. Розрахунок знижки
        BigDecimal finalAmount = calculateDiscount(order.getTotalAmount());
        order.setTotalAmount(finalAmount);

        // 3. Обробка платежу
        if (!paymentGateway.processPayment(order.getId(), finalAmount)) {
            order.setStatus("FAILED");
            order.setMessage("Payment failed");
            return;
        }

        // 4. Резервування товару та успішне завершення
        warehouseService.reserve(order.getItemCode(), order.getQuantity());
        order.setStatus("COMPLETED");
        order.setMessage("Order processed successfully");
        orderHistory.add(order);

        // 5. Сповіщення
        notificationService.sendEmail(order.getId(), "Your order is completed.");
        notificationService.logOrderProcessed(order.getId());
    }

    public BigDecimal calculateDiscount(BigDecimal amount) {
        // Логіка знижки (якщо сума > 1000, знижка 10%, інакше 0%)
        if (amount.compareTo(new BigDecimal("1000")) > 0) {
            return amount.multiply(new BigDecimal("0.90"));
        }
        return amount;
    }

    public List<Order> getOrderHistory() {
        return new ArrayList<>(orderHistory);
    }
}
