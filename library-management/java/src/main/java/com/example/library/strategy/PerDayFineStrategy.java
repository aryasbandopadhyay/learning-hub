package com.example.library.strategy;

import com.example.library.model.Loan;

import java.time.Duration;
import java.time.Instant;

/**
 * Simple fine policy for the MVP: every started overdue day costs a fixed rate.
 * Not late => zero fine. Three days late at rate 5 => 15.
 */
public class PerDayFineStrategy implements FineStrategy {

    private final long ratePerDay;

    public PerDayFineStrategy(long ratePerDay) {
        this.ratePerDay = ratePerDay;
    }

    @Override
    public long calculateFine(Loan loan, Instant returnTime) {
        Duration overdue = Duration.between(loan.getDueTime(), returnTime);
        if (overdue.isNegative() || overdue.isZero()) {
            return 0L;
        }
        long days = (long) Math.ceil(overdue.toHours() / 24.0);
        return days * ratePerDay;
    }
}
