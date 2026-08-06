# 13. Insert Delete GetRandom O(1)

- **Difficulty:** Medium
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Uber

## Problem
Implement `RandomizedSet` supporting `insert`, `remove`, and `getRandom` in average O(1). Random must be uniform among stored values.

## Examples
```text
Input: ["RandomizedSet","insert","remove","insert","getRandom","remove","insert","getRandom"] with [[],[1],[2],[2],[],[1],[2],[]]
Output: [null,true,false,true,2,true,false,2]
Explanation: The data structure maintains values with constant-time operations.
```

## Understanding & Intuition
A list gives random indexing, and a dictionary gives positions. Deletion swaps the removed value with the last element before popping.

## Approach 1 — Naive / Brute Force
**Idea:** Use a list and linear membership/removal.
```python
import random

class RandomizedSet:
    def __init__(self):
        self.values = []

    def insert(self, val: int) -> bool:
        if val in self.values:
            return False
        self.values.append(val)
        return True

    def remove(self, val: int) -> bool:
        if val not in self.values:
            return False
        self.values.remove(val)
        return True

    def getRandom(self) -> int:
        return random.choice(self.values)
```
- **Time:** O(n) insert/remove, O(1) random — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a set, converting to a tuple for random.
```python
import random

class RandomizedSet:
    def __init__(self):
        self.values = set()

    def insert(self, val: int) -> bool:
        if val in self.values:
            return False
        self.values.add(val)
        return True

    def remove(self, val: int) -> bool:
        if val not in self.values:
            return False
        self.values.remove(val)
        return True

    def getRandom(self) -> int:
        return random.choice(tuple(self.values))
```
- **Time:** O(1) insert/remove, O(n) random — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use list plus value-to-index map.
```python
import random

class RandomizedSet:
    def __init__(self):
        self.values = []
        self.index = {}

    def insert(self, val: int) -> bool:
        if val in self.index:
            return False
        self.index[val] = len(self.values)
        self.values.append(val)
        return True

    def remove(self, val: int) -> bool:
        if val not in self.index:
            return False
        i = self.index[val]
        last = self.values[-1]
        self.values[i] = last
        self.index[last] = i
        self.values.pop()
        del self.index[val]
        return True

    def getRandom(self) -> int:
        return random.choice(self.values)
```
- **Time:** O(1) average — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) insert/remove, O(1) random | O(n) |
| Better | O(1) insert/remove, O(n) random | O(n) |
| Optimal | O(1) average | O(n) |

## Edge Cases & Pitfalls
- Update moved value index on remove.
- Removing the last element works.
- getRandom is called only when non-empty.

## Related
- Insert Delete GetRandom O(1) - Duplicates allowed
- LRU Cache
