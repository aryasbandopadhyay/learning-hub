"""Runnable demo: borrow, use, release, and print deterministic availability changes.

Run:  python -m pool.main   (from the python/ directory)
"""

from __future__ import annotations

from .connection_pool import ConnectionPool


def main() -> None:
    pool = ConnectionPool(2)

    print("Pool size:", pool.size)
    print("Available at start:", pool.available)

    first = pool.borrow(0.1)
    print("Borrowed", first.id)
    print("Available after first borrow:", pool.available)

    second = pool.borrow(0.1)
    print("Borrowed", second.id)
    print("Available after second borrow:", pool.available)

    pool.release(first)
    print("Released", first.id)
    print("Available after release:", pool.available)

    again = pool.borrow(0.1)
    print("Borrowed again", again.id)
    pool.release(second)
    pool.release(again)
    print("Available at end:", pool.available)


if __name__ == "__main__":
    main()
