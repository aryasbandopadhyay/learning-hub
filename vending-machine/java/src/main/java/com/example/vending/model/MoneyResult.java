package com.example.vending.model;

/** Result of inserting a coin: the updated transaction balance and a user-facing message. */
public record MoneyResult(int balance, String message) {
}
