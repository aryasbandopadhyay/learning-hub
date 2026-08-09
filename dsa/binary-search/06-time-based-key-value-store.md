# 06. Time Based Key-Value Store

- **Difficulty:** Medium
- **Pattern:** Binary Search
- **Asked at:** Google, Amazon, Meta, Uber

## Problem
Design a time-based key-value store. It supports storing multiple values for the same key at
different timestamps and retrieving the value associated with the greatest timestamp not exceeding a
query timestamp.

Implement the `TimeMap` class with:
- `TimeMap()`: initializes the object.
- `set(key, value, timestamp)`: stores `value` for `key` at `timestamp`.
- `get(key, timestamp)`: returns the value set for `key` with the largest timestamp `<= timestamp`,
  or the empty string if no such timestamp exists.

**Input**
- A sequence of constructor, `set`, and `get` operations with their arguments.

**Output**
- For each operation in order, output `null` for `TimeMap()` and `set`, and the returned string for
  each `get`. **This judge compares exactly**, so outputs must follow the operation order.

## Constraints
- 1 <= key.length, value.length <= 100
- `key` and `value` consist of lowercase English letters and digits.
- 1 <= timestamp <= 10^7
- Timestamps passed to `set` for the same key are strictly increasing.
- At most `2 * 10^5` calls are made to `set` and `get`.

## Examples
```text
Input: set("foo","bar",1), get("foo",1), get("foo",3)
Output: null, "bar", "bar"
Explanation: After setting `foo` to `bar` at timestamp `1`, both queries at timestamps `1` and `3` return the latest value whose timestamp is at most the query time: `bar`.
```

## Understanding & Intuition
Each key stores versions in increasing timestamp order. A `get` asks for the rightmost timestamp not greater than the query. Binary search is ideal for finding that floor timestamp quickly.

## Approach 1 — Naive / Brute Force
**Idea:** Store all pairs and scan the key's history on each `get`.
```python
from collections import defaultdict

class TimeMap:
    def __init__(self):
        self.store = defaultdict(list)

    def set(self, key: str, value: str, timestamp: int) -> None:
        self.store[key].append((timestamp, value))

    def get(self, key: str, timestamp: int) -> str:
        answer = ""
        for time, value in self.store[key]:
            if time <= timestamp:
                answer = value
            else:
                break
        return answer
```
- **Time:** O(k) per `get` — **Space:** O(n)

## Approach 2 — Better
**Idea:** Keep separate timestamp and value arrays, then use `bisect_right`.
```python
from collections import defaultdict
from bisect import bisect_right

class TimeMap:
    def __init__(self):
        self.times = defaultdict(list)
        self.values = defaultdict(list)

    def set(self, key: str, value: str, timestamp: int) -> None:
        self.times[key].append(timestamp)
        self.values[key].append(value)

    def get(self, key: str, timestamp: int) -> str:
        index = bisect_right(self.times[key], timestamp) - 1
        if index < 0:
            return ""
        return self.values[key][index]
```
- **Time:** O(log k) per `get` — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Binary search manually on the per-key list of pairs.
```python
from collections import defaultdict

class TimeMap:
    def __init__(self):
        self.store = defaultdict(list)

    def set(self, key: str, value: str, timestamp: int) -> None:
        self.store[key].append((timestamp, value))

    def get(self, key: str, timestamp: int) -> str:
        entries = self.store[key]
        left, right = 0, len(entries) - 1
        answer = ""
        while left <= right:
            mid = (left + right) // 2
            time, value = entries[mid]
            if time <= timestamp:
                answer = value
                left = mid + 1
            else:
                right = mid - 1
        return answer
```
- **Time:** O(log k) per `get` — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(k) get | O(n) |
| Better | O(log k) get | O(n) |
| Optimal | O(log k) get | O(n) |

## Edge Cases & Pitfalls
- Return `""` when the key is missing or all timestamps are too large.
- `bisect_right` finds the insertion point after equal timestamps.
- This relies on per-key timestamps arriving in increasing order.

## Related
- Snapshot Array
- Online Stock Span

