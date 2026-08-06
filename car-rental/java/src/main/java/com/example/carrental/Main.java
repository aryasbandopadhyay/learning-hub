package com.example.carrental;

import com.example.carrental.model.Car;
import com.example.carrental.model.CarType;
import com.example.carrental.model.Reservation;
import com.example.carrental.service.RentalCompany;
import com.example.carrental.strategy.DailyRatePricingStrategy;

import java.time.LocalDate;
import java.util.List;

/** Runnable demo: search, reserve, pick up, return. */
public class Main {

    public static void main(String[] args) {
        RentalCompany company = new RentalCompany(List.of(
                new Car("C1", "KA-01-1111", CarType.ECONOMY, "BLR"),
                new Car("C2", "KA-02-2222", CarType.SUV, "BLR"),
                new Car("C3", "KA-03-3333", CarType.SUV, "DEL")),
                new DailyRatePricingStrategy());

        LocalDate pickup = LocalDate.of(2026, 9, 1);
        LocalDate drop = LocalDate.of(2026, 9, 4);

        System.out.println("Available SUVs in BLR: "
                + company.searchAvailable("BLR", CarType.SUV, pickup, drop).size());
        Reservation reservation = company.reserve("C2", pickup, drop);
        System.out.println("Reserved " + reservation.getCar().getId()
                + " for " + reservation.getTotalPrice());
        System.out.println("Available SUVs in BLR after reserve: "
                + company.searchAvailable("BLR", CarType.SUV, pickup, drop).size());
        company.pickUp(reservation.getId());
        System.out.println("Status after pickup: " + reservation.getStatus());
        company.returnCar(reservation.getId());
        System.out.println("Status after return: " + reservation.getStatus());
    }
}
