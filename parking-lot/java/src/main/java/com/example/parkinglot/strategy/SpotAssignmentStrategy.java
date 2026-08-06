package com.example.parkinglot.strategy;

import com.example.parkinglot.model.ParkingSpot;
import com.example.parkinglot.model.Vehicle;
import com.example.parkinglot.service.Level;

import java.util.List;
import java.util.Optional;

/**
 * Strategy pattern: decides WHICH spot a vehicle gets. The implementation is responsible for
 * atomically reserving the spot it returns (by calling {@link ParkingSpot#tryOccupy}), so the
 * "find" and "claim" happen together and stay thread-safe.
 */
public interface SpotAssignmentStrategy {

    /**
     * Find and atomically reserve a compatible free spot.
     *
     * @return the reserved spot, or empty if the lot is full for this vehicle.
     */
    Optional<ParkingSpot> assign(List<Level> levels, Vehicle vehicle);
}
