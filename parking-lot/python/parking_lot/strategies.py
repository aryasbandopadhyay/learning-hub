"""Strategy pattern: pluggable fee calculation and spot assignment policies."""

from __future__ import annotations

import math
from abc import ABC, abstractmethod
from datetime import datetime
from typing import TYPE_CHECKING, Optional, Sequence

from .models import ParkingSpot, SpotType, Ticket, Vehicle

if TYPE_CHECKING:  # avoid a runtime import cycle (lot imports strategies)
    from .lot import Level


# --------------------------------------------------------------------------------------------- Fee


class FeeStrategy(ABC):
    """How to price a session. Swap this to change pricing without touching the service."""

    @abstractmethod
    def calculate_fee(self, ticket: Ticket, exit_time: datetime) -> int:
        ...


class HourlyFeeStrategy(FeeStrategy):
    """Charge per started hour at a size-dependent rate; minimum one hour."""

    DEFAULT_RATES = {SpotType.SMALL: 10, SpotType.MEDIUM: 20, SpotType.LARGE: 30}

    def __init__(self, rates: Optional[dict[SpotType, int]] = None) -> None:
        self._rates = dict(rates) if rates else dict(self.DEFAULT_RATES)

    def calculate_fee(self, ticket: Ticket, exit_time: datetime) -> int:
        seconds = max(0.0, (exit_time - ticket.entry_time).total_seconds())
        hours = max(1, math.ceil(seconds / 3600.0))  # round up, min 1 hour
        rate = self._rates.get(ticket.spot.spot_type, 0)
        return hours * rate


# -------------------------------------------------------------------------------------- Assignment


class SpotAssignmentStrategy(ABC):
    """Decides WHICH spot a vehicle gets, and atomically reserves it."""

    @abstractmethod
    def assign(self, levels: "Sequence[Level]", vehicle: Vehicle) -> Optional[ParkingSpot]:
        ...


class NearestFirstAssignmentStrategy(SpotAssignmentStrategy):
    """Scan levels then spots in order; claim the first compatible free spot.

    Thread-safety comes from ``ParkingSpot.try_occupy``: even if two threads inspect the same spot,
    only one ``try_occupy`` returns True.
    """

    def assign(self, levels: "Sequence[Level]", vehicle: Vehicle) -> Optional[ParkingSpot]:
        for level in levels:
            for spot in level.spots:
                if spot.try_occupy(vehicle):
                    return spot
        return None
