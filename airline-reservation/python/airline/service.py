"""The orchestrating service: search flights, book seats, cancel PNRs."""

from __future__ import annotations

import threading
from datetime import datetime, timezone
from typing import Optional

from .exceptions import BookingNotFoundError, FlightNotFoundError, NoSeatAvailableError, SeatAlreadyBookedError
from .models import Booking, Cabin, Flight, Passenger
from .strategies import CabinPricingStrategy, FixedCabinPricingStrategy


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


class AirlineReservationService:
    """Application service / aggregate root.

    Flights are read-mostly in-memory data. PNRs are stored in a dict guarded by ``_bookings_lock``
    so cancel consumes a booking exactly once. Seat inventory correctness is delegated to
    ``Seat.try_book`` rather than a coarse service-wide lock, allowing unrelated seats to book in
    parallel.
    """

    def __init__(self, pricing_strategy: Optional[CabinPricingStrategy] = None, clock=_utc_now) -> None:
        self._flights: dict[str, Flight] = {}
        self._bookings: dict[str, Booking] = {}
        self._bookings_lock = threading.Lock()
        self._pricing = pricing_strategy or FixedCabinPricingStrategy()
        self._clock = clock

    def add_flight(self, flight: Flight) -> None:
        self._flights[flight.flight_number] = flight

    def search_flights(self, origin: str, destination: str, date=None, only_with_seats: bool = False) -> list[Flight]:
        flights = [f for f in self._flights.values() if f.matches(origin, destination, date)]
        if only_with_seats:
            flights = [f for f in flights if f.inventory.has_available_seat()]
        return sorted(flights, key=lambda f: f.departure_time)

    def book_seat(self, flight_no: str, seat_no: str, passenger: Passenger) -> Booking:
        flight = self._flight_or_raise(flight_no)
        seat = flight.inventory.find_seat(seat_no)
        if seat is None:
            raise NoSeatAvailableError(f"Unknown seat {seat_no}")
        if not seat.try_book(passenger):
            raise SeatAlreadyBookedError(f"Seat {seat_no} is already booked")
        return self._save_booking(flight, seat_no, passenger, seat.cabin)

    def book_any(self, flight_no: str, cabin: Cabin, passenger: Passenger) -> Booking:
        flight = self._flight_or_raise(flight_no)
        seat = flight.inventory.try_book_any(cabin, passenger)
        if seat is None:
            raise NoSeatAvailableError(f"No available {cabin.name} seat")
        return self._save_booking(flight, seat.seat_no, passenger, cabin)

    def cancel(self, pnr: str) -> Booking:
        """Cancel consumes a PNR exactly once and then frees its seat."""
        with self._bookings_lock:
            booking = self._bookings.pop(pnr, None)
        if booking is None:
            raise BookingNotFoundError(f"Unknown or already-cancelled PNR: {pnr}")
        flight = self._flight_or_raise(booking.flight_number)
        seat = flight.inventory.find_seat(booking.seat_no)
        if seat is not None:
            seat.free()
        return booking

    def find_booking(self, pnr: str) -> Optional[Booking]:
        with self._bookings_lock:
            return self._bookings.get(pnr)

    def _flight_or_raise(self, flight_no: str) -> Flight:
        flight = self._flights.get(flight_no)
        if flight is None:
            raise FlightNotFoundError(f"Unknown flight: {flight_no}")
        return flight

    def _save_booking(self, flight: Flight, seat_no: str, passenger: Passenger, cabin: Cabin) -> Booking:
        booking = Booking(
            flight_number=flight.flight_number,
            seat_no=seat_no,
            passenger=passenger,
            cabin=cabin,
            price=self._pricing.price_for(cabin),
            booked_at=self._clock(),
        )
        with self._bookings_lock:
            self._bookings[booking.pnr] = booking
        return booking
