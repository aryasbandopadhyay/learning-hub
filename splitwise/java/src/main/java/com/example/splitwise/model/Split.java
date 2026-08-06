package com.example.splitwise.model;

/**
 * One user's computed share of an expense. Amount is in cents (long) to avoid floating-point money
 * bugs such as 0.1 + 0.2 != 0.3.
 */
public record Split(User user, long amountCents) {

    public Split {
        if (user == null) {
            throw new IllegalArgumentException("Split user is required");
        }
        if (amountCents < 0) {
            throw new IllegalArgumentException("Split amount cannot be negative");
        }
    }
}
