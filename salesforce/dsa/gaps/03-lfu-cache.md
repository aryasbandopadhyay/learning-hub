# 03. LFU Cache

- **Difficulty:** Hard
- **Pattern:** Design / Hash Map
- **Asked at:** Salesforce, Amazon, Google

## Problem
Design an LFU cache with `get` and `put`. Evict the least frequently used key, breaking frequency ties by least recent use.

## Examples
```text
Input: LFUCache(2), put(1,1), put(2,2), get(1), put(3,3), get(2), get(3)
Output: [null,null,null,1,null,-1,3]
Explanation: Key 2 is evicted because key 1 has higher frequency.
```

## Understanding & Intuition
Each access increases frequency. O(1) eviction needs direct key lookup plus frequency buckets that preserve recency within each frequency.

## Approach 1 — Naive / Brute Force
**Idea:** Store value, frequency, and timestamp; scan to evict.
```python
class LFUCache:
    def __init__(self, capacity: int):
        self.capacity = capacity; self.time = 0; self.data = {}

    def get(self, key: int) -> int:
        if key not in self.data: return -1
        value, freq, _ = self.data[key]; self.time += 1
        self.data[key] = (value, freq + 1, self.time)
        return value

    def put(self, key: int, value: int) -> None:
        if self.capacity == 0: return
        self.time += 1
        if key in self.data:
            _, freq, _ = self.data[key]
            self.data[key] = (value, freq + 1, self.time); return
        if len(self.data) == self.capacity:
            victim = min(self.data, key=lambda k: (self.data[k][1], self.data[k][2]))
            del self.data[victim]
        self.data[key] = (value, 1, self.time)
```
- **Time:** O(n) put, O(1) get — **Space:** O(n)

## Approach 2 — Better
**Idea:** Track frequency buckets but use simple lists, so removal from an old bucket may scan.
```python
class LFUCache:
    def __init__(self, capacity: int):
        self.capacity = capacity; self.min_freq = 0; self.info = {}; self.buckets = {}

    def _touch(self, key: int) -> None:
        value, freq = self.info[key]
        self.buckets[freq].remove(key)
        if not self.buckets[freq] and self.min_freq == freq: self.min_freq += 1
        self.info[key] = (value, freq + 1)
        self.buckets.setdefault(freq + 1, []).append(key)

    def get(self, key: int) -> int:
        if key not in self.info: return -1
        self._touch(key); return self.info[key][0]

    def put(self, key: int, value: int) -> None:
        if self.capacity == 0: return
        if key in self.info:
            self.info[key] = (value, self.info[key][1]); self._touch(key); return
        if len(self.info) == self.capacity:
            victim = self.buckets[self.min_freq].pop(0); del self.info[victim]
        self.info[key] = (value, 1); self.buckets.setdefault(1, []).append(key); self.min_freq = 1
```
- **Time:** O(n) worst-case — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use dicts plus ordered frequency buckets; `OrderedDict` is a doubly linked list-backed bucket.
```python
from collections import OrderedDict, defaultdict

class LFUCache:
    def __init__(self, capacity: int):
        self.capacity = capacity; self.min_freq = 0
        self.info = {}; self.freqs = defaultdict(OrderedDict)

    def _touch(self, key: int) -> None:
        value, freq = self.info[key]
        del self.freqs[freq][key]
        if not self.freqs[freq] and self.min_freq == freq:
            self.min_freq += 1
        self.info[key] = (value, freq + 1)
        self.freqs[freq + 1][key] = None

    def get(self, key: int) -> int:
        if key not in self.info: return -1
        self._touch(key); return self.info[key][0]

    def put(self, key: int, value: int) -> None:
        if self.capacity == 0: return
        if key in self.info:
            self.info[key] = (value, self.info[key][1]); self._touch(key); return
        if len(self.info) == self.capacity:
            old, _ = self.freqs[self.min_freq].popitem(last=False); del self.info[old]
        self.info[key] = (value, 1); self.freqs[1][key] = None; self.min_freq = 1
```
- **Time:** O(1) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) put, O(1) get | O(n) |
| Better | O(n) worst-case | O(n) |
| Optimal | O(1) | O(n) |

## Edge Cases & Pitfalls
- Capacity zero ignores all writes.
- Updating an existing key increases its frequency.
- Evict LRU among the minimum-frequency keys.

## Related
- LRU Cache
- Maximum Frequency Stack
