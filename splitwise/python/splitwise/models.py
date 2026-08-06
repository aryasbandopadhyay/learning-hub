"""Domain models: users, computed splits, expenses, and balance snapshots."""

from __future__ import annotations

import uuid
from dataclasses import dataclass, field
from types import MappingProxyType
from typing import Mapping


@dataclass(frozen=True)
class User:
    """A person in the system. Equality is by immutable id, so dict keys are stable."""

    id: str
    name: str

    def __post_init__(self) -> None:
        if not self.id:
            raise ValueError("User id is required")
        if not self.name:
            raise ValueError("User name is required")

    def __str__(self) -> str:  # pragma: no cover - trivial
        return self.name


@dataclass(frozen=True)
class Split:
    """One user's share of an expense, in integer cents to avoid float rounding bugs."""

    user: User
    amount_cents: int

    def __post_init__(self) -> None:
        if self.amount_cents < 0:
            raise ValueError("Split amount cannot be negative")


@dataclass(frozen=True)
class Expense:
    """Immutable expense after a strategy converts caller input into exact shares."""

    payer: User
    total_cents: int
    splits: tuple[Split, ...]
    id: str = field(default_factory=lambda: str(uuid.uuid4()))

    def __post_init__(self) -> None:
        if self.total_cents <= 0:
            raise ValueError("Total must be positive")
        object.__setattr__(self, "splits", tuple(self.splits))


@dataclass(frozen=True)
class BalanceSummary:
    """Snapshot returned by get_balances(user): outgoing and incoming debts."""

    user: User
    owes: Mapping[User, int]
    owed_by: Mapping[User, int]

    def __post_init__(self) -> None:
        object.__setattr__(self, "owes", MappingProxyType(dict(self.owes)))
        object.__setattr__(self, "owed_by", MappingProxyType(dict(self.owed_by)))
