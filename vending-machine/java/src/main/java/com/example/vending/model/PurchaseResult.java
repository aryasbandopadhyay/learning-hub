package com.example.vending.model;

import java.util.List;

/** Result of a successful vend: dispensed product, greedy change coins, and final message. */
public record PurchaseResult(Product product, List<Integer> change, String message) {
    public PurchaseResult {
        change = List.copyOf(change);
    }
}
