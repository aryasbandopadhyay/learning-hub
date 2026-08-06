"""End-to-end tests for LRU/LFU cache behavior and concurrency."""

from __future__ import annotations

import threading

from cache import Cache, LfuEvictionPolicy, LruEvictionPolicy


def test_lru_evicts_least_recently_used_key() -> None:
    cache: Cache[str, int] = Cache(2, LruEvictionPolicy())
    cache.put("a", 1)
    cache.put("b", 2)
    assert cache.get("a") == 1

    cache.put("c", 3)

    assert cache.contains_key("a")
    assert not cache.contains_key("b")
    assert cache.contains_key("c")


def test_lru_update_refreshes_recency() -> None:
    cache: Cache[str, int] = Cache(2, LruEvictionPolicy())
    cache.put("a", 1)
    cache.put("b", 2)
    cache.put("a", 10)

    cache.put("c", 3)

    assert cache.get("a") == 10
    assert not cache.contains_key("b")
    assert cache.contains_key("c")


def test_lfu_evicts_lowest_frequency_key() -> None:
    cache: Cache[str, int] = Cache(2, LfuEvictionPolicy())
    cache.put("a", 1)
    cache.put("b", 2)
    cache.get("a")
    cache.get("a")
    cache.get("b")

    cache.put("c", 3)

    assert cache.contains_key("a")
    assert not cache.contains_key("b")
    assert cache.contains_key("c")


def test_lfu_breaks_frequency_ties_by_lru() -> None:
    cache: Cache[str, int] = Cache(2, LfuEvictionPolicy())
    cache.put("a", 1)
    cache.put("b", 2)
    cache.get("a")  # after get(b), both have freq=2 but a is older in that bucket
    cache.get("b")

    cache.put("c", 3)

    assert not cache.contains_key("a")
    assert cache.contains_key("b")
    assert cache.contains_key("c")


def test_missing_get_returns_none_and_size_never_exceeds_capacity() -> None:
    cache: Cache[str, int] = Cache(2, LruEvictionPolicy())
    assert cache.get("missing") is None
    cache.put("a", 1)
    cache.put("b", 2)
    cache.put("c", 3)
    assert cache.size() == 2


def test_concurrent_hammer_never_exceeds_capacity_or_crashes() -> None:
    capacity = 5
    threads = 32
    operations = 1_000
    cache: Cache[int, int] = Cache(capacity, LruEvictionPolicy())
    start = threading.Event()
    errors: list[BaseException | str] = []
    errors_lock = threading.Lock()

    def worker(thread_id: int) -> None:
        start.wait()
        try:
            for i in range(operations):
                key = (thread_id + i) % 20
                cache.put(key, i)
                cache.get((key + 1) % 20)
                if cache.size() > capacity:
                    with errors_lock:
                        errors.append("capacity exceeded")
        except BaseException as exc:  # noqa: BLE001 - test records any worker crash
            with errors_lock:
                errors.append(exc)

    workers = [threading.Thread(target=worker, args=(i,)) for i in range(threads)]
    for worker_thread in workers:
        worker_thread.start()
    start.set()
    for worker_thread in workers:
        worker_thread.join(timeout=10)

    assert errors == []
    assert cache.size() <= capacity


def test_concurrent_capacity_one_puts_leave_exactly_one_entry() -> None:
    cache: Cache[int, int] = Cache(1, LfuEvictionPolicy())
    threads = 40
    start = threading.Event()
    errors: list[BaseException] = []
    errors_lock = threading.Lock()

    def worker(key: int) -> None:
        start.wait()
        try:
            cache.put(key, key)
        except BaseException as exc:  # noqa: BLE001 - test records any worker crash
            with errors_lock:
                errors.append(exc)

    workers = [threading.Thread(target=worker, args=(i,)) for i in range(threads)]
    for worker_thread in workers:
        worker_thread.start()
    start.set()
    for worker_thread in workers:
        worker_thread.join(timeout=10)

    assert errors == []
    assert cache.size() == 1
