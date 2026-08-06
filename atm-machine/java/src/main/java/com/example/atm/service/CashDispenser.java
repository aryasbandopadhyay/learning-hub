package com.example.atm.service;

import com.example.atm.exception.CashDispenseException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Maintains ATM note inventory and computes denomination breakdowns.
 *
 * <p>Denominations are stored in cents/paise. The default demo notes are ₹2000, ₹500, ₹200, and
 * ₹100, represented as 200000, 50000, 20000, and 10000 cents. Greedy works for this canonical
 * denomination set and keeps the solution simple and explainable.
 */
public class CashDispenser {

    private final TreeMap<Integer, Integer> notesDescending = new TreeMap<>((a, b) -> b - a);

    public CashDispenser(Map<Integer, Integer> openingInventory) {
        openingInventory.forEach((denomination, count) -> {
            if (denomination <= 0 || count < 0) {
                throw new IllegalArgumentException("invalid denomination inventory");
            }
            notesDescending.put(denomination, count);
        });
    }

    public static CashDispenser demoDispenser() {
        return new CashDispenser(Map.of(200000, 5, 50000, 10, 20000, 10, 10000, 20));
    }

    /** Snapshot of remaining cash notes, useful for tests and admin display. */
    public synchronized Map<Integer, Integer> inventory() {
        return Map.copyOf(notesDescending);
    }

    /** Plan notes using greedy and decrement inventory atomically if an exact breakdown exists. */
    public synchronized Map<Integer, Integer> dispense(long amountCents) {
        Map<Integer, Integer> plan = planBreakdown(amountCents);
        plan.forEach((denomination, used) ->
                notesDescending.put(denomination, notesDescending.get(denomination) - used));
        return plan;
    }

    /** Compute a breakdown without changing inventory. */
    public synchronized Map<Integer, Integer> planBreakdown(long amountCents) {
        if (amountCents <= 0 || amountCents > Integer.MAX_VALUE) {
            throw new CashDispenseException("Withdrawal amount must be positive and supported");
        }
        int remaining = (int) amountCents;
        Map<Integer, Integer> plan = new LinkedHashMap<>();
        for (var entry : notesDescending.entrySet()) {
            int denomination = entry.getKey();
            int available = entry.getValue();
            int needed = Math.min(remaining / denomination, available);
            if (needed > 0) {
                plan.put(denomination, needed);
                remaining -= denomination * needed;
            }
        }
        if (remaining != 0) {
            throw new CashDispenseException("ATM cannot dispense exact amount with available notes");
        }
        return plan;
    }
}
