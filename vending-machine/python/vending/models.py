"""Core model objects: catalog product, mutable inventory, state names, and result DTOs."""

from __future__ import annotations

from dataclasses import dataclass
from enum import Enum, auto


class MachineStateName(Enum):
    IDLE = auto()
    HAS_MONEY = auto()
    DISPENSING = auto()
    SOLD_OUT = auto()


@dataclass(frozen=True)
class Product:
    """Immutable catalog entry. Stock is deliberately separate in ``InventoryItem``."""

    code: str
    name: str
    price: int

    def __post_init__(self) -> None:
        if not self.code:
            raise ValueError("Product code is required")
        if self.price <= 0:
            raise ValueError("Price must be positive")


@dataclass
class InventoryItem:
    """Mutable stock holder. The VendingMachine lock guards calls to ``decrement``."""

    product: Product
    stock: int

    def __post_init__(self) -> None:
        if self.stock < 0:
            raise ValueError("Stock cannot be negative")

    @property
    def is_available(self) -> bool:
        return self.stock > 0

    def decrement(self) -> None:
        if self.stock <= 0:
            raise RuntimeError("Cannot decrement sold-out stock")
        self.stock -= 1


@dataclass(frozen=True)
class MoneyResult:
    balance: int
    message: str


@dataclass(frozen=True)
class PurchaseResult:
    product: Product
    change: tuple[int, ...]
    message: str


@dataclass(frozen=True)
class RefundResult:
    coins: tuple[int, ...]
    amount: int
    message: str
