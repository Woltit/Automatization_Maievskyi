package com.example.order;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private WarehouseService warehouseService;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    // Базове налаштування Mockito та 3 бізнес-сценарії

    // Успішна обробка замовлення
    @Test
    void shouldProcessOrderSuccessfully() {
        // Arrange
        Order order = new Order("123", "ITEM1", 2, new BigDecimal("500"));
        when(warehouseService.isAvailable("ITEM1", 2)).thenReturn(true);
        when(paymentGateway.processPayment(eq("123"), any(BigDecimal.class))).thenReturn(true);

        // Act
        orderService.processOrder(order);

        // Використання AssertJ SoftAssertions
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(order.getStatus()).isEqualTo("COMPLETED");
        softly.assertThat(order.getMessage()).isEqualTo("Order processed successfully");
        softly.assertThat(order.getTotalAmount()).isEqualByComparingTo("500");
        softly.assertAll();

        // Перевірка void-методів (verify)
        verify(warehouseService, times(1)).reserve("ITEM1", 2);
        verify(notificationService, times(1)).sendEmail("123", "Your order is completed.");
        verify(notificationService, times(1)).logOrderProcessed("123");
    }

    // Відмова, бо товару немає на складі
    @Test
    void shouldFailWhenItemNotAvailable() {
        // Arrange
        Order order = new Order("124", "ITEM2", 5, new BigDecimal("1000"));
        when(warehouseService.isAvailable("ITEM2", 5)).thenReturn(false);

        // Act
        orderService.processOrder(order);

        // Assert
        assertThat(order.getStatus()).isEqualTo("FAILED");
        assertThat(order.getMessage()).isEqualTo("Item not available in warehouse");

        // Перевірка void-методів (never)
        verify(paymentGateway, never()).processPayment(anyString(), any(BigDecimal.class));
        verify(warehouseService, never()).reserve(anyString(), anyInt());
        verify(notificationService, never()).sendEmail(anyString(), anyString());
    }

    // Відмова через помилку оплати
    @Test
    void shouldFailWhenPaymentFails() {
        // Arrange
        Order order = new Order("125", "ITEM3", 1, new BigDecimal("800"));
        when(warehouseService.isAvailable("ITEM3", 1)).thenReturn(true);
        when(paymentGateway.processPayment(eq("125"), any(BigDecimal.class))).thenReturn(false);

        // Act
        orderService.processOrder(order);

        // Assert
        assertThat(order.getStatus()).isEqualTo("FAILED");
        assertThat(order.getMessage()).isEqualTo("Payment failed");

        // Перевірка void-методів (never)
        verify(warehouseService, never()).reserve(anyString(), anyInt());
        verify(notificationService, never()).sendEmail(anyString(), anyString());
    }

    // AssertJ перевірки списків
    @Test
    void shouldReturnOrderHistory() {
        // Arrange
        Order order1 = new Order("101", "ITEM1", 1, new BigDecimal("100"));
        Order order2 = new Order("102", "ITEM2", 2, new BigDecimal("200"));

        when(warehouseService.isAvailable(anyString(), anyInt())).thenReturn(true);
        when(paymentGateway.processPayment(anyString(), any(BigDecimal.class))).thenReturn(true);

        orderService.processOrder(order1);
        orderService.processOrder(order2);

        // Act
        List<Order> history = orderService.getOrderHistory();

        // Assert (3 різні типи перевірок)
        assertThat(history)
                .hasSize(2)
                .extracting("id")
                .containsExactlyInAnyOrder("101", "102");

        assertThat(history).isNotEmpty();
    }

    // Приклад для PIT Mutation Testing (Мутаційне тестування)

    @Test
    void calculateDiscount_weakTest() {
        assertThat(orderService.calculateDiscount(new BigDecimal("500"))).isEqualByComparingTo("500");
        assertThat(orderService.calculateDiscount(new BigDecimal("2000"))).isEqualByComparingTo("1800");
    }

    @Test
    void calculateDiscount_strongTest() {
        assertThat(orderService.calculateDiscount(new BigDecimal("999"))).isEqualByComparingTo("999");
        assertThat(orderService.calculateDiscount(new BigDecimal("1000"))).isEqualByComparingTo("1000");
        assertThat(orderService.calculateDiscount(new BigDecimal("1001"))).isEqualByComparingTo("900.9");
    }
}
