"""The orchestrating service: ExpenseManager and its thread-safe balance sheet."""

from __future__ import annotations

import threading
from collections import OrderedDict
from typing import Mapping, Sequence

from .models import BalanceSummary, Expense, User
from .strategies import SplitStrategy


class ExpenseManager:
    """Application service / aggregate root.

    balances[debtor][creditor] = cents the debtor owes the creditor. add_expense holds one RLock
    because it must atomically append the expense and net multiple balance entries. Reads also lock
    briefly to return consistent snapshots.
    """

    def __init__(self) -> None:
        self._expenses: list[Expense] = []
        self._balances: OrderedDict[User, OrderedDict[User, int]] = OrderedDict()
        self._lock = threading.RLock()

    def add_expense(
        self,
        payer: User,
        total_cents: int,
        participants: Sequence[User],
        strategy: SplitStrategy,
        split_values: Mapping[User, int] | None = None,
    ) -> Expense:
        splits = strategy.split(total_cents, participants, split_values or {})
        expense = Expense(payer=payer, total_cents=total_cents, splits=tuple(splits))
        with self._lock:
            self._expenses.append(expense)
            for split in splits:
                if split.user != payer and split.amount_cents > 0:
                    self._add_debt(split.user, payer, split.amount_cents)
        return expense

    def _add_debt(self, debtor: User, creditor: User, amount_cents: int) -> None:
        """Net a new debtor->creditor amount against current same-direction and opposite debt."""
        current = self._balances.get(debtor, {}).get(creditor, 0)
        opposite = self._balances.get(creditor, {}).get(debtor, 0)
        new_debt = current + amount_cents
        if opposite >= new_debt:
            self._set_debt(debtor, creditor, 0)
            self._set_debt(creditor, debtor, opposite - new_debt)
        else:
            self._set_debt(creditor, debtor, 0)
            self._set_debt(debtor, creditor, new_debt - opposite)

    def _set_debt(self, debtor: User, creditor: User, amount_cents: int) -> None:
        row = self._balances.setdefault(debtor, OrderedDict())
        if amount_cents == 0:
            row.pop(creditor, None)
            if not row:
                self._balances.pop(debtor, None)
        else:
            row[creditor] = amount_cents

    def get_balances(self, user: User) -> BalanceSummary:
        with self._lock:
            owes = dict(self._balances.get(user, {}))
            owed_by = {debtor: row[user] for debtor, row in self._balances.items() if user in row}
        return BalanceSummary(user=user, owes=owes, owed_by=owed_by)

    def show_balances(self) -> str:
        with self._lock:
            if not self._balances:
                return "No balances"
            lines = [
                f"{debtor.name} owes {creditor.name} {format_cents(amount)}"
                for debtor, row in self._balances.items()
                for creditor, amount in row.items()
            ]
        return "\n".join(lines)

    @property
    def expenses(self) -> tuple[Expense, ...]:
        with self._lock:
            return tuple(self._expenses)


def format_cents(cents: int) -> str:
    return f"${cents / 100:.2f}"
