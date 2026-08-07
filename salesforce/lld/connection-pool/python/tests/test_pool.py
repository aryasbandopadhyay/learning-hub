"""End-to-end tests for the Connection Pool MVP, including concurrent borrow/release."""

from __future__ import annotations

import threading
import time

import pytest

from pool.connection import Connection
from pool.connection_pool import ConnectionPool, InvalidResourceError, PoolTimeoutError


def test_borrow_up_to_capacity_then_next_borrow_times_out() -> None:
    pool = ConnectionPool(2)

    first = pool.borrow(0.05)
    second = pool.borrow(0.05)

    assert pool.available == 0
    with pytest.raises(PoolTimeoutError):
        pool.borrow(0.025)

    pool.release(first)
    pool.release(second)


def test_release_makes_connection_available_again() -> None:
    pool = ConnectionPool(1)
    only = pool.borrow(0.05)
    assert pool.available == 0

    pool.release(only)

    assert pool.available == 1
    borrowed_again = pool.borrow(0.05)
    assert borrowed_again.id == only.id
    pool.release(borrowed_again)


def test_double_release_is_rejected() -> None:
    pool = ConnectionPool(1)
    only = pool.borrow(0.05)

    pool.release(only)

    with pytest.raises(InvalidResourceError):
        pool.release(only)
    assert pool.available == 1


def test_foreign_release_is_rejected() -> None:
    pool = ConnectionPool(1)
    with pytest.raises(InvalidResourceError):
        pool.release(Connection("foreign"))
    assert pool.available == 1


def test_concurrent_borrow_release_never_over_allocates_or_double_hands_out() -> None:
    capacity = 5
    threads = 50
    created = 0
    created_lock = threading.Lock()

    def factory(connection_id: str) -> Connection:
        nonlocal created
        with created_lock:
            created += 1
        return Connection(connection_id)

    pool = ConnectionPool(capacity, factory=factory)
    start = threading.Event()
    active_ids: set[str] = set()
    active_lock = threading.Lock()
    borrowed_ids: list[str] = []
    errors: list[BaseException] = []
    max_active = 0

    def worker() -> None:
        nonlocal max_active
        try:
            start.wait()
            connection = pool.borrow(5)
            with active_lock:
                if connection.id in active_ids:
                    raise AssertionError(f"duplicate active connection {connection.id}")
                active_ids.add(connection.id)
                borrowed_ids.append(connection.id)
                max_active = max(max_active, len(active_ids))
            time.sleep(0.01)
            with active_lock:
                active_ids.remove(connection.id)
            pool.release(connection)
        except BaseException as exc:  # keep worker failures visible to the main test thread
            errors.append(exc)

    workers = [threading.Thread(target=worker) for _ in range(threads)]
    for worker_thread in workers:
        worker_thread.start()
    start.set()
    for worker_thread in workers:
        worker_thread.join(timeout=10)

    assert not errors
    assert len(borrowed_ids) == threads, "every thread eventually borrows once"
    assert created == capacity, "factory called exactly capacity times"
    assert max_active <= capacity, "active resources never exceed capacity"
    assert pool.available == capacity
    assert pool.borrowed == 0
