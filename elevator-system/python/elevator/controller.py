"""The orchestrating service: thread-safe request intake + deterministic stepping."""

from __future__ import annotations

import threading
from collections import deque
from typing import Sequence

from .exceptions import ElevatorNotFoundError, InvalidFloorError
from .models import Direction, Elevator, ExternalRequest, InternalRequest
from .strategies import SchedulingStrategy


class ElevatorController:
    """Facade for clients.

    Requests may arrive from many threads and are appended under a lock. Movement stays single-
    threaded: tests and demos advance the whole system by explicitly calling ``step()``.
    """

    def __init__(self, floor_count: int, elevators: Sequence[Elevator], scheduling_strategy: SchedulingStrategy) -> None:
        self._min_floor = 0
        self._max_floor = floor_count - 1
        self._elevators = tuple(elevators)
        self._strategy = scheduling_strategy
        self._external_requests: deque[ExternalRequest] = deque()
        self._internal_requests: deque[InternalRequest] = deque()
        self._request_lock = threading.Lock()

    def submit_external_request(self, floor: int, direction: Direction) -> None:
        self._validate_floor(floor)
        with self._request_lock:
            self._external_requests.append(ExternalRequest(floor, direction))

    def submit_internal_request(self, car_id: int, target_floor: int) -> None:
        self._validate_floor(target_floor)
        with self._request_lock:
            self._internal_requests.append(InternalRequest(car_id, target_floor))

    def step(self) -> None:
        """Drain pending requests and move every car one deterministic tick."""
        self._drain_internal_requests()
        self._drain_external_requests()
        for elevator in self._elevators:
            elevator.step()

    def _drain_internal_requests(self) -> None:
        for request in self._pop_all_internal():
            self.find_elevator(request.car_id).add_target_floor(request.target_floor)

    def _drain_external_requests(self) -> None:
        for request in self._pop_all_external():
            selected = self._strategy.select_car(self._elevators, request) or self._nearest_fallback(request)
            selected.add_target_floor(request.floor)

    def _pop_all_external(self) -> list[ExternalRequest]:
        with self._request_lock:
            items = list(self._external_requests)
            self._external_requests.clear()
            return items

    def _pop_all_internal(self) -> list[InternalRequest]:
        with self._request_lock:
            items = list(self._internal_requests)
            self._internal_requests.clear()
            return items

    def _nearest_fallback(self, request: ExternalRequest) -> Elevator:
        if not self._elevators:
            raise ElevatorNotFoundError("No elevators configured")
        return min(self._elevators, key=lambda e: e.distance_to(request))

    def find_elevator(self, car_id: int) -> Elevator:
        for elevator in self._elevators:
            if elevator.id == car_id:
                return elevator
        raise ElevatorNotFoundError(f"Unknown elevator: {car_id}")

    def _validate_floor(self, floor: int) -> None:
        if floor < self._min_floor or floor > self._max_floor:
            raise InvalidFloorError(f"Floor out of range: {floor}")

    def pending_external_request_count(self) -> int:
        with self._request_lock:
            return len(self._external_requests)

    def pending_internal_request_count(self) -> int:
        with self._request_lock:
            return len(self._internal_requests)

    @property
    def elevators(self) -> tuple[Elevator, ...]:
        return self._elevators
