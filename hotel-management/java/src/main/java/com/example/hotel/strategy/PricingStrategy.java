package com.example.hotel.strategy;

import com.example.hotel.model.Room;

import java.time.LocalDate;

/** Strategy seam: swap this for weekend, seasonal, loyalty, or demand-based pricing later. */
public interface PricingStrategy {
    long calculatePrice(Room room, LocalDate checkIn, LocalDate checkOut);
}
