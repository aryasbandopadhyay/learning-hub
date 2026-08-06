package com.example.elevator;

import com.example.elevator.model.Direction;
import com.example.elevator.model.Elevator;
import com.example.elevator.service.ElevatorController;
import com.example.elevator.strategy.NearestCarSchedulingStrategy;

import java.util.List;

/** Runnable deterministic demo. No sleeps: each printed tick is one explicit controller.step(). */
public class Main {

    public static void main(String[] args) {
        Elevator car1 = new Elevator(1, 0, 0, 6);
        Elevator car2 = new Elevator(2, 6, 0, 6);
        ElevatorController controller = new ElevatorController(
                7,
                List.of(car1, car2),
                new NearestCarSchedulingStrategy());

        System.out.println("Initial: Car 1 at floor 0 (IDLE), Car 2 at floor 6 (IDLE)");
        controller.submitExternalRequest(2, Direction.UP);
        controller.submitInternalRequest(1, 5);
        System.out.println("Queued requests: "
                + (controller.pendingExternalRequestCount() + controller.pendingInternalRequestCount()));

        for (int tick = 1; tick <= 7; tick++) {
            controller.step();
            System.out.println("Tick " + tick + ": Car 1 at floor "
                    + car1.getCurrentFloor() + " (" + car1.getState() + ")");
        }
    }
}
