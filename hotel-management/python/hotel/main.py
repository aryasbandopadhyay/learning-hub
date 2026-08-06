"""Runnable demo: search -> book -> check-in -> check-out.

Run:  python -m hotel.main   (from the python/ directory)
"""

from __future__ import annotations

from datetime import date

from .models import Hotel, Room, RoomType
from .service import HotelManagementService
from .strategies import NightlyPricingStrategy


def main() -> None:
    hotel = Hotel(
        "Sea View",
        [
            Room("101", RoomType.STANDARD),
            Room("102", RoomType.STANDARD),
            Room("201", RoomType.DELUXE),
            Room("301", RoomType.SUITE),
        ],
    )
    service = HotelManagementService(hotel, NightlyPricingStrategy())

    check_in = date(2026, 1, 10)
    check_out = date(2026, 1, 12)

    print(
        "Available STANDARD rooms for 2026-01-10 to 2026-01-12:",
        _ids(service.search_available_rooms(RoomType.STANDARD, check_in, check_out)),
    )
    reservation = service.book_room("101", check_in, check_out)
    print(f"Booked room {reservation.room.id} for 2 nights, total = {reservation.total_price}")
    print(
        "Available STANDARD rooms after booking:",
        _ids(service.search_available_rooms(RoomType.STANDARD, check_in, check_out)),
    )
    service.check_in(reservation.id)
    print("Reservation status after check-in:", reservation.status.value)
    service.check_out(reservation.id)
    print("Reservation status after check-out:", reservation.status.value)


def _ids(rooms) -> str:
    return "[" + ", ".join(room.id for room in rooms) + "]"


if __name__ == "__main__":
    main()
