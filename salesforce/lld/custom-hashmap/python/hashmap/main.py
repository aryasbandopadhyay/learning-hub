"""Runnable deterministic demo for the custom hash map.

Run:  python -m hashmap.main   (from the python/ directory)
"""

from __future__ import annotations

from .hashmap import MyHashMap


def _bool_text(value: bool) -> str:
    return str(value).lower()


def main() -> None:
    map_: MyHashMap[int, str] = MyHashMap(initial_capacity=4)

    print(f"Initial capacity: {map_.capacity}")
    map_.put(1, "Alice")
    print(f"After put(1, Alice): {map_.get(1)}")
    map_.put(2, "Bob")
    print(f"After put(2, Bob): {map_.get(2)}")
    map_.put(1, "Alicia")
    print(f"After overwrite put(1, Alicia): {map_.get(1)}")
    print(f"Contains key 2: {_bool_text(map_.contains_key(2))}")
    print(f"Remove key 2: {map_.remove(2)}")
    print(f"Contains key 2: {_bool_text(map_.contains_key(2))}")
    print(f"Size after remove: {map_.size}")

    for i in range(10):
        map_.put(100 + i, f"V{i}")
    all_retrievable = all(map_.get(100 + i) == f"V{i}" for i in range(10))
    print(f"Capacity after resizing demo: {map_.capacity}")
    print(f"All resize keys retrievable: {_bool_text(all_retrievable)}")


if __name__ == "__main__":
    main()
