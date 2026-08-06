"""State pattern implementations for the vending transaction lifecycle."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import TYPE_CHECKING

from .exceptions import InsufficientFundsError, InvalidStateError, OutOfStockError
from .models import MachineStateName, MoneyResult, PurchaseResult, RefundResult

if TYPE_CHECKING:  # avoid a runtime import cycle; machine imports these states
    from .machine import VendingMachine


class State(ABC):
    """The service delegates user operations here; each state owns its legal transitions."""

    @property
    @abstractmethod
    def name(self) -> MachineStateName:
        ...

    def insert_money(self, machine: "VendingMachine", amount: int) -> MoneyResult:
        raise InvalidStateError(f"insert_money is not allowed while machine is {self.name.name}")

    def select_product(self, machine: "VendingMachine", product_code: str) -> PurchaseResult:
        raise InvalidStateError(f"select_product is not allowed while machine is {self.name.name}")

    def dispense(self, machine: "VendingMachine") -> PurchaseResult:
        raise InvalidStateError(f"dispense is not allowed while machine is {self.name.name}")

    def cancel(self, machine: "VendingMachine") -> RefundResult:
        raise InvalidStateError(f"cancel is not allowed while machine is {self.name.name}")


class IdleState(State):
    """No active transaction. Inserting the first coin moves to HAS_MONEY."""

    @property
    def name(self) -> MachineStateName:
        return MachineStateName.IDLE

    def insert_money(self, machine: "VendingMachine", amount: int) -> MoneyResult:
        machine.accept_coin(amount)
        machine.state = HAS_MONEY
        return MoneyResult(machine.balance, f"Accepted {amount}, balance = {machine.balance}")


class HasMoneyState(State):
    """The customer may insert more coins, select a product, or cancel for a refund."""

    @property
    def name(self) -> MachineStateName:
        return MachineStateName.HAS_MONEY

    def insert_money(self, machine: "VendingMachine", amount: int) -> MoneyResult:
        machine.accept_coin(amount)
        return MoneyResult(machine.balance, f"Accepted {amount}, balance = {machine.balance}")

    def select_product(self, machine: "VendingMachine", product_code: str) -> PurchaseResult:
        item = machine.require_item(product_code)
        product = item.product
        if not item.is_available:
            machine.last_sold_out_code = product_code
            machine.state = SOLD_OUT
            machine.state = HAS_MONEY  # rejection branch preserves money for another choice/cancel
            raise OutOfStockError(f"{product_code} is sold out")
        if machine.balance < product.price:
            raise InsufficientFundsError(f"Need {product.price}, but balance is {machine.balance}")
        machine.selected_product = product
        machine.state = DISPENSING
        return machine.dispense()

    def cancel(self, machine: "VendingMachine") -> RefundResult:
        return machine.refund_and_reset("Transaction cancelled")


class DispensingState(State):
    """Short-lived state that decrements inventory, makes change, and returns to IDLE."""

    @property
    def name(self) -> MachineStateName:
        return MachineStateName.DISPENSING

    def dispense(self, machine: "VendingMachine") -> PurchaseResult:
        return machine.complete_dispense()


class SoldOutState(State):
    """Explicit SOLD_OUT branch for diagrams and clear rejection behaviour."""

    @property
    def name(self) -> MachineStateName:
        return MachineStateName.SOLD_OUT

    def cancel(self, machine: "VendingMachine") -> RefundResult:
        return machine.refund_and_reset("Sold-out selection cancelled")


IDLE = IdleState()
HAS_MONEY = HasMoneyState()
DISPENSING = DispensingState()
SOLD_OUT = SoldOutState()
