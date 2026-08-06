"""Factory pattern: centralized creation of Vehicle subclasses."""

from __future__ import annotations

from .models import Car, Motorcycle, Truck, Vehicle, VehicleType

_CONSTRUCTORS = {
    VehicleType.MOTORCYCLE: Motorcycle,
    VehicleType.CAR: Car,
    VehicleType.TRUCK: Truck,
}


def create_vehicle(vtype: VehicleType, license_plate: str) -> Vehicle:
    """Build the concrete Vehicle for ``vtype``. Callers depend on the enum, not constructors."""
    try:
        return _CONSTRUCTORS[vtype](license_plate)
    except KeyError:  # pragma: no cover - defensive
        raise ValueError(f"Unsupported vehicle type: {vtype}")
