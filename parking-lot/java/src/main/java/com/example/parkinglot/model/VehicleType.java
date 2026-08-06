package com.example.parkinglot.model;

/**
 * The kinds of vehicle the lot accepts. Each maps to the smallest spot size it needs
 * (see {@link Vehicle#requiredSize()}).
 */
public enum VehicleType {
    MOTORCYCLE,
    CAR,
    TRUCK
}
