"""Strategy pattern: pluggable elevator-car scheduling policies."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Optional, Sequence

from .models import Elevator, ExternalRequest


class SchedulingStrategy(ABC):
    """Selects which elevator should receive a hall-call request."""

    @abstractmethod
    def select_car(self, elevators: Sequence[Elevator], request: ExternalRequest) -> Optional[Elevator]:
        ...


class NearestCarSchedulingStrategy(SchedulingStrategy):
    """Choose the nearest suitable car: idle, or already moving toward the caller."""

    def select_car(self, elevators: Sequence[Elevator], request: ExternalRequest) -> Optional[Elevator]:
        suitable = [e for e in elevators if e.can_serve_on_current_path(request)]
        if not suitable:
            return None
        return min(suitable, key=lambda e: e.distance_to(request))
