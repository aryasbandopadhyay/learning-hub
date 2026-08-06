"""Strategy pattern: pluggable algorithms for computing expense shares."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Mapping, Sequence

from .exceptions import InvalidSplitError
from .models import Split, User


class SplitStrategy(ABC):
    """Common contract for all split algorithms.

    ExpenseManager depends on this abstraction only. Adding weighted shares later means creating a
    new strategy class, not editing balance-sheet code.
    """

    @abstractmethod
    def split(self, total_cents: int, participants: Sequence[User], values: Mapping[User, int] | None = None) -> list[Split]:
        ...


class EqualSplitStrategy(SplitStrategy):
    """Divide by headcount; if cents do not divide evenly, first users get 1 extra cent."""

    def split(self, total_cents: int, participants: Sequence[User], values: Mapping[User, int] | None = None) -> list[Split]:
        if total_cents <= 0:
            raise InvalidSplitError("Total must be positive")
        if not participants:
            raise InvalidSplitError("At least one participant is required")
        base, remainder = divmod(total_cents, len(participants))
        return [Split(user, base + (1 if i < remainder else 0)) for i, user in enumerate(participants)]


class ExactSplitStrategy(SplitStrategy):
    """Caller supplies exact cents per participant; the shares must sum exactly to total."""

    def split(self, total_cents: int, participants: Sequence[User], values: Mapping[User, int] | None = None) -> list[Split]:
        if not participants:
            raise InvalidSplitError("At least one participant is required")
        if values is None:
            raise InvalidSplitError("Exact split requires amounts")
        splits: list[Split] = []
        total = 0
        for user in participants:
            amount = values.get(user)
            if amount is None or amount < 0:
                raise InvalidSplitError(f"Missing or negative exact amount for {user}")
            total += amount
            splits.append(Split(user, amount))
        if total != total_cents:
            raise InvalidSplitError("Exact split amounts must sum to total")
        return splits


class PercentSplitStrategy(SplitStrategy):
    """Integer percentages must sum to 100; cents are allocated using integer arithmetic."""

    def split(self, total_cents: int, participants: Sequence[User], values: Mapping[User, int] | None = None) -> list[Split]:
        if not participants:
            raise InvalidSplitError("At least one participant is required")
        if values is None:
            raise InvalidSplitError("Percent split requires percentages")
        percents = []
        for user in participants:
            percent = values.get(user)
            if percent is None or percent < 0:
                raise InvalidSplitError(f"Missing or negative percent for {user}")
            percents.append(percent)
        if sum(percents) != 100:
            raise InvalidSplitError("Percent split must sum to 100")

        splits: list[Split] = []
        allocated = 0
        for i, user in enumerate(participants):
            amount = total_cents * values[user] // 100
            if i == len(participants) - 1:
                amount = total_cents - allocated  # last participant receives rounding remainder
            allocated += amount
            splits.append(Split(user, amount))
        return splits
