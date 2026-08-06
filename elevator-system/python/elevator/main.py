"""Runnable deterministic demo.

Run:  python -m elevator.main   (from the python/ directory)
"""

from __future__ import annotations

from .controller import ElevatorController
from .models import Direction, Elevator
from .strategies import NearestCarSchedulingStrategy


def main() -> None:
    car1 = Elevator(1, 0, 0, 6)
    car2 = Elevator(2, 6, 0, 6)
    controller = ElevatorController(7, [car1, car2], NearestCarSchedulingStrategy())

    print("Initial: Car 1 at floor 0 (IDLE), Car 2 at floor 6 (IDLE)")
    controller.submit_external_request(2, Direction.UP)
    controller.submit_internal_request(1, 5)
    print("Queued requests:", controller.pending_external_request_count() + controller.pending_internal_request_count())

    for tick in range(1, 8):
        controller.step()
        print(f"Tick {tick}: Car 1 at floor {car1.current_floor} ({car1.state.name})")


if __name__ == "__main__":
    main()
