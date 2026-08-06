package com.example.vending.model;

/**
 * Immutable catalog entry. Stock intentionally lives outside Product in InventoryItem so product
 * identity (code/name/price) is stable while inventory changes transaction by transaction.
 */
public class Product {

    private final String code;
    private final String name;
    private final int price;

    public Product(String code, String name, int price) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Product code is required");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return code + "(" + name + ", " + price + ")";
    }
}
