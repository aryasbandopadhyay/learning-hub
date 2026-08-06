"""End-to-end tests for the ATM Machine MVP, including the account concurrency race."""

from __future__ import annotations

import threading

import pytest

from atm.atm import AtmMachine, CashDispenser
from atm.exceptions import (
    AuthenticationError,
    CashDispenseError,
    InsufficientFundsError,
    InvalidOperationError,
)
from atm.models import Account, AtmStatus, Card


def authenticated_atm(account: Account) -> AtmMachine:
    atm = AtmMachine(CashDispenser({200_000: 5, 50_000: 10, 20_000: 10, 10_000: 20}))
    atm.insert_card(Card("CARD-1", "1234", account))
    atm.enter_pin("1234")
    return atm


def test_insert_card_and_correct_pin_authenticates() -> None:
    account = Account("A1", 100_000)
    atm = AtmMachine(CashDispenser.demo_dispenser())
    atm.insert_card(Card("C1", "1234", account))
    assert atm.status is AtmStatus.CARD_INSERTED
    atm.enter_pin("1234")
    assert atm.status is AtmStatus.AUTHENTICATED


def test_wrong_pin_beyond_limit_ejects_card() -> None:
    account = Account("A1", 100_000)
    atm = AtmMachine(CashDispenser.demo_dispenser())
    atm.insert_card(Card("C1", "1234", account))
    with pytest.raises(AuthenticationError):
        atm.enter_pin("0000")
    with pytest.raises(AuthenticationError):
        atm.enter_pin("1111")
    with pytest.raises(AuthenticationError):
        atm.enter_pin("2222")
    assert atm.status is AtmStatus.IDLE


def test_withdraw_sufficient_funds_dispenses_notes_and_updates_inventory() -> None:
    account = Account("A1", 1_000_000)
    atm = authenticated_atm(account)
    result = atm.withdraw(300_000)
    assert account.balance_cents == 700_000
    assert dict(result.notes) == {200_000: 1, 50_000: 2}
    assert atm.cash_inventory[200_000] == 4
    assert atm.cash_inventory[50_000] == 8
    assert atm.status is AtmStatus.AUTHENTICATED


def test_withdraw_more_than_balance_is_rejected() -> None:
    account = Account("A1", 100_000)
    atm = authenticated_atm(account)
    with pytest.raises(InsufficientFundsError):
        atm.withdraw(200_000)
    assert account.balance_cents == 100_000


def test_non_dispensable_amount_is_rejected_before_debit() -> None:
    account = Account("A1", 1_000_000)
    atm = authenticated_atm(account)
    with pytest.raises(CashDispenseError):
        atm.withdraw(12_500)
    assert account.balance_cents == 1_000_000


def test_operation_before_authentication_is_rejected_by_state_guards() -> None:
    account = Account("A1", 1_000_000)
    idle = AtmMachine(CashDispenser.demo_dispenser())
    with pytest.raises(InvalidOperationError):
        idle.withdraw(10_000)

    card_inserted = AtmMachine(CashDispenser.demo_dispenser())
    card_inserted.insert_card(Card("C1", "1234", account))
    with pytest.raises(InvalidOperationError):
        card_inserted.withdraw(10_000)


def test_deposit_increases_balance() -> None:
    account = Account("A1", 100_000)
    atm = authenticated_atm(account)
    atm.deposit(50_000)
    assert atm.check_balance() == 150_000


def test_concurrent_withdrawals_never_overdraw_account() -> None:
    threads = 50
    start_balance = 500_000
    amount = 100_000
    shared = Account("A1", start_balance)
    shared_card = Card("C1", "1234", shared)
    start = threading.Event()
    successes = 0
    successes_lock = threading.Lock()

    def worker() -> None:
        nonlocal successes
        atm = AtmMachine(CashDispenser.demo_dispenser())
        atm.insert_card(shared_card)
        atm.enter_pin("1234")
        start.wait()
        try:
            atm.withdraw(amount)
            with successes_lock:
                successes += 1
        except InsufficientFundsError:
            pass

    workers = [threading.Thread(target=worker) for _ in range(threads)]
    for w in workers:
        w.start()
    start.set()
    for w in workers:
        w.join(timeout=10)

    assert shared.balance_cents == start_balance - successes * amount
    assert shared.balance_cents >= 0
    assert successes == 5
