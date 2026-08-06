package com.example.vending.model;

/**
 * Mutable stock holder for one product.
 *
 * <p>The VendingMachine service owns the transaction lock, so stock is normally mutated while that
 * lock is held. Keeping the decrement in one method still makes the invariant explicit: stock can
 * never go below zero.
 */
public class InventoryItem {

    private final Product product;
    private int stock;

    public InventoryItem(Product product, int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        this.product = product;
        this.stock = stock;
    }

    public Product getProduct() {
        return product;
    }

    public int getStock() {
        return stock;
    }

    public boolean isAvailable() {
        return stock > 0;
    }

    public void decrement() {
        if (stock <= 0) {
            throw new IllegalStateException("Cannot decrement sold-out stock");
        }
        stock--;
    }
}
