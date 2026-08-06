"""Runnable demo: card, PIN, withdrawal, eject.

Run:  python -m atm.main   (from the python/ directory)
"""

from __future__ import annotations

from .atm import AtmMachine, CashDispenser
from .models import Account, Card


def main() -> None:
    account = Account("ACC-1", 1_000_000)  # ₹10,000.00 in cents/paise
    card = Card("CARD-1", "1234", account)
    atm = AtmMachine(CashDispenser.demo_dispenser())

    print("ATM state at open:", atm.status.value)
    atm.insert_card(card)
    print("After card insert:", atm.status.value)
    atm.enter_pin("1234")
    print("After PIN:", atm.status.value)
    print("Balance before withdrawal:", money(atm.check_balance()))

    result = atm.withdraw(300_000)
    print(f"Dispensed: {money(result.amount_cents)} as {dict(result.notes)}")
    print("Balance after withdrawal:", money(atm.check_balance()))
    atm.eject_card()
    print("After eject:", atm.status.value)


def money(cents: int) -> str:
    return f"INR {cents / 100:,.2f}"


if __name__ == "__main__":
    main()
