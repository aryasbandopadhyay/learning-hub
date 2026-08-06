"""Strategy pattern: pluggable pricing policies."""

from __future__ import annotations

from abc import ABC, abstractmethod
from datetime import date

from .models import Car


class PricingStrategy(ABC):
    """How to price a rental. Swap to weekend/seasonal/loyalty pricing later."""

    @abstractmethod
    def calculate_price(self, car: Car, pickup_date: date, return_date: date) -> int:
        ...


class DailyRatePricingStrategy(PricingStrategy):
    """Default MVP pricing: days * daily rate from the car type."""

    def calculate_price(self, car: Car, pickup_date: date, return_date: date) -> int:
        days = (return_date - pickup_date).days
        return days * car.type.daily_rate
