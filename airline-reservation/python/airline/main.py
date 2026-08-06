"""Runnable demo: search, book, reject duplicate booking, cancel, then book again.

Run:  python -m airline.main   (from the python/ directory)
"""

from __future__ import annotations

from datetime import datetime

from .exceptions import SeatAlreadyBookedError
from .models import Cabin, Flight, FlightInventory, Passenger, Seat
from .service import AirlineReservationService


def sample_flight() -> Flight:
    return Flight(
        "AI101",
        "BLR",
        "DEL",
        datetime(2026, 8, 5, 9, 30),
        FlightInventory([
            Seat("1A", Cabin.BUSINESS),
            Seat("1B", Cabin.BUSINESS),
            Seat("12A", Cabin.ECONOMY),
        ]),
    )


def main() -> None:
    service = AirlineReservationService()
    service.add_flight(sample_flight())

    print("Search BLR -> DEL:")
    for flight in service.search_flights("BLR", "DEL", only_with_seats=True):
        print(f"{flight.flight_number} {flight.origin}->{flight.destination} seats={flight.inventory.available_count()}")

    booking = service.book_seat("AI101", "1A", Passenger("Alice", "alice@example.com"))
    print(f"Booked {booking.pnr} for {booking.passenger.name} on {booking.seat_no} price={booking.price}")

    try:
        service.book_seat("AI101", "1A", Passenger("Bob", "bob@example.com"))
    except SeatAlreadyBookedError as exc:
        print(f"Second booking rejected: {exc}")

    service.cancel(booking.pnr)
    seat_free = service.search_flights("BLR", "DEL")[0].inventory.find_seat("1A").is_available
    print(f"Cancelled {booking.pnr}; seat 1A available={str(seat_free).lower()}")

    rebooked = service.book_seat("AI101", "1A", Passenger("Bob", "bob@example.com"))
    print(f"Rebooked {rebooked.pnr} for Bob on {rebooked.seat_no}")


if __name__ == "__main__":
    main()
