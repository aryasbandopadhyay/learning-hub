"""The VendingMachine aggregate root: inventory, change-making, state delegation, concurrency."""

from __future__ import annotations

import threading
from collections.abc import Iterable, Sequence

from .exceptions import InvalidDenominationError, InvalidStateError, UnknownProductError
from .models import InventoryItem, MachineStateName, MoneyResult, Product, PurchaseResult, RefundResult
from .states import IDLE, State


class VendingMachine:
    """One physical machine, one transaction at a time.

    ``RLock`` is used because ``purchase`` is an atomic convenience that calls ``insert_money`` and
    ``select_product`` while already holding the same lock. Every mutation of balance/state/stock is
    therefore serialized, preventing interleavings that could oversell inventory.
    """

    def __init__(self, items: Sequence[InventoryItem], accepted_denominations: Iterable[int]) -> None:
        self._inventory = {item.product.code: item for item in items}
        self._accepted = set(accepted_denominations)
        self._change_desc = sorted(self._accepted, reverse=True)
        self._lock = threading.RLock()
        self.state: State = IDLE
        self.balance = 0
        self.selected_product: Product | None = None
        self.last_sold_out_code: str | None = None

    @classmethod
    def demo_machine(cls) -> "VendingMachine":
        return cls(
            [
                InventoryItem(Product("WATER", "Water Bottle", 25), 2),
                InventoryItem(Product("CHIPS", "Potato Chips", 15), 3),
                InventoryItem(Product("SODA", "Soda Can", 35), 1),
            ],
            {1, 5, 10, 25},
        )

    def insert_money(self, amount: int) -> MoneyResult:
        with self._lock:
            return self.state.insert_money(self, amount)

    def select_product(self, product_code: str) -> PurchaseResult:
        with self._lock:
            return self.state.select_product(self, product_code)

    def dispense(self) -> PurchaseResult:
        with self._lock:
            return self.state.dispense(self)

    def cancel(self) -> RefundResult:
        with self._lock:
            return self.state.cancel(self)

    def purchase(self, product_code: str, coins: Sequence[int]) -> PurchaseResult:
        """Atomic full transaction used by concurrent callers."""
        with self._lock:
            if self.state.name is not MachineStateName.IDLE:
                raise InvalidStateError("purchase requires a fresh IDLE machine")
            try:
                for coin in coins:
                    self.insert_money(coin)
                return self.select_product(product_code)
            except Exception:
                self._reset_transaction()  # whole-transaction helper must not leak another caller's money
                raise

    def require_item(self, product_code: str) -> InventoryItem:
        try:
            return self._inventory[product_code]
        except KeyError:
            raise UnknownProductError(f"Unknown product: {product_code}") from None

    def accept_coin(self, amount: int) -> None:
        if amount not in self._accepted:
            raise InvalidDenominationError(f"Unsupported denomination: {amount}")
        self.balance += amount

    def complete_dispense(self) -> PurchaseResult:
        if self.selected_product is None:
            raise InvalidStateError("No product selected for dispensing")
        item = self.require_item(self.selected_product.code)
        item.decrement()
        change = tuple(self._make_change(self.balance - self.selected_product.price))
        product = self.selected_product
        self._reset_transaction()
        return PurchaseResult(product, change, f"Dispensed {product.code}, change = {list(change)}")

    def refund_and_reset(self, message: str) -> RefundResult:
        coins = tuple(self._make_change(self.balance))
        amount = self.balance
        self._reset_transaction()
        return RefundResult(coins, amount, f"{message}, refund = {list(coins)}")

    def _make_change(self, amount: int) -> list[int]:
        coins: list[int] = []
        remaining = amount
        for denom in self._change_desc:
            while remaining >= denom:
                coins.append(denom)
                remaining -= denom
        if remaining != 0:
            raise RuntimeError(f"Cannot make exact greedy change for {amount}")
        return coins

    def _reset_transaction(self) -> None:
        self.balance = 0
        self.selected_product = None
        self.state = IDLE

    def stock_of(self, product_code: str) -> int:
        with self._lock:
            return self.require_item(product_code).stock

    def current_state(self) -> MachineStateName:
        with self._lock:
            return self.state.name

    def stock_snapshot(self) -> dict[str, int]:
        with self._lock:
            return {code: item.stock for code, item in self._inventory.items()}
