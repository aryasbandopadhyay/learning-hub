package com.example.elevator;

import com.example.elevator.model.Direction;
import com.example.elevator.model.Elevator;
import com.example.elevator.model.ElevatorState;
import com.example.elevator.model.ExternalRequest;
import com.example.elevator.service.ElevatorController;
import com.example.elevator.strategy.NearestCarSchedulingStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElevatorControllerTest {

    private ElevatorController oneCarController(Elevator car) {
        return new ElevatorController(10, List.of(car), new NearestCarSchedulingStrategy());
    }

    @Test
    void singleCarServesInternalRequestAndOpensDoors() {
        Elevator car = new Elevator(1, 0, 0, 9);
        ElevatorController controller = oneCarController(car);

        controller.submitInternalRequest(1, 3);
        controller.step();
        assertEquals(1, car.getCurrentFloor());
        assertEquals(ElevatorState.MOVING_UP, car.getState());

        controller.step();
        controller.step();
        assertEquals(3, car.getCurrentFloor());
        assertEquals(ElevatorState.DOORS_OPEN, car.getState());

        controller.step();
        assertEquals(ElevatorState.IDLE, car.getState());
    }

    @Test
    void movingUpServesHigherFloorsBeforeReversing() {
        Elevator car = new Elevator(1, 0, 0, 9);
        ElevatorController controller = oneCarController(car);

        controller.submitInternalRequest(1, 5);
        controller.step(); // car is now moving up from 0 to 1
        controller.submitInternalRequest(1, 2); // lower than target but still above current floor
        controller.submitInternalRequest(1, 0); // behind the car; should wait for reversal

        controller.step();
        assertEquals(2, car.getCurrentFloor());
        assertEquals(ElevatorState.DOORS_OPEN, car.getState());
        controller.step();
        assertEquals(ElevatorState.MOVING_UP, car.getState(), "must not reverse while floor 5 remains above");

        controller.step();
        assertEquals(3, car.getCurrentFloor());
        assertEquals(ElevatorState.MOVING_UP, car.getState());
    }

    @Test
    void nearestCarStrategyPicksCloserIdleCar() {
        Elevator near = new Elevator(1, 1, 0, 9);
        Elevator far = new Elevator(2, 8, 0, 9);
        NearestCarSchedulingStrategy strategy = new NearestCarSchedulingStrategy();

        Elevator selected = strategy.selectCar(List.of(near, far), new ExternalRequest(3, Direction.UP)).orElseThrow();
        assertEquals(1, selected.getId());
    }

    /** Many producer threads submit hall calls; the thread-safe queue must not lose any. */
    @Test
    void concurrentExternalRequestsAreAllQueued() throws InterruptedException {
        int threads = 50;
        ElevatorController controller = new ElevatorController(
                100,
                List.of(new Elevator(1, 0, 0, 99)),
                new NearestCarSchedulingStrategy());
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);

        for (int i = 0; i < threads; i++) {
            final int floor = i + 1;
            pool.submit(() -> {
                try {
                    start.await();
                    controller.submitExternalRequest(floor, Direction.UP);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(threads, controller.pendingExternalRequestCount(), "no submitted request is lost");
    }
}
