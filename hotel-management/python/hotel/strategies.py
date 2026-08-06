"""Pricing strategy abstractions and the MVP nightly implementation."""

from __future__ import annotations

from abc import ABC, abstractmethod
from datetime import date


class PricingStrategy(ABC):
    """Strategy seam: weekend/seasonal/demand pricing can replace this without touching service."""

    @abstractmethod
    def calculate_price(self, room, check_in: date, check_out: date) -> int:
        raise NotImplementedError


class NightlyPricingStrategy(PricingStrategy):
    """MVP pricing: nights in [check_in, check_out) * room type rate."""

    def calculate_price(self, room, check_in: date, check_out: date) -> int:
        nights = (check_out - check_in).days
        return nights * room.room_type.nightly_rate
