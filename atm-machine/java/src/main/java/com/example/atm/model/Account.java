package com.example.atm.model;

import com.example.atm.exception.InsufficientFundsException;

/**
 * Bank account balance stored as integer cents/paise, never floating point.
 *
 * <p>THIS CLASS IS THE MONEY CONCURRENCY BOUNDARY. {@link #withdraw(long)} and
 * {@link #deposit(long)} are synchronized, so check-and-decrement is atomic. If many threads race
 * to withdraw from the same account, at most the existing balance can leave the account.
 */
public class Account {

    private final String accountNumber;
    private long balanceCents;

    public Account(String accountNumber, long openingBalanceCents) {
        if (openingBalanceCents < 0) {
            throw new IllegalArgumentException("opening balance cannot be negative");
        }
        this.accountNumber = accountNumber;
        this.balanceCents = openingBalanceCents;
    }

    public synchronized void withdraw(long amountCents) {
        requirePositive(amountCents);
        if (amountCents > balanceCents) {
            throw new InsufficientFundsException("Insufficient account balance");
        }
        balanceCents -= amountCents;
    }

    public synchronized void deposit(long amountCents) {
        requirePositive(amountCents);
        balanceCents += amountCents;
    }

    public synchronized long getBalanceCents() {
        return balanceCents;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    private static void requirePositive(long amountCents) {
        if (amountCents <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
    }
}
