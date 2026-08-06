package com.example.library.strategy;

import com.example.library.model.Loan;

import java.time.Instant;

/** Strategy abstraction: swap overdue pricing without changing LibraryService. */
public interface FineStrategy {
    long calculateFine(Loan loan, Instant returnTime);
}
