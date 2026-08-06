package com.example.elevator.service;

import com.example.elevator.exception.ElevatorNotFoundException;
import com.example.elevator.exception.InvalidFloorException;
import com.example.elevator.model.Direction;
import com.example.elevator.model.Elevator;
import com.example.elevator.model.ExternalRequest;
import com.example.elevator.model.InternalRequest;
import com.example.elevator.strategy.SchedulingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Facade for clients. Request intake is thread-safe; movement is deterministic.
 *
 * <p>External and internal requests can arrive from many threads and are appended to blocking
 * queues. A test can assert queue sizes before the single-threaded simulation drains them. The
 * core movement never sleeps and never owns background worker threads, so every test can advance
 * the world by calling {@link #step()} exactly as many times as needed.
 */
public class ElevatorController {

    private final int minFloor = 0;
    private final int maxFloor;
    private final List<Elevator> elevators;
    private final SchedulingStrategy schedulingStrategy;
    private final BlockingQueue<ExternalRequest> externalRequests = new LinkedBlockingQueue<>();
    private final BlockingQueue<InternalRequest> internalRequests = new LinkedBlockingQueue<>();

    public ElevatorController(int floorCount,
                              List<Elevator> elevators,
                              SchedulingStrategy schedulingStrategy) {
        this.maxFloor = floorCount - 1;
        this.elevators = List.copyOf(elevators);
        this.schedulingStrategy = schedulingStrategy;
    }

    public void submitExternalRequest(int floor, Direction direction) {
        validateFloor(floor);
        externalRequests.add(new ExternalRequest(floor, direction));
    }

    public void submitInternalRequest(int carId, int targetFloor) {
        validateFloor(targetFloor);
        internalRequests.add(new InternalRequest(carId, targetFloor));
    }

    /** Drain pending requests and move every car one tick. */
    public void step() {
        drainInternalRequests();
        drainExternalRequests();
        elevators.forEach(Elevator::step);
    }

    private void drainInternalRequests() {
        InternalRequest request;
        while ((request = internalRequests.poll()) != null) {
            findElevator(request.carId()).addTargetFloor(request.targetFloor());
        }
    }

    private void drainExternalRequests() {
        ExternalRequest request;
        while ((request = externalRequests.poll()) != null) {
            ExternalRequest current = request;
            Optional<Elevator> selected = schedulingStrategy.selectCar(elevators, current);
            selected.orElseGet(() -> nearestFallback(current)).addTargetFloor(current.floor());
        }
    }

    private Elevator nearestFallback(ExternalRequest request) {
        return elevators.stream()
                .min((a, b) -> Integer.compare(a.distanceTo(request), b.distanceTo(request)))
                .orElseThrow(() -> new ElevatorNotFoundException("No elevators configured"));
    }

    public Elevator findElevator(int carId) {
        return elevators.stream()
                .filter(e -> e.getId() == carId)
                .findFirst()
                .orElseThrow(() -> new ElevatorNotFoundException("Unknown elevator: " + carId));
    }

    private void validateFloor(int floor) {
        if (floor < minFloor || floor > maxFloor) {
            throw new InvalidFloorException("Floor out of range: " + floor);
        }
    }

    public int pendingExternalRequestCount() {
        return externalRequests.size();
    }

    public int pendingInternalRequestCount() {
        return internalRequests.size();
    }

    public List<Elevator> getElevators() {
        return new ArrayList<>(elevators);
    }
}
