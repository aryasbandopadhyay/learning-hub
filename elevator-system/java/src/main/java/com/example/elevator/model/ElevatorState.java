package com.example.elevator.model;

/** Movement state. The state value is the small State-pattern object for this MVP. */
public enum ElevatorState {
    IDLE,
    MOVING_UP,
    MOVING_DOWN,
    DOORS_OPEN
}
