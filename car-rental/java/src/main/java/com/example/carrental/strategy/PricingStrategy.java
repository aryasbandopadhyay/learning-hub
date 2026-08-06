package com.example.carrental.strategy;

import com.example.carrental.model.Car;

import java.time.LocalDate;

/** Strategy pattern: swap this to add weekend, seasonal, loyalty, or coupon pricing. */
public interface PricingStrategy {
    long calculatePrice(Car car, LocalDate pickupDate, LocalDate returnDate);
}
