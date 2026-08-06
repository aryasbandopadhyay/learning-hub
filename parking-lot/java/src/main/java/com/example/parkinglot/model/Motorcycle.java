package com.example.parkinglot.model;

/** A motorcycle fits in the smallest spot (and, by the fit rule, any larger spot too). */
public class Motorcycle extends Vehicle {
    public Motorcycle(String licensePlate) {
        super(licensePlate, VehicleType.MOTORCYCLE);
    }

    @Override
    public SpotType requiredSize() {
        return SpotType.SMALL;
    }
}
