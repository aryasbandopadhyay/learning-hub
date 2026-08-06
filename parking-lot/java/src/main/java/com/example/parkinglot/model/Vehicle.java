package com.example.parkinglot.model;

/**
 * Abstract base of the vehicle hierarchy (OOP: inheritance + polymorphism).
 *
 * <p>Each concrete subclass declares the minimum {@link SpotType} it can occupy. Using an abstract
 * method rather than a big switch keeps the Open/Closed Principle: adding a new vehicle type means
 * adding a subclass, not editing existing code.
 */
public abstract class Vehicle {

    private final String licensePlate;
    private final VehicleType type;

    protected Vehicle(String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }

    /** The smallest spot size this vehicle can park in. */
    public abstract SpotType requiredSize();

    public String getLicensePlate() {
        return licensePlate;
    }

    public VehicleType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type + "(" + licensePlate + ")";
    }
}
