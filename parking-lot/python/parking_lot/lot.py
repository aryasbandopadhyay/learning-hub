"""The orchestrating service: Level, Receipt, and ParkingLot."""

from __future__ import annotations

import threading
from dataclasses import dataclass
from datetime import datetime, timezone
from typing import Callable, Optional, Sequence

from .exceptions import InvalidTicketError, NoAvailableSpotError
from .models import ParkingSpot, SpotType, Ticket, Vehicle
from .strategies import FeeStrategy, SpotAssignmentStrategy


class Level:
    """A floor holding a fixed list of spots. Concurrency is handled per-spot, so no level lock."""

    def __init__(self, level_number: int, spots: Sequence[ParkingSpot]) -> None:
        self.level_number = level_number
        self.spots: tuple[ParkingSpot, ...] = tuple(spots)

    @classmethod
    def of(cls, level_number: int, small: int, medium: int, large: int) -> "Level":
        spots: list[ParkingSpot] = []
        idx = 0
        for _ in range(small):
            spots.append(ParkingSpot(f"L{level_number}-S{idx}", SpotType.SMALL))
            idx += 1
        for _ in range(medium):
            spots.append(ParkingSpot(f"L{level_number}-S{idx}", SpotType.MEDIUM))
            idx += 1
        for _ in range(large):
            spots.append(ParkingSpot(f"L{level_number}-S{idx}", SpotType.LARGE))
            idx += 1
        return cls(level_number, spots)

    def available_count(self) -> int:
        return sum(1 for s in self.spots if not s.is_occupied)


@dataclass(frozen=True)
class Receipt:
    ticket: Ticket
    exit_time: datetime
    fee: int


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


class ParkingLot:
    """Aggregate root wiring model + strategies.

    Depends only on the FeeStrategy / SpotAssignmentStrategy abstractions (Dependency Inversion).
    A ``clock`` callable is injected so fee tests are deterministic.

    Concurrency: no coarse lot-wide lock for reservation (that is delegated to atomic per-spot
    ``try_occupy``). A small lock guards the ``_active_tickets`` dict so issuing/removing tickets is
    safe, and unpark frees each spot exactly once.
    """

    def __init__(
        self,
        levels: Sequence[Level],
        assignment_strategy: SpotAssignmentStrategy,
        fee_strategy: FeeStrategy,
        clock: Callable[[], datetime] = _utc_now,
    ) -> None:
        self._levels: tuple[Level, ...] = tuple(levels)
        self._assignment = assignment_strategy
        self._fee = fee_strategy
        self._clock = clock
        self._active_tickets: dict[str, Ticket] = {}
        self._tickets_lock = threading.Lock()

    def park(self, vehicle: Vehicle) -> Ticket:
        """Reserve a compatible spot atomically and issue a ticket."""
        spot = self._assignment.assign(self._levels, vehicle)
        if spot is None:
            raise NoAvailableSpotError(f"No available spot for {vehicle!r}")
        ticket = Ticket(vehicle=vehicle, spot=spot, entry_time=self._clock())
        with self._tickets_lock:
            self._active_tickets[ticket.id] = ticket
        return ticket

    def unpark(self, ticket_id: str) -> Receipt:
        """Free the spot, compute the fee, and invalidate the ticket.

        Popping under the lock makes double-exit impossible: two threads presenting the same ticket
        -> only one gets the Ticket object and frees the spot.
        """
        with self._tickets_lock:
            ticket = self._active_tickets.pop(ticket_id, None)
        if ticket is None:
            raise InvalidTicketError(f"Unknown or already-used ticket: {ticket_id}")
        exit_time = self._clock()
        fee = self._fee.calculate_fee(ticket, exit_time)
        ticket.spot.free()
        return Receipt(ticket=ticket, exit_time=exit_time, fee=fee)

    def available_spots(self) -> int:
        return sum(level.available_count() for level in self._levels)

    def find_ticket(self, ticket_id: str) -> Optional[Ticket]:
        with self._tickets_lock:
            return self._active_tickets.get(ticket_id)

    @property
    def levels(self) -> tuple[Level, ...]:
        return self._levels
