"""Core data models: account, card, public ATM status, and withdrawal result."""

from __future__ import annotations

import threading
from dataclasses import dataclass
from enum import Enum
from types import MappingProxyType
from typing import Mapping

from .exceptions import InsufficientFundsError


class AtmStatus(Enum):
    IDLE = "IDLE"
    CARD_INSERTED = "CARD_INSERTED"
    AUTHENTICATED = "AUTHENTICATED"
    DISPENSING = "DISPENSING"


class Account:
    """Thread-safe account balance stored as integer cents/paise, never float.

    ``withdraw`` holds ``self._lock`` across check-and-decrement. That is the concurrency boundary
    that prevents overdraft when many ATM sessions hit the same account together.
    """

    def __init__(self, account_number: str, opening_balance_cents: int) -> None:
        if opening_balance_cents < 0:
            raise ValueError("opening balance cannot be negative")
        self.account_number = account_number
        self._balance_cents = opening_balance_cents
        self._lock = threading.Lock()

    def withdraw(self, amount_cents: int) -> None:
        _require_positive(amount_cents)
        with self._lock:
            if amount_cents > self._balance_cents:
                raise InsufficientFundsError("Insufficient account balance")
            self._balance_cents -= amount_cents

    def deposit(self, amount_cents: int) -> None:
        _require_positive(amount_cents)
        with self._lock:
            self._balance_cents += amount_cents

    @property
    def balance_cents(self) -> int:
        with self._lock:
            return self._balance_cents


@dataclass(frozen=True)
class Card:
    """Simplified card: directly references an Account and keeps a demo PIN in memory."""

    card_number: str
    pin: str
    account: Account

    def matches_pin(self, candidate: str) -> bool:
        return self.pin == candidate


@dataclass(frozen=True)
class WithdrawalResult:
    amount_cents: int
    notes: Mapping[int, int]

    def __post_init__(self) -> None:
        object.__setattr__(self, "notes", MappingProxyType(dict(self.notes)))


def _require_positive(amount_cents: int) -> None:
    if amount_cents <= 0:
        raise ValueError("amount must be positive")
