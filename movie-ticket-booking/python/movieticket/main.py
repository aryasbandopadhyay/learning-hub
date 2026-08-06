"""Runnable demo: create holds, pay, and show failed payment release.

Run: python -m movieticket.main (from the python/ directory)
"""

from __future__ import annotations

from datetime import timedelta

from .exceptions import PaymentRejectedError, SeatUnavailableError
from .models import SeatStatus, Show
from .payment import AlwaysSuccessPaymentProcessor, FailingPaymentProcessor
from .service import BookingService


def main() -> None:
    show = Show("SHOW-1", 2, 3, 250)
    service = BookingService([show], AlwaysSuccessPaymentProcessor(), hold_window=timedelta(minutes=5))

    print("Seats at open:", len(show.seats))
    happy = service.create_booking("SHOW-1", ["R1C1", "R1C2"], "user-1")
    print(f"Created booking {happy.id} -> {happy.state.value}, total = {happy.total_price}")
    service.pay(happy.id, "PAY-OK")
    print(f"After payment -> {happy.state.value}, R1C1 = {show.seat('R1C1').status.value}")

    failed_show = Show("SHOW-2", 1, 2, 250)
    failing_service = BookingService([failed_show], FailingPaymentProcessor(), hold_window=timedelta(minutes=5))
    failed = failing_service.create_booking("SHOW-2", ["R1C1"], "user-2")
    try:
        failing_service.pay(failed.id, "PAY-NO")
    except PaymentRejectedError:
        print(f"Failed payment -> {failed.state.value}, R1C1 = {failed_show.seat('R1C1').status.value}")

    try:
        service.create_booking("SHOW-1", ["R1C1"], "user-3")
    except SeatUnavailableError:
        print(f"Booked seat cannot be held again -> {SeatStatus.BOOKED.value}")


if __name__ == "__main__":
    main()
