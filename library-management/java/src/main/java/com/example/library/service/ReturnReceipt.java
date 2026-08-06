package com.example.library.service;

import com.example.library.model.Loan;

import java.time.Instant;

/** Immutable receipt returned after a copy is checked in. */
public record ReturnReceipt(Loan loan, Instant returnTime, long fine) {
}
