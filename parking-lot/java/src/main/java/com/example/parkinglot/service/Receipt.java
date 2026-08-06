package com.example.parkinglot.service;

import com.example.parkinglot.model.Ticket;

import java.time.Instant;

/** Result of unparking: which ticket, when it left, and what it owes. */
public record Receipt(Ticket ticket, Instant exitTime, long fee) {
}
