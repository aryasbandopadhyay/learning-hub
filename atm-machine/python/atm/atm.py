"""ATM facade plus CashDispenser service."""

from __future__ import annotations

import threading
from collections import OrderedDict
from types import MappingProxyType
from typing import Mapping

from .exceptions import CashDispenseError
from .models import AtmStatus, Card, WithdrawalResult
from .states import AtmState, IdleState


class CashDispenser:
    """Maintains note inventory and computes greedy denomination breakdowns.

    Denominations are integer cents/paise. The demo uses ₹2000, ₹500, ₹200, and ₹100 as 200000,
    50000, 20000, and 10000. The lock makes plan+decrement atomic for one physical dispenser.
    """

    def __init__(self, opening_inventory: Mapping[int, int]) -> None:
        for denomination, count in opening_inventory.items():
            if denomination <= 0 or count < 0:
                raise ValueError("invalid denomination inventory")
        self._notes: OrderedDict[int, int] = OrderedDict(
            sorted(opening_inventory.items(), key=lambda item: item[0], reverse=True)
        )
        self._lock = threading.RLock()

    @classmethod
    def demo_dispenser(cls) -> "CashDispenser":
        return cls({200_000: 5, 50_000: 10, 20_000: 10, 10_000: 20})

    @property
    def inventory(self) -> Mapping[int, int]:
        with self._lock:
            return MappingProxyType(dict(self._notes))

    def plan_breakdown(self, amount_cents: int) -> dict[int, int]:
        with self._lock:
            if amount_cents <= 0:
                raise CashDispenseError("Withdrawal amount must be positive")
            remaining = amount_cents
            plan: dict[int, int] = {}
            for denomination, available in self._notes.items():
                needed = min(remaining // denomination, available)
                if needed:
                    plan[denomination] = needed
                    remaining -= denomination * needed
            if remaining != 0:
                raise CashDispenseError("ATM cannot dispense exact amount with available notes")
            return plan

    def dispense(self, amount_cents: int) -> dict[int, int]:
        with self._lock:
            plan = self.plan_breakdown(amount_cents)
            for denomination, used in plan.items():
                self._notes[denomination] -= used
            return plan


class AtmMachine:
    """Physical ATM facade. Public calls delegate to the current State object."""

    def __init__(self, cash_dispenser: CashDispenser, max_pin_attempts: int = 3) -> None:
        self._cash_dispenser = cash_dispenser
        self.max_pin_attempts = max_pin_attempts
        self._state: AtmState = IdleState()
        self._current_card: Card | None = None
        self._failed_pin_attempts = 0
        self._lock = threading.RLock()

    def insert_card(self, card: Card) -> None:
        with self._lock:
            self._state.insert_card(self, card)

    def enter_pin(self, pin: str) -> None:
        with self._lock:
            self._state.enter_pin(self, pin)

    def check_balance(self) -> int:
        with self._lock:
            return self._state.check_balance(self)

    def withdraw(self, amount_cents: int) -> WithdrawalResult:
        with self._lock:
            return self._state.withdraw(self, amount_cents)

    def deposit(self, amount_cents: int) -> None:
        with self._lock:
            self._state.deposit(self, amount_cents)

    def eject_card(self) -> None:
        with self._lock:
            self._state.eject_card(self)

    @property
    def status(self) -> AtmStatus:
        with self._lock:
            return self._state.status

    @property
    def cash_inventory(self) -> Mapping[int, int]:
        return self._cash_dispenser.inventory

    def _dispense_cash(self, amount_cents: int) -> WithdrawalResult:
        if self._current_card is None:  # defensive; state guards normally prevent this
            raise RuntimeError("no card attached")
        with self._cash_dispenser._lock:
            plan = self._cash_dispenser.plan_breakdown(amount_cents)
            self._current_card.account.withdraw(amount_cents)
            self._cash_dispenser.dispense(amount_cents)
            return WithdrawalResult(amount_cents, plan)

    def _transition_to(self, state: AtmState) -> None:
        self._state = state

    def _attach_card(self, card: Card) -> None:
        self._current_card = card

    def _clear_card(self) -> None:
        self._current_card = None
        self._reset_failed_pin_attempts()

    def _increment_failed_pin_attempts(self) -> int:
        self._failed_pin_attempts += 1
        return self._failed_pin_attempts

    def _reset_failed_pin_attempts(self) -> None:
        self._failed_pin_attempts = 0
