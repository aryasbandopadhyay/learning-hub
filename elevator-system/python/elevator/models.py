"""Domain models: directions, requests, state, and the Elevator car."""

from __future__ import annotations

import threading
from dataclasses import dataclass
from enum import Enum, auto


class Direction(Enum):
    """Direction requested at a hall button or chosen by a moving elevator."""

    UP = auto()
    DOWN = auto()


class ElevatorState(Enum):
    """Movement state. For this MVP, the enum is the compact State-pattern representation."""

    IDLE = auto()
    MOVING_UP = auto()
    MOVING_DOWN = auto()
    DOORS_OPEN = auto()


@dataclass(frozen=True)
class ExternalRequest:
    """Hall-call request: a passenger waiting on a floor and desired direction."""

    floor: int
    direction: Direction


@dataclass(frozen=True)
class InternalRequest:
    """Car-panel request: a passenger inside one car selects a target floor."""

    car_id: int
    target_floor: int


class Elevator:
    """One deterministic, step-driven elevator car.

    The car uses a SCAN-like policy: continue in the current direction while targets remain there,
    then reverse. Public methods hold a lock because request intake can be multi-threaded, but the
    simulation loop itself is intentionally single-threaded and sleep-free.
    """

    def __init__(self, car_id: int, start_floor: int, min_floor: int, max_floor: int) -> None:
        self.id = car_id
        self.current_floor = start_floor
        self._min_floor = min_floor
        self._max_floor = max_floor
        self.state = ElevatorState.IDLE
        self._state_before_doors = ElevatorState.IDLE
        self._target_floors: set[int] = set()
        self._lock = threading.Lock()

    def add_target_floor(self, floor: int) -> None:
        """Add or merge a floor target. A set deduplicates repeated button presses."""
        if floor < self._min_floor or floor > self._max_floor:
            raise ValueError(f"Floor out of range: {floor}")
        with self._lock:
            self._target_floors.add(floor)
            if self.state is ElevatorState.IDLE:
                self._choose_next_state()

    def step(self) -> None:
        """Advance exactly one tick: move one floor, or open/close doors."""
        with self._lock:
            if self.state is ElevatorState.DOORS_OPEN:
                self._target_floors.discard(self.current_floor)
                self._choose_next_state()
                return
            if not self._target_floors:
                self.state = ElevatorState.IDLE
                return
            if self.current_floor in self._target_floors:
                self._open_doors()
                return
            if self.state is ElevatorState.IDLE:
                self._choose_next_state()
            if self.state is ElevatorState.MOVING_UP:
                self.current_floor += 1
            elif self.state is ElevatorState.MOVING_DOWN:
                self.current_floor -= 1
            if self.current_floor in self._target_floors:
                self._open_doors()

    def _open_doors(self) -> None:
        self._state_before_doors = self.state if self.state is not ElevatorState.IDLE else self._direction_for_current_floor()
        self.state = ElevatorState.DOORS_OPEN

    def _direction_for_current_floor(self) -> ElevatorState:
        if any(f > self.current_floor for f in self._target_floors):
            return ElevatorState.MOVING_UP
        if any(f < self.current_floor for f in self._target_floors):
            return ElevatorState.MOVING_DOWN
        return ElevatorState.IDLE

    def _choose_next_state(self) -> None:
        if not self._target_floors:
            self.state = ElevatorState.IDLE
        elif self._state_before_doors is ElevatorState.MOVING_UP and any(f > self.current_floor for f in self._target_floors):
            self.state = ElevatorState.MOVING_UP
        elif self._state_before_doors is ElevatorState.MOVING_DOWN and any(f < self.current_floor for f in self._target_floors):
            self.state = ElevatorState.MOVING_DOWN
        elif any(f > self.current_floor for f in self._target_floors):
            self.state = ElevatorState.MOVING_UP
        elif any(f < self.current_floor for f in self._target_floors):
            self.state = ElevatorState.MOVING_DOWN
        elif self.current_floor in self._target_floors:
            self._open_doors()
        else:
            self.state = ElevatorState.IDLE

    def can_serve_on_current_path(self, request: ExternalRequest) -> bool:
        with self._lock:
            return (
                self.state is ElevatorState.IDLE
                or (self.state is ElevatorState.MOVING_UP and request.direction is Direction.UP and request.floor >= self.current_floor)
                or (self.state is ElevatorState.MOVING_DOWN and request.direction is Direction.DOWN and request.floor <= self.current_floor)
            )

    def distance_to(self, request: ExternalRequest) -> int:
        with self._lock:
            return abs(self.current_floor - request.floor)

    @property
    def target_floors(self) -> tuple[int, ...]:
        with self._lock:
            return tuple(sorted(self._target_floors))
