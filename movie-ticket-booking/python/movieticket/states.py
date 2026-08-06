"""Explicit booking state machine."""

from __future__ import annotations

from enum import Enum

from .exceptions import InvalidBookingStateError


class BookingState(Enum):
    CREATED = "CREATED"
    SEATS_HELD = "SEATS_HELD"
    PAYMENT_PENDING = "PAYMENT_PENDING"
    CONFIRMED = "CONFIRMED"
    EXPIRED = "EXPIRED"
    FAILED = "FAILED"


_ALLOWED: dict[BookingState, set[BookingState]] = {
    BookingState.CREATED: {BookingState.SEATS_HELD},
    BookingState.SEATS_HELD: {BookingState.PAYMENT_PENDING, BookingState.EXPIRED},
    BookingState.PAYMENT_PENDING: {BookingState.CONFIRMED, BookingState.FAILED, BookingState.EXPIRED},
    BookingState.CONFIRMED: set(),
    BookingState.EXPIRED: set(),
    BookingState.FAILED: set(),
}


def ensure_can_move(source: BookingState, target: BookingState) -> None:
    """Reject accidental illegal jumps such as EXPIRED -> CONFIRMED."""
    if target not in _ALLOWED[source]:
        raise InvalidBookingStateError(f"Illegal booking transition: {source.value} -> {target.value}")
