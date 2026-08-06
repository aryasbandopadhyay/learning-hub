"""Application service for search, booking, and reservation lifecycle workflows."""

from __future__ import annotations

import threading
from datetime import date

from .exceptions import ReservationNotFoundError, RoomNotFoundError
from .models import Hotel, Reservation, ReservationStatus, Room, RoomType
from .strategies import PricingStrategy


class HotelManagementService:
    """Coordinates the hotel use cases while Room owns per-room locking.

    Search asks each room for a locked availability snapshot. Booking a specific room delegates to
    Room.book, where the overlap check and reservation append are atomic under that room's lock.
    """

    def __init__(self, hotel: Hotel, pricing_strategy: PricingStrategy) -> None:
        self._hotel = hotel
        self._pricing = pricing_strategy
        self._reservations_by_id: dict[str, Reservation] = {}
        self._reservations_lock = threading.Lock()

    def search_available_rooms(self, room_type: RoomType, check_in: date, check_out: date) -> list[Room]:
        return sorted(
            [room for room in self._hotel.rooms if room.room_type is room_type and room.is_available(check_in, check_out)],
            key=lambda room: room.id,
        )

    def book_room(self, room_id: str, check_in: date, check_out: date) -> Reservation:
        room = self._find_room(room_id)
        reservation = room.book(check_in, check_out, self._pricing)
        with self._reservations_lock:
            self._reservations_by_id[reservation.id] = reservation
        return reservation

    def check_in(self, reservation_id: str) -> Reservation:
        reservation = self._find_reservation(reservation_id)
        reservation.check_in_reservation()
        return reservation

    def check_out(self, reservation_id: str) -> Reservation:
        reservation = self._find_reservation(reservation_id)
        reservation.check_out_reservation()
        return reservation

    def cancel(self, reservation_id: str) -> Reservation:
        reservation = self._find_reservation(reservation_id)
        reservation.cancel()
        return reservation

    def reservations_for_room(self, room_id: str) -> tuple[Reservation, ...]:
        return self._find_room(room_id).reservations

    def active_overlapping_reservations(self, room_id: str, check_in: date, check_out: date) -> int:
        return sum(
            1
            for r in self.reservations_for_room(room_id)
            if r.status in {ReservationStatus.CONFIRMED, ReservationStatus.CHECKED_IN}
            and r.overlaps(check_in, check_out)
        )

    def _find_room(self, room_id: str) -> Room:
        for room in self._hotel.rooms:
            if room.id == room_id:
                return room
        raise RoomNotFoundError(f"Room not found: {room_id}")

    def _find_reservation(self, reservation_id: str) -> Reservation:
        with self._reservations_lock:
            reservation = self._reservations_by_id.get(reservation_id)
        if reservation is None:
            raise ReservationNotFoundError(f"Reservation not found: {reservation_id}")
        return reservation
