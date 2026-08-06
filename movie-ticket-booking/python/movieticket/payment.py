"""Payment strategy abstraction and fakes for tests/demo."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass

from .models import Booking


@dataclass(frozen=True)
class PaymentResult:
    success: bool
    message: str


class PaymentProcessor(ABC):
    """Dependency-inverted payment gateway. Real gateways can replace these fakes later."""

    @abstractmethod
    def process(self, booking: Booking, payment_ref: str) -> PaymentResult:
        raise NotImplementedError


class AlwaysSuccessPaymentProcessor(PaymentProcessor):
    def process(self, booking: Booking, payment_ref: str) -> PaymentResult:
        return PaymentResult(True, f"Payment accepted: {payment_ref}")


class FailingPaymentProcessor(PaymentProcessor):
    def process(self, booking: Booking, payment_ref: str) -> PaymentResult:
        return PaymentResult(False, f"Payment declined: {payment_ref}")
