"""The orchestrating service: fleet search, reservation, and lifecycle transitions."""

from __future__ import annotations

import threading
from datetime import date
from typing import Sequence

from .exceptions import (
    CarUnavailableError,
    InvalidDateRangeError,
    InvalidReservationStateError,
    ReservationNotFoundError,
)
from .models import Car, CarType, Reservation, ReservationStatus
from .strategies import PricingStrategy


class RentalCompany:
    """Aggregate root wiring models + pricing strategy.

    Concurrency: there is no company-wide reservation lock. Each car has a lock, and reserve()
    performs "check no overlap -> calculate price -> append reservation" under that car lock. That
    atomic critical section is exactly what prevents overlapping double-booking.
    """

    def __init__(self, cars: Sequence[Car], pricing_strategy: PricingStrategy) -> None:
        self._cars: dict[str, Car] = {}
        self._reservations: dict[str, Reservation] = {}
        self._reservations_lock = threading.Lock()
        self._pricing = pricing_strategy
        for car in cars:
            self.add_car(car)

    def add_car(self, car: Car) -> None:
        self._cars[car.id] = car

    def search_available(
        self, location: str, car_type: CarType, pickup_date: date, return_date: date
    ) -> list[Car]:
        self._validate_range(pickup_date, return_date)
        result: list[Car] = []
        for car in self._cars.values():
            if car.location != location or car.type is not car_type:
                continue
            with car.lock:
                if self._is_available_locked(car, pickup_date, return_date):
                    result.append(car)
        return result

    def reserve(self, car_id: str, pickup_date: date, return_date: date) -> Reservation:
        self._validate_range(pickup_date, return_date)
        car = self._cars.get(car_id)
        if car is None:
            raise CarUnavailableError(f"Unknown car: {car_id}")

        with car.lock:
            if not self._is_available_locked(car, pickup_date, return_date):
                raise CarUnavailableError(f"Car unavailable for requested dates: {car_id}")
            total = self._pricing.calculate_price(car, pickup_date, return_date)
            reservation = Reservation(car, pickup_date, return_date, total)
            car.reservations.append(reservation)
            with self._reservations_lock:
                self._reservations[reservation.id] = reservation
            return reservation

    def pick_up(self, reservation_id: str) -> Reservation:
        return self._transition(
            reservation_id, ReservationStatus.CONFIRMED, ReservationStatus.PICKED_UP
        )

    def return_car(self, reservation_id: str) -> Reservation:
        return self._transition(
            reservation_id, ReservationStatus.PICKED_UP, ReservationStatus.RETURNED
        )

    def cancel(self, reservation_id: str) -> Reservation:
        return self._transition(
            reservation_id, ReservationStatus.CONFIRMED, ReservationStatus.CANCELLED
        )

    def reservations_for_car(self, car_id: str) -> list[Reservation]:
        car = self._cars.get(car_id)
        if car is None:
            return []
        with car.lock:
            return list(car.reservations)

    def _transition(
        self,
        reservation_id: str,
        expected: ReservationStatus,
        next_status: ReservationStatus,
    ) -> Reservation:
        with self._reservations_lock:
            reservation = self._reservations.get(reservation_id)
        if reservation is None:
            raise ReservationNotFoundError(reservation_id)
        with reservation.car.lock:
            if reservation.status is not expected:
                raise InvalidReservationStateError(
                    f"Expected {expected.name} but was {reservation.status.name}"
                )
            reservation.status = next_status
            return reservation

    @staticmethod
    def _is_available_locked(car: Car, pickup_date: date, return_date: date) -> bool:
        return not any(
            r.blocks_availability() and r.overlaps(pickup_date, return_date)
            for r in car.reservations
        )

    @staticmethod
    def _validate_range(pickup_date: date, return_date: date) -> None:
        if pickup_date >= return_date:
            raise InvalidDateRangeError("pickup must be before return")
