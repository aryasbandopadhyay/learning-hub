package com.example.elevator.model;

import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * One elevator car with deterministic, step-driven movement.
 *
 * <p>The car uses a SCAN-like policy: while moving up it prefers higher targets; while moving down
 * it prefers lower targets. Only when there is nothing left in the current direction does it
 * reverse. This mirrors real elevator behavior without needing real threads or timers.
 *
 * <p>Methods are synchronized because requests may be attached while another thread is submitting
 * work to the controller. The simulation loop itself remains single-threaded and deterministic.
 */
public class Elevator {

    private final int id;
    private final int minFloor;
    private final int maxFloor;
    private final NavigableSet<Integer> targetFloors = new TreeSet<>();

    private int currentFloor;
    private ElevatorState state = ElevatorState.IDLE;
    private ElevatorState stateBeforeDoors = ElevatorState.IDLE;

    public Elevator(int id, int startFloor, int minFloor, int maxFloor) {
        this.id = id;
        this.currentFloor = startFloor;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
    }

    /** Add or merge a floor target. A set naturally deduplicates repeated button presses. */
    public synchronized void addTargetFloor(int floor) {
        if (floor < minFloor || floor > maxFloor) {
            throw new IllegalArgumentException("Floor out of range: " + floor);
        }
        targetFloors.add(floor);
        if (state == ElevatorState.IDLE) {
            chooseNextState();
        }
    }

    /** Advance exactly one deterministic tick: move one floor, or open/close doors. */
    public synchronized void step() {
        if (state == ElevatorState.DOORS_OPEN) {
            targetFloors.remove(currentFloor);
            chooseNextState();
            return;
        }
        if (targetFloors.isEmpty()) {
            state = ElevatorState.IDLE;
            return;
        }
        if (targetFloors.contains(currentFloor)) {
            openDoors();
            return;
        }
        if (state == ElevatorState.IDLE) {
            chooseNextState();
        }
        if (state == ElevatorState.MOVING_UP) {
            currentFloor++;
        } else if (state == ElevatorState.MOVING_DOWN) {
            currentFloor--;
        }
        if (targetFloors.contains(currentFloor)) {
            openDoors();
        }
    }

    private void openDoors() {
        stateBeforeDoors = state == ElevatorState.IDLE ? chooseDirectionForCurrentFloor() : state;
        state = ElevatorState.DOORS_OPEN;
    }

    private ElevatorState chooseDirectionForCurrentFloor() {
        if (targetFloors.higher(currentFloor) != null) {
            return ElevatorState.MOVING_UP;
        }
        if (targetFloors.lower(currentFloor) != null) {
            return ElevatorState.MOVING_DOWN;
        }
        return ElevatorState.IDLE;
    }

    private void chooseNextState() {
        if (targetFloors.isEmpty()) {
            state = ElevatorState.IDLE;
            return;
        }
        if (stateBeforeDoors == ElevatorState.MOVING_UP && targetFloors.higher(currentFloor) != null) {
            state = ElevatorState.MOVING_UP;
        } else if (stateBeforeDoors == ElevatorState.MOVING_DOWN && targetFloors.lower(currentFloor) != null) {
            state = ElevatorState.MOVING_DOWN;
        } else if (targetFloors.higher(currentFloor) != null) {
            state = ElevatorState.MOVING_UP;
        } else if (targetFloors.lower(currentFloor) != null) {
            state = ElevatorState.MOVING_DOWN;
        } else if (targetFloors.contains(currentFloor)) {
            openDoors();
        } else {
            state = ElevatorState.IDLE;
        }
    }

    public synchronized boolean canServeOnCurrentPath(ExternalRequest request) {
        return (state == ElevatorState.IDLE)
                || (state == ElevatorState.MOVING_UP
                    && request.direction() == Direction.UP
                    && request.floor() >= currentFloor)
                || (state == ElevatorState.MOVING_DOWN
                    && request.direction() == Direction.DOWN
                    && request.floor() <= currentFloor);
    }

    public synchronized int distanceTo(ExternalRequest request) {
        return Math.abs(currentFloor - request.floor());
    }

    public int getId() {
        return id;
    }

    public synchronized int getCurrentFloor() {
        return currentFloor;
    }

    public synchronized ElevatorState getState() {
        return state;
    }

    public synchronized NavigableSet<Integer> getTargetFloors() {
        return new TreeSet<>(targetFloors);
    }
}
