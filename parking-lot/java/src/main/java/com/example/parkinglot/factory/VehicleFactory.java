package com.example.parkinglot.factory;

import com.example.parkinglot.model.Car;
import com.example.parkinglot.model.Motorcycle;
import com.example.parkinglot.model.Truck;
import com.example.parkinglot.model.Vehicle;
import com.example.parkinglot.model.VehicleType;

/**
 * Factory pattern: centralizes creation of {@link Vehicle} subclasses so callers depend on the
 * abstract type and the enum, not on concrete constructors. Adding a vehicle type touches only
 * this switch.
 */
public final class VehicleFactory {

    private VehicleFactory() {
    }

    public static Vehicle create(VehicleType type, String licensePlate) {
        return switch (type) {
            case MOTORCYCLE -> new Motorcycle(licensePlate);
            case CAR -> new Car(licensePlate);
            case TRUCK -> new Truck(licensePlate);
        };
    }
}
