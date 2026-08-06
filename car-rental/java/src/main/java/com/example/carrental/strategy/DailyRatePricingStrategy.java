package com.example.carrental.strategy;

import com.example.carrental.model.Car;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Default MVP pricing: number of rental days times the car type's daily rate. */
public class DailyRatePricingStrategy implements PricingStrategy {

    @Override
    public long calculatePrice(Car car, LocalDate pickupDate, LocalDate returnDate) {
        long days = ChronoUnit.DAYS.between(pickupDate, returnDate);
        return days * car.getType().getDailyRate();
    }
}
