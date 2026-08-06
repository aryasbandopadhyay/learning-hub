"""Runnable demo: record a few expenses and print the resulting net balances.

Run:  python -m splitwise.main   (from the python/ directory)
"""

from __future__ import annotations

from .models import User
from .service import ExpenseManager
from .strategies import EqualSplitStrategy, ExactSplitStrategy, PercentSplitStrategy


def main() -> None:
    alice = User("u1", "Alice")
    bob = User("u2", "Bob")
    charlie = User("u3", "Charlie")
    manager = ExpenseManager()

    manager.add_expense(alice, 30000, [alice, bob, charlie], EqualSplitStrategy())
    manager.add_expense(bob, 12000, [alice, bob], ExactSplitStrategy(), {alice: 5000, bob: 7000})
    manager.add_expense(charlie, 10000, [alice, charlie], PercentSplitStrategy(), {alice: 25, charlie: 75})

    print("Recorded expenses:", len(manager.expenses))
    print("Balances:")
    print(manager.show_balances())


if __name__ == "__main__":
    main()
