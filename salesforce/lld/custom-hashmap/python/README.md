# Custom HashMap — LLD Machine Coding (Python)

A from-scratch generic hash map for the LeetCode 706 / machine-coding flavor. It demonstrates
hashing, bucket indexing, separate chaining, and dynamic resizing without storing entries in a
Python `dict`.

> A parallel Java implementation lives in `../java` with its own README. Both produce identical
> demo output. The class structure is intentionally 1:1 between the two languages.

---

## 1. Why this MVP?

A machine-coding interviewer looks for a clear API, collision handling, resizing, and tests that
prove correctness after rehashing. The MVP is the **smallest complete map** that still exercises all
of those:

**In scope**
- Generic `MyHashMap[K,V]`
- `put`, `get`, `remove`, `contains_key`, `size`
- Separate chaining with linked-list bucket nodes
- Dynamic resizing at load factor `0.75`
- Deterministic demo and pytest tests, including forced collisions and 2+ resizes

**Deliberately out of scope** (extension points): tree bins, iterator views, serialization,
concurrent access, and full `MutableMapping` compatibility. See *Extending* below.

---

## 2. UML

### Class diagram
```mermaid
classDiagram
    class MyHashMap~K,V~ {
      -list buckets
      -int _size
      -float _load_factor
      +put(key,value)
      +get(key)
      +remove(key)
      +contains_key(key) bool
      +size int
      +capacity int
      -_index_for(key, capacity) int
      -_resize(new_capacity)
    }
    class _Node~K,V~ {
      +K key
      +V value
      +_Node next
    }
    class main {
      +main()
    }
    MyHashMap *-- _Node
    main ..> MyHashMap
```

### Put sequence
```mermaid
sequenceDiagram
    participant C as Client
    participant M as MyHashMap
    participant B as Bucket list
    participant N as Node chain
    C->>M: put(key, value)
    M->>M: _index_for(key, capacity)
    M->>N: scan bucket chain for existing key
    alt key exists
        M->>N: overwrite value
        M-->>C: old value
    else new key
        M->>M: _ensure_capacity_for(size + 1)
        opt load factor exceeded
            M->>B: double buckets and rehash nodes
        end
        M->>B: insert new _Node at bucket head
        M-->>C: None
    end
```

---

## 3. Design decisions

| Decision | Reasoning |
| --- | --- |
| **List of bucket heads** | Gives O(1) average access while keeping storage explicit and interview-friendly. |
| **Separate chaining** | Colliding keys are stored in a linked list instead of overwriting each other. |
| **Power-of-two capacity** | Makes bucket calculation fast: `hash & (capacity - 1)`. |
| **Hash spreading** | `h ^ (h >> 16)` mixes high bits into low bits before masking. |
| **Resize before inserting new keys** | Keeps `size / capacity <= 0.75` after every new insertion. |
| **Overwrite does not resize** | Updating an existing key does not change load factor. |
| **None key support** | `None` hashes to bucket `0`, mirroring the Java null-key behavior. |

### Hashing model (the key part)
Capacity is always rounded to a power of two. Because of that, `index = spread_hash & (capacity - 1)`
is equivalent to modulo for bucket selection but fast and never negative. During resize, capacity
doubles, so every node is re-indexed and moved to the correct new bucket.

---

## 4. Code flow

```
main → MyHashMap(initial_capacity=4)
put → scan chain for key → maybe resize → prepend _Node into bucket
get → hash to bucket → scan linked list → value or None
remove → hash to bucket → unlink matching _Node → old value or None
_resize → double bucket list → rehash every existing _Node
```

Module layout:
```
hashmap/
├── __init__.py
├── hashmap.py   generic map, bucket nodes, chaining, resize, hash/index logic
└── main.py      runnable deterministic demo
tests/
└── test_hashmap.py
```

---

## 5. How to run

Prerequisites: Python 3.10+.

```powershell
cd python

# install the test runner if needed
python -m pip install pytest

# run the suite (7 tests incl. collisions and 2+ resizes)
python -m pytest -q

# run the demo
python -m hashmap.main
```

Expected demo output:
```
Initial capacity: 4
After put(1, Alice): Alice
After put(2, Bob): Bob
After overwrite put(1, Alicia): Alicia
Contains key 2: true
Remove key 2: Bob
Contains key 2: false
Size after remove: 1
Capacity after resizing demo: 16
All resize keys retrievable: true
```

---

## 6. Tests

`tests/test_hashmap.py` covers:
- `put` + `get`
- overwrite existing key without increasing size
- `remove`
- absent key behavior
- `contains_key` when the stored value is `None`
- forced hash collisions via same-`__hash__` keys
- dynamic resizing through 2+ doublings with all keys still retrievable

---

## 7. Extending (what a follow-up would add)
- **Concurrency**: a synchronized wrapper or striped locks per bucket for concurrent operations.
- **Iteration**: key/value/items iterators with modification tracking.
- **Tree bins**: convert very long chains to balanced trees under adversarial hashes.
- **Mapping API**: implement `collections.abc.MutableMapping` once the MVP behavior is stable.
