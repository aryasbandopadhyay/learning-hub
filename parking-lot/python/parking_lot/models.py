"""Domain models: spot/vehicle types, the Vehicle hierarchy, ParkingSpot, and Ticket."""

from __future__ import annotations

import threading
import uuid
from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from datetime import datetime
from enum import IntEnum, Enum


class SpotType(IntEnum):
    """Spot size. IntEnum so we can compare sizes directly (SMALL < MEDIUM < LARGE)."""

    SMALL = 0
    MEDIUM = 1
    LARGE = 2


class VehicleType(Enum):
    MOTORCYCLE = "MOTORCYCLE"
    CAR = "CAR"
    TRUCK = "TRUCK"


class Vehicle(ABC):
    """Abstract base of the vehicle hierarchy (OOP: inheritance + polymorphism)."""

    def __init__(self, license_plate: str, vtype: VehicleType) -> None:
        self.license_plate = license_plate
        self.type = vtype

    @property
    @abstractmethod
    def required_size(self) -> SpotType:
        """Smallest spot size this vehicle can occupy."""

    def __repr__(self) -> str:  # pragma: no cover - trivial
        return f"{self.type.value}({self.license_plate})"


class Motorcycle(Vehicle):
    def __init__(self, license_plate: str) -> None:
        super().__init__(license_plate, VehicleType.MOTORCYCLE)

    @property
    def required_size(self) -> SpotType:
        return SpotType.SMALL


class Car(Vehicle):
    def __init__(self, license_plate: str) -> None:
        super().__init__(license_plate, VehicleType.CAR)

    @property
    def required_size(self) -> SpotType:
        return SpotType.MEDIUM


class Truck(Vehicle):
    def __init__(self, license_plate: str) -> None:
        super().__init__(license_plate, VehicleType.TRUCK)

    @property
    def required_size(self) -> SpotType:
        return SpotType.LARGE


class ParkingSpot:
    """A single spot. THE CONCURRENCY BOUNDARY.

    ``try_occupy`` and ``free`` hold ``self._lock`` so the "is it free and does the vehicle fit?"
    check and the state change are one atomic step. When many threads race for the last spot, the
    lock guarantees exactly one flips ``_occupied`` from False to True.
    """

    def __init__(self, spot_id: str, spot_type: SpotType) -> None:
        self.id = spot_id
        self.spot_type = spot_type
        self._occupied = False
        self._vehicle: Vehicle | None = None
        self._lock = threading.Lock()

    def can_fit(self, vehicle: Vehicle) -> bool:
        return self.spot_type >= vehicle.required_size

    def try_occupy(self, vehicle: Vehicle) -> bool:
        """Atomically claim this spot; return True on success."""
        with self._lock:
            if self._occupied or not self.can_fit(vehicle):
                return False
            self._occupied = True
            self._vehicle = vehicle
            return True

    def free(self) -> None:
        with self._lock:
            self._occupied = False
            self._vehicle = None

    @property
    def is_occupied(self) -> bool:
        with self._lock:
            return self._occupied


@dataclass(frozen=True)
class Ticket:
    """Issued at entry. Exit time + fee are computed by the service at unpark time."""

    vehicle: Vehicle
    spot: ParkingSpot
    entry_time: datetime
    id: str = field(default_factory=lambda: str(uuid.uuid4()))
