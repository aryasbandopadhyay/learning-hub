"""End-to-end tests for the Vending Machine MVP, including a last-unit concurrency race."""

from __future__ import annotations

import threading

import pytest

from vending.exceptions import InsufficientFundsError, OutOfStockError
from vending.machine import VendingMachine
from vending.models import InventoryItem, MachineStateName, Product


def make_machine() -> VendingMachine:
    return VendingMachine(
        [
            InventoryItem(Product("WATER", "Water", 25), 2),
            InventoryItem(Product("CHIPS", "Chips", 15), 1),
            InventoryItem(Product("CANDY", "Candy", 10), 0),
        ],
        {1, 5, 10, 25},
    )


def test_exact_change_dispenses_product_and_returns_to_idle():
    machine = make_machine()
    machine.insert_money(25)
    result = machine.select_product("WATER")
    assert result.product.code == "WATER"
    assert result.change == ()
    assert machine.stock_of("WATER") == 1
    assert machine.current_state() is MachineStateName.IDLE


def test_overpayment_returns_greedy_change():
    machine = make_machine()
    machine.insert_money(25)
    result = machine.select_product("CHIPS")
    assert result.change == (10,)
    assert machine.stock_of("CHIPS") == 0


def test_selecting_without_enough_money_is_rejected_and_stays_has_money():
    machine = make_machine()
    machine.insert_money(10)
    with pytest.raises(InsufficientFundsError):
        machine.select_product("WATER")
    assert machine.balance == 10
    assert machine.current_state() is MachineStateName.HAS_MONEY


def test_out_of_stock_product_is_rejected_and_money_is_preserved():
    machine = make_machine()
    machine.insert_money(10)
    with pytest.raises(OutOfStockError):
        machine.select_product("CANDY")
    assert machine.stock_of("CANDY") == 0
    assert machine.balance == 10
    assert machine.current_state() is MachineStateName.HAS_MONEY


def test_cancel_refunds_full_inserted_amount_and_returns_to_idle():
    machine = make_machine()
    machine.insert_money(25)
    machine.insert_money(10)
    refund = machine.cancel()
    assert refund.coins == (25, 10)
    assert refund.amount == 35
    assert machine.balance == 0
    assert machine.current_state() is MachineStateName.IDLE


def test_concurrent_buyers_cannot_oversell_last_unit():
    threads = 50
    machine = VendingMachine([InventoryItem(Product("WATER", "Water", 25), 1)], {1, 5, 10, 25})

    start = threading.Event()
    successes: list[str] = []
    successes_lock = threading.Lock()

    def worker() -> None:
        start.wait()
        try:
            result = machine.purchase("WATER", [25])
            with successes_lock:
                successes.append(result.product.code)
        except OutOfStockError:
            pass

    workers = [threading.Thread(target=worker) for _ in range(threads)]
    for worker_thread in workers:
        worker_thread.start()
    start.set()
    for worker_thread in workers:
        worker_thread.join(timeout=10)

    assert len(successes) == 1, "only one buyer can get the last unit"
    assert machine.stock_of("WATER") == 0
