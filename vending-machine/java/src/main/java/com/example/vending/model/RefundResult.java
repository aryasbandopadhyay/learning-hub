package com.example.vending.model;

import java.util.List;

/** Result of cancel/refund: the coins returned and their total amount. */
public record RefundResult(List<Integer> coins, int amount, String message) {
    public RefundResult {
        coins = List.copyOf(coins);
    }
}
