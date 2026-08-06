package com.example.parkinglot.strategy;

import com.example.parkinglot.model.ParkingSpot;
import com.example.parkinglot.model.Vehicle;
import com.example.parkinglot.service.Level;

import java.util.List;
import java.util.Optional;

/**
 * Nearest-first assignment: scan levels in order, and within a level scan spots in order, claiming
 * the first compatible free spot. "Nearest" here means lowest level / lowest index, which models a
 * lot where entry is at level 0.
 *
 * <p>Thread-safety comes for free from {@link ParkingSpot#tryOccupy}: even if two threads scan the
 * same spot simultaneously, only one {@code tryOccupy} returns true.
 */
public class NearestFirstAssignmentStrategy implements SpotAssignmentStrategy {

    @Override
    public Optional<ParkingSpot> assign(List<Level> levels, Vehicle vehicle) {
        for (Level level : levels) {
            for (ParkingSpot spot : level.getSpots()) {
                if (spot.tryOccupy(vehicle)) {
                    return Optional.of(spot);
                }
            }
        }
        return Optional.empty();
    }
}
