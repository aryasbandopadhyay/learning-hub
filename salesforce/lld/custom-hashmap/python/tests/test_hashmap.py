"""End-to-end tests for the custom HashMap MVP."""

from __future__ import annotations

from hashmap import MyHashMap


class BadHashKey:
    """Every instance hashes the same, forcing one long bucket chain."""

    def __init__(self, key: str) -> None:
        self.key = key

    def __hash__(self) -> int:
        return 42

    def __eq__(self, other: object) -> bool:
        return isinstance(other, BadHashKey) and self.key == other.key


def test_put_and_get_returns_stored_value() -> None:
    map_: MyHashMap[str, int] = MyHashMap()
    map_.put("one", 1)
    map_.put("two", 2)
    assert map_.get("one") == 1
    assert map_.get("two") == 2
    assert map_.size == 2


def test_overwrite_existing_key_does_not_increase_size() -> None:
    map_: MyHashMap[str, str] = MyHashMap()
    assert map_.put("name", "Alice") is None
    assert map_.put("name", "Alicia") == "Alice"
    assert map_.get("name") == "Alicia"
    assert map_.size == 1


def test_remove_deletes_key_and_returns_value() -> None:
    map_: MyHashMap[int, str] = MyHashMap()
    map_.put(7, "seven")
    assert map_.remove(7) == "seven"
    assert map_.get(7) is None
    assert not map_.contains_key(7)
    assert map_.size == 0


def test_absent_key_returns_none_and_remove_is_noop() -> None:
    map_: MyHashMap[str, str] = MyHashMap()
    map_.put("present", "value")
    assert map_.get("missing") is None
    assert map_.remove("missing") is None
    assert map_.size == 1


def test_contains_key_finds_present_keys_even_when_value_is_none() -> None:
    map_: MyHashMap[str, str | None] = MyHashMap()
    map_.put("nullable", None)
    assert map_.contains_key("nullable")
    assert map_.get("nullable") is None


def test_collisions_are_resolved_by_separate_chaining() -> None:
    map_: MyHashMap[BadHashKey, str] = MyHashMap(initial_capacity=2)
    a = BadHashKey("a")
    b = BadHashKey("b")
    c = BadHashKey("c")

    map_.put(a, "A")
    map_.put(b, "B")
    map_.put(c, "C")

    assert map_.get(a) == "A"
    assert map_.get(b) == "B"
    assert map_.get(c) == "C"
    assert map_.remove(b) == "B"
    assert map_.get(a) == "A"
    assert map_.get(c) == "C"


def test_resize_twice_keeps_every_key_retrievable() -> None:
    map_: MyHashMap[int, str] = MyHashMap(initial_capacity=4)
    start_capacity = map_.capacity

    for i in range(20):
        map_.put(i, f"value-{i}")

    assert map_.capacity >= start_capacity * 4
    assert map_.size == 20
    for i in range(20):
        assert map_.get(i) == f"value-{i}"
