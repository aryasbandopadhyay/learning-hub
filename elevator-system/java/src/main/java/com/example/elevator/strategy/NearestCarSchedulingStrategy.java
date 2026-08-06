package com.example.elevator.strategy;

import com.example.elevator.model.Elevator;
import com.example.elevator.model.ExternalRequest;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Choose the nearest suitable car. A car is suitable if it is idle, or already moving toward the
 * caller in the requested direction. This keeps the MVP understandable while still modelling a
 * real elevator scheduler.
 */
public class NearestCarSchedulingStrategy implements SchedulingStrategy {

    @Override
    public Optional<Elevator> selectCar(List<Elevator> elevators, ExternalRequest request) {
        return elevators.stream()
                .filter(e -> e.canServeOnCurrentPath(request))
                .min(Comparator.comparingInt(e -> e.distanceTo(request)));
    }
}
