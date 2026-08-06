package com.example.parkinglot.model;

/** A truck only fits in a LARGE spot. */
public class Truck extends Vehicle {
    public Truck(String licensePlate) {
        super(licensePlate, VehicleType.TRUCK);
    }

    @Override
    public SpotType requiredSize() {
        return SpotType.LARGE;
    }
}
