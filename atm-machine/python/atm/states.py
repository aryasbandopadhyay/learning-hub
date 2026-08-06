"""State pattern implementation for ATM session flow."""

from __future__ import annotations

from typing import TYPE_CHECKING

from .exceptions import AuthenticationError, InvalidOperationError
from .models import AtmStatus, Card, WithdrawalResult

if TYPE_CHECKING:  # pragma: no cover
    from .atm import AtmMachine


class AtmState:
    """Base State. Invalid operations fail here; concrete states override only allowed actions."""

    status = AtmStatus.IDLE

    def insert_card(self, atm: "AtmMachine", card: Card) -> None:
        raise self._invalid("insert_card")

    def enter_pin(self, atm: "AtmMachine", pin: str) -> None:
        raise self._invalid("enter_pin")

    def check_balance(self, atm: "AtmMachine") -> int:
        raise self._invalid("check_balance")

    def withdraw(self, atm: "AtmMachine", amount_cents: int) -> WithdrawalResult:
        raise self._invalid("withdraw")

    def deposit(self, atm: "AtmMachine", amount_cents: int) -> None:
        raise self._invalid("deposit")

    def eject_card(self, atm: "AtmMachine") -> None:
        raise self._invalid("eject_card")

    def _invalid(self, operation: str) -> InvalidOperationError:
        return InvalidOperationError(f"{operation} is not allowed while ATM is {self.status.value}")


class IdleState(AtmState):
    status = AtmStatus.IDLE

    def insert_card(self, atm: "AtmMachine", card: Card) -> None:
        atm._attach_card(card)
        atm._reset_failed_pin_attempts()
        atm._transition_to(CardInsertedState())


class CardInsertedState(AtmState):
    status = AtmStatus.CARD_INSERTED

    def enter_pin(self, atm: "AtmMachine", pin: str) -> None:
        if atm._current_card.matches_pin(pin):
            atm._reset_failed_pin_attempts()
            atm._transition_to(AuthenticatedState())
            return
        failures = atm._increment_failed_pin_attempts()
        if failures >= atm.max_pin_attempts:
            atm._clear_card()
            atm._transition_to(IdleState())
            raise AuthenticationError("Too many wrong PIN attempts; card ejected")
        raise AuthenticationError("Incorrect PIN")

    def eject_card(self, atm: "AtmMachine") -> None:
        atm._clear_card()
        atm._transition_to(IdleState())


class AuthenticatedState(AtmState):
    status = AtmStatus.AUTHENTICATED

    def check_balance(self, atm: "AtmMachine") -> int:
        return atm._current_card.account.balance_cents

    def withdraw(self, atm: "AtmMachine", amount_cents: int) -> WithdrawalResult:
        atm._transition_to(DispensingState())
        try:
            return atm._dispense_cash(amount_cents)
        finally:
            atm._transition_to(self)

    def deposit(self, atm: "AtmMachine", amount_cents: int) -> None:
        atm._current_card.account.deposit(amount_cents)

    def eject_card(self, atm: "AtmMachine") -> None:
        atm._clear_card()
        atm._transition_to(IdleState())


class DispensingState(AtmState):
    """Short-lived transaction state; rejects outside calls while withdrawal is in progress."""

    status = AtmStatus.DISPENSING
