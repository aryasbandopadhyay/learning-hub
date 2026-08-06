package com.example.elevator.model;

/** Car-panel request: a passenger already inside a specific car selects a target floor. */
public record InternalRequest(int carId, int targetFloor) {
}
