package com.example.order;

public interface WarehouseService {
    boolean isAvailable(String itemCode, int quantity);

    void reserve(String itemCode, int quantity);
}
