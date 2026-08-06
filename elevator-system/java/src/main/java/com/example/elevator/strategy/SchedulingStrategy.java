package com.example.elevator.strategy;

import com.example.elevator.model.Elevator;
import com.example.elevator.model.ExternalRequest;

import java.util.List;
import java.util.Optional;

/** Strategy pattern: swap car-selection logic without changing the controller. */
public interface SchedulingStrategy {
    Optional<Elevator> selectCar(List<Elevator> elevators, ExternalRequest request);
}
