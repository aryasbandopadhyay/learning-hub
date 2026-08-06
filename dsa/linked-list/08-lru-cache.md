# 08. LRU Cache

- **Difficulty:** Medium
- **Pattern:** Linked List
- **Asked at:** Amazon, Meta, Microsoft, Google

## Problem
Design an LRU Cache with `get(key)` and `put(key, value)` in average O(1) time. When capacity is exceeded, evict the least recently used key. Capacity is positive, and up to `2 * 10^5` operations may be called.

## Examples
```text
Input: ["LRUCache","put","put","get","put","get","put","get","get","get"], [[2],[1,1],[2,2],[1],[3,3],[2],[4,4],[1],[3],[4]]
Output: [null,null,null,1,null,-1,null,-1,3,4]
Explanation: Keys 2 and then 1 are evicted as they become least recently used.
```

## Understanding & Intuition
An LRU cache needs fast lookup by key and fast movement of recently used entries. A list alone can track recency but lookup is slow. A dictionary plus a doubly linked list gives O(1) access, removal, insertion, and eviction.

## Approach 1 — Naive / Brute Force
**Idea:** Store `(key, value)` pairs in a list ordered from least to most recent.
```python
class LRUCache:
    def __init__(self, capacity: int):
        self.capacity = capacity
        self.items = []

    def get(self, key: int) -> int:
        for i, (k, v) in enumerate(self.items):
            if k == key:
                self.items.pop(i)
                self.items.append((k, v))  # Mark as recently used.
                return v
        return -1

    def put(self, key: int, value: int) -> None:
        for i, (k, _) in enumerate(self.items):
            if k == key:
                self.items.pop(i)
                break
        self.items.append((key, value))
        if len(self.items) > self.capacity:
            self.items.pop(0)
```
- **Time:** O(n) per operation — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use `OrderedDict`, a standard-library ordered hash map designed for recency movement.
```python
from collections import OrderedDict

class LRUCache:
    def __init__(self, capacity: int):
        self.capacity = capacity
        self.cache = OrderedDict()

    def get(self, key: int) -> int:
        if key not in self.cache:
            return -1
        self.cache.move_to_end(key)
        return self.cache[key]

    def put(self, key: int, value: int) -> None:
        if key in self.cache:
            self.cache.move_to_end(key)
        self.cache[key] = value
        if len(self.cache) > self.capacity:
            self.cache.popitem(last=False)
```
- **Time:** O(1) average per operation — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Implement the interview-standard dictionary plus custom doubly linked list.
```python
class Node:
    def __init__(self, key=0, value=0):
        self.key = key
        self.value = value
        self.prev = None
        self.next = None

class LRUCache:
    def __init__(self, capacity: int):
        self.capacity = capacity
        self.cache = {}
        self.left = Node()   # Least-recent sentinel.
        self.right = Node()  # Most-recent sentinel.
        self.left.next = self.right
        self.right.prev = self.left

    def _remove(self, node):
        node.prev.next = node.next
        node.next.prev = node.prev

    def _insert_recent(self, node):
        prev = self.right.prev
        prev.next = node
        node.prev = prev
        node.next = self.right
        self.right.prev = node

    def get(self, key: int) -> int:
        if key not in self.cache:
            return -1
        node = self.cache[key]
        self._remove(node)
        self._insert_recent(node)
        return node.value

    def put(self, key: int, value: int) -> None:
        if key in self.cache:
            self._remove(self.cache[key])
        node = Node(key, value)
        self.cache[key] = node
        self._insert_recent(node)
        if len(self.cache) > self.capacity:
            lru = self.left.next
            self._remove(lru)
            del self.cache[lru.key]
```
- **Time:** O(1) average per operation — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) per operation | O(n) |
| Better | O(1) average per operation | O(n) |
| Optimal | O(1) average per operation | O(n) |

## Edge Cases & Pitfalls
- Updating an existing key must also mark it recent.
- Evict from the least-recent end, not the most-recent end.
- Sentinel nodes remove most null-pointer special cases.

## Related
- Design LFU Cache
- Copy List with Random Pointer

