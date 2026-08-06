package com.example.parkinglot.model;

/**
 * Physical size of a parking spot. Ordering matters: a vehicle fits in a spot whose size is
 * >= the vehicle's required size (see {@link Vehicle#requiredSize()} and
 * {@link ParkingSpot#canFit}). Declaring them smallest-to-largest lets us compare by ordinal.
 */
public enum SpotType {
    SMALL,
    MEDIUM,
    LARGE
}
