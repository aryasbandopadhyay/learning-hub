package com.example.splitwise.model;

import java.util.List;
import java.util.UUID;

/**
 * Immutable expense after a SplitStrategy has converted caller input into exact per-user shares.
 * The service then uses these splits to update the balance sheet.
 */
public final class Expense {

    private final String id;
    private final User payer;
    private final long totalCents;
    private final List<Split> splits;

    public Expense(User payer, long totalCents, List<Split> splits) {
        if (payer == null) {
            throw new IllegalArgumentException("Payer is required");
        }
        if (totalCents <= 0) {
            throw new IllegalArgumentException("Total must be positive");
        }
        this.id = UUID.randomUUID().toString();
        this.payer = payer;
        this.totalCents = totalCents;
        this.splits = List.copyOf(splits);
    }

    public String getId() {
        return id;
    }

    public User getPayer() {
        return payer;
    }

    public long getTotalCents() {
        return totalCents;
    }

    public List<Split> getSplits() {
        return splits;
    }
}
