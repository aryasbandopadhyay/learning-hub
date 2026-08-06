"""End-to-end tests for the Elevator MVP, including thread-safe request intake."""

from __future__ import annotations

import threading

from elevator.controller import ElevatorController
from elevator.models import Direction, Elevator, ElevatorState, ExternalRequest
from elevator.strategies import NearestCarSchedulingStrategy


def one_car_controller(car: Elevator) -> ElevatorController:
    return ElevatorController(10, [car], NearestCarSchedulingStrategy())


def test_single_car_serves_internal_request_and_opens_doors() -> None:
    car = Elevator(1, 0, 0, 9)
    controller = one_car_controller(car)

    controller.submit_internal_request(1, 3)
    controller.step()
    assert car.current_floor == 1
    assert car.state is ElevatorState.MOVING_UP

    controller.step()
    controller.step()
    assert car.current_floor == 3
    assert car.state is ElevatorState.DOORS_OPEN

    controller.step()
    assert car.state is ElevatorState.IDLE


def test_moving_up_serves_higher_floors_before_reversing() -> None:
    car = Elevator(1, 0, 0, 9)
    controller = one_car_controller(car)

    controller.submit_internal_request(1, 5)
    controller.step()  # car moves from 0 to 1
    controller.submit_internal_request(1, 2)
    controller.submit_internal_request(1, 0)

    controller.step()
    assert car.current_floor == 2
    assert car.state is ElevatorState.DOORS_OPEN
    controller.step()
    assert car.state is ElevatorState.MOVING_UP  # must not reverse while floor 5 remains above

    controller.step()
    assert car.current_floor == 3
    assert car.state is ElevatorState.MOVING_UP


def test_nearest_car_strategy_picks_closer_idle_car() -> None:
    near = Elevator(1, 1, 0, 9)
    far = Elevator(2, 8, 0, 9)
    strategy = NearestCarSchedulingStrategy()

    selected = strategy.select_car([near, far], ExternalRequest(3, Direction.UP))
    assert selected is near


def test_concurrent_external_requests_are_all_queued() -> None:
    threads = 50
    controller = ElevatorController(100, [Elevator(1, 0, 0, 99)], NearestCarSchedulingStrategy())
    start = threading.Event()

    def worker(floor: int) -> None:
        start.wait()
        controller.submit_external_request(floor, Direction.UP)

    workers = [threading.Thread(target=worker, args=(i + 1,)) for i in range(threads)]
    for worker_thread in workers:
        worker_thread.start()
    start.set()
    for worker_thread in workers:
        worker_thread.join(timeout=10)

    assert controller.pending_external_request_count() == threads
