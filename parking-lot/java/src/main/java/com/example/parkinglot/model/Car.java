package com.example.parkinglot.model;

/** A car needs at least a MEDIUM spot (fits MEDIUM or LARGE). */
public class Car extends Vehicle {
    public Car(String licensePlate) {
        super(licensePlate, VehicleType.CAR);
    }

    @Override
    public SpotType requiredSize() {
        return SpotType.MEDIUM;
    }
}
