"""End-to-end tests for the Splitwise MVP, including concurrent add_expense updates."""

from __future__ import annotations

import threading

import pytest

from splitwise.exceptions import InvalidSplitError
from splitwise.models import User
from splitwise.service import ExpenseManager
from splitwise.strategies import EqualSplitStrategy, ExactSplitStrategy, PercentSplitStrategy


def users() -> tuple[User, User, User]:
    return User("u1", "Alice"), User("u2", "Bob"), User("u3", "Charlie")


def test_equal_split_creates_debts_to_payer() -> None:
    alice, bob, charlie = users()
    manager = ExpenseManager()
    manager.add_expense(alice, 300, [alice, bob, charlie], EqualSplitStrategy())

    alice_balances = manager.get_balances(alice)
    assert alice_balances.owed_by[bob] == 100
    assert alice_balances.owed_by[charlie] == 100
    assert not alice_balances.owes


def test_exact_split_rejects_bad_sum_and_accepts_valid_shares() -> None:
    alice, bob, charlie = users()
    manager = ExpenseManager()
    strategy = ExactSplitStrategy()

    with pytest.raises(InvalidSplitError):
        manager.add_expense(alice, 600, [alice, bob, charlie], strategy, {alice: 300, bob: 100, charlie: 100})

    manager.add_expense(alice, 600, [alice, bob, charlie], strategy, {alice: 300, bob: 100, charlie: 200})
    assert manager.get_balances(bob).owes[alice] == 100
    assert manager.get_balances(charlie).owes[alice] == 200


def test_percent_split_rejects_bad_sum_and_updates_balances() -> None:
    alice, bob, _ = users()
    manager = ExpenseManager()
    strategy = PercentSplitStrategy()

    with pytest.raises(InvalidSplitError):
        manager.add_expense(bob, 10000, [alice, bob], strategy, {alice: 30, bob: 30})

    manager.add_expense(bob, 10000, [alice, bob], strategy, {alice: 25, bob: 75})
    assert manager.get_balances(alice).owes[bob] == 2500


def test_balances_net_out_across_multiple_expenses() -> None:
    alice, bob, charlie = users()
    manager = ExpenseManager()
    manager.add_expense(alice, 300, [alice, bob, charlie], EqualSplitStrategy())
    manager.add_expense(bob, 150, [alice, bob], EqualSplitStrategy())

    assert manager.get_balances(bob).owes[alice] == 25
    assert manager.get_balances(charlie).owes[alice] == 100
    assert not manager.get_balances(alice).owes


def test_concurrent_equal_expenses_have_no_lost_updates() -> None:
    alice, bob, _ = users()
    manager = ExpenseManager()
    thread_count = 80
    start = threading.Event()

    def worker() -> None:
        start.wait()
        manager.add_expense(alice, 2, [alice, bob], EqualSplitStrategy())

    threads = [threading.Thread(target=worker) for _ in range(thread_count)]
    for thread in threads:
        thread.start()
    start.set()
    for thread in threads:
        thread.join(timeout=10)

    assert manager.get_balances(bob).owes[alice] == thread_count
    assert len(manager.expenses) == thread_count
