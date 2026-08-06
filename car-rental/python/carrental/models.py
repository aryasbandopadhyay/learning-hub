"""Domain models: car type, car, reservation status, and reservation."""

from __future__ import annotations

import threading
import uuid
from dataclasses import dataclass, field
from datetime import date
from enum import Enum


class CarType(Enum):
    """Car category plus base daily rate."""

    ECONOMY = 40
    SUV = 70
    LUXURY = 120

    @property
    def daily_rate(self) -> int:
        return self.value


class ReservationStatus(Enum):
    """Reservation state machine: CONFIRMED -> PICKED_UP -> RETURNED, or -> CANCELLED."""

    CONFIRMED = "CONFIRMED"
    PICKED_UP = "PICKED_UP"
    RETURNED = "RETURNED"
    CANCELLED = "CANCELLED"


class Car:
    """A rentable car. Its lock guards its reservation list.

    This per-car lock is the concurrency boundary: reserve() checks for overlapping reservations
    and appends the new one while holding this lock, so two threads cannot both book the same car.
    """

    def __init__(self, car_id: str, license_plate: str, car_type: CarType, location: str) -> None:
        self.id = car_id
        self.license_plate = license_plate
        self.type = car_type
        self.location = location
        self.lock = threading.Lock()
        self.reservations: list[Reservation] = []

    def __repr__(self) -> str:  # pragma: no cover - trivial
        return f"{self.type.name}({self.id}, {self.location})"


@dataclass
class Reservation:
    """Booking record for one car and one half-open date range: [pickup_date, return_date)."""

    car: Car
    pickup_date: date
    return_date: date
    total_price: int
    status: ReservationStatus = ReservationStatus.CONFIRMED
    id: str = field(default_factory=lambda: str(uuid.uuid4()))

    def overlaps(self, other_start: date, other_end: date) -> bool:
        # Half-open interval overlap: start1 < end2 AND start2 < end1. Return day is free.
        return self.pickup_date < other_end and other_start < self.return_date

    def blocks_availability(self) -> bool:
        return self.status in {ReservationStatus.CONFIRMED, ReservationStatus.PICKED_UP}
