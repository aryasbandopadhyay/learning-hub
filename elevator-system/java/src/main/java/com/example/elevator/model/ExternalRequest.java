package com.example.elevator.model;

/** Hall-call request: a passenger waiting on a floor and saying which way they want to go. */
public record ExternalRequest(int floor, Direction direction) {
}
