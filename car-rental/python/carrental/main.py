"""Runnable demo: search, reserve, pick up, and return.

Run:  python -m carrental.main   (from the python/ directory)
"""

from __future__ import annotations

from datetime import date

from .models import Car, CarType
from .service import RentalCompany
from .strategies import DailyRatePricingStrategy


def main() -> None:
    company = RentalCompany(
        [
            Car("C1", "KA-01-1111", CarType.ECONOMY, "BLR"),
            Car("C2", "KA-02-2222", CarType.SUV, "BLR"),
            Car("C3", "KA-03-3333", CarType.SUV, "DEL"),
        ],
        DailyRatePricingStrategy(),
    )

    pickup = date(2026, 9, 1)
    drop = date(2026, 9, 4)

    print("Available SUVs in BLR:", len(company.search_available("BLR", CarType.SUV, pickup, drop)))
    reservation = company.reserve("C2", pickup, drop)
    print(f"Reserved {reservation.car.id} for {reservation.total_price}")
    print(
        "Available SUVs in BLR after reserve:",
        len(company.search_available("BLR", CarType.SUV, pickup, drop)),
    )
    company.pick_up(reservation.id)
    print("Status after pickup:", reservation.status.name)
    company.return_car(reservation.id)
    print("Status after return:", reservation.status.name)


if __name__ == "__main__":
    main()
