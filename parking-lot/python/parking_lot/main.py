"""Runnable demo: build a lot, park vehicles, unpark, print receipts.

Run:  python -m parking_lot.main   (from the python/ directory)
"""

from __future__ import annotations

from .factory import create_vehicle
from .lot import Level, ParkingLot
from .models import VehicleType
from .strategies import HourlyFeeStrategy, NearestFirstAssignmentStrategy


def main() -> None:
    levels = [Level.of(0, 2, 2, 1), Level.of(1, 2, 2, 1)]
    lot = ParkingLot(levels, NearestFirstAssignmentStrategy(), HourlyFeeStrategy())

    print("Free spots at open:", lot.available_spots())

    bike = create_vehicle(VehicleType.MOTORCYCLE, "KA-01-1234")
    car = create_vehicle(VehicleType.CAR, "KA-02-5678")
    truck = create_vehicle(VehicleType.TRUCK, "KA-03-9999")

    t1 = lot.park(bike)
    t2 = lot.park(car)
    t3 = lot.park(truck)
    print("Parked bike at", t1.spot.id)
    print("Parked car  at", t2.spot.id)
    print("Parked truck at", t3.spot.id)
    print("Free spots now:", lot.available_spots())

    receipt = lot.unpark(t2.id)
    print(f"Car left spot {receipt.ticket.spot.id}, fee = {receipt.fee}")
    print("Free spots after exit:", lot.available_spots())


if __name__ == "__main__":
    main()
