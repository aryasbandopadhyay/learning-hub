"""Strategy pattern: pluggable overdue fine calculation."""

from __future__ import annotations

import math
from abc import ABC, abstractmethod
from datetime import datetime

from .models import Loan


class FineStrategy(ABC):
    """How to price overdue returns. Swap this without touching the service."""

    @abstractmethod
    def calculate_fine(self, loan: Loan, return_time: datetime) -> int:
        ...


class PerDayFineStrategy(FineStrategy):
    """Charge a fixed amount per started overdue day; not late means zero fine."""

    def __init__(self, rate_per_day: int) -> None:
        self._rate_per_day = rate_per_day

    def calculate_fine(self, loan: Loan, return_time: datetime) -> int:
        seconds = (return_time - loan.due_time).total_seconds()
        if seconds <= 0:
            return 0
        days = math.ceil(seconds / 86_400.0)
        return days * self._rate_per_day
