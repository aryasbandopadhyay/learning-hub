package com.example.hotel.strategy;

import com.example.hotel.model.Room;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** MVP pricing: number of nights in [checkIn, checkOut) multiplied by the room type's rate. */
public class NightlyPricingStrategy implements PricingStrategy {

    @Override
    public long calculatePrice(Room room, LocalDate checkIn, LocalDate checkOut) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        return nights * room.getRoomType().getNightlyRate();
    }
}
