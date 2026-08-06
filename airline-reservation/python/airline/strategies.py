"""Strategy pattern: pluggable cabin pricing."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Optional

from .models import Cabin


class CabinPricingStrategy(ABC):
    """How to price a cabin. Swap this to add dynamic fares without touching the service."""

    @abstractmethod
    def price_for(self, cabin: Cabin) -> int:
        ...


class FixedCabinPricingStrategy(CabinPricingStrategy):
    """Simple deterministic fare table, intentionally not a full pricing engine."""

    DEFAULT_PRICES = {Cabin.ECONOMY: 5000, Cabin.BUSINESS: 12000}

    def __init__(self, prices: Optional[dict[Cabin, int]] = None) -> None:
        self._prices = dict(prices) if prices else dict(self.DEFAULT_PRICES)

    def price_for(self, cabin: Cabin) -> int:
        return self._prices.get(cabin, 0)
