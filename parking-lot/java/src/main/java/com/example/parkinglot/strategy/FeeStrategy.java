package com.example.parkinglot.strategy;

import com.example.parkinglot.model.Ticket;

import java.time.Instant;

/**
 * Strategy pattern: how to price a parking session. Swapping this out (flat rate, day/night rates,
 * free-first-30-min, etc.) requires no change to the {@code ParkingLot} service.
 */
public interface FeeStrategy {

    /**
     * @param ticket   the ticket issued at entry (holds entry time + spot type)
     * @param exitTime when the vehicle leaves
     * @return the fee to charge, in whole currency units (cents avoided for MVP simplicity)
     */
    long calculateFee(Ticket ticket, Instant exitTime);
}
