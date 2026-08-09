# 07. Map Sum Pairs

- **Difficulty:** Medium
- **Pattern:** Tries
- **Asked at:** Google, Amazon, Microsoft, Uber

## Problem
Design `MapSum`. `insert(key, val)` sets a string key to a value, replacing any previous value for that key. `sum(prefix)` returns the total value of all stored keys beginning with `prefix`.

**Input**
- A sequence of `MapSum()`, `insert(key, val)`, and `sum(prefix)` operations.

**Output**
- Operation results in order: constructor and `insert` return `null`, and `sum` returns integers. This judge compares exactly one result per operation.

## Constraints
- `1 <= key.length, prefix.length <= 50`
- `key` and `prefix` contain lowercase English letters.
- `1 <= val <= 1000`
- At most `50` operations are performed.

## Examples
```text
Input: ["MapSum","insert","sum","insert","sum"]
       [[],["apple",3],["ap"],["app",2],["ap"]]
Output: [null,null,3,null,5]
Explanation: After `apple = 3`, prefix `ap` sums to `3`; adding `app = 2` makes the same prefix sum `5`.
```

## Understanding & Intuition
The tricky part is replacement: inserting the same key again should change previous prefix sums by the difference, not add twice. A trie can store cumulative prefix sums at each node. Then `sum(prefix)` becomes a simple prefix walk.

## Approach 1 — Naive / Brute Force
**Idea:** Store key-values in a dictionary and scan all keys for each prefix sum.
```python
class MapSum:
    def __init__(self):
        self.values = {}

    def insert(self, key: str, val: int) -> None:
        self.values[key] = val

    def sum(self, prefix: str) -> int:
        total = 0
        for key, val in self.values.items():
            if key.startswith(prefix):
                total += val
        return total
```
- **Time:** O(1) insert, O(NL) sum — **Space:** O(NL)

## Approach 2 — Better
**Idea:** Maintain a dictionary of every prefix sum and update all prefixes by the replacement delta.
```python
from collections import defaultdict


class MapSum:
    def __init__(self):
        self.values = {}
        self.prefix_sum = defaultdict(int)

    def insert(self, key: str, val: int) -> None:
        delta = val - self.values.get(key, 0)
        self.values[key] = val
        # Apply only the change caused by this insert.
        for i in range(1, len(key) + 1):
            self.prefix_sum[key[:i]] += delta

    def sum(self, prefix: str) -> int:
        return self.prefix_sum[prefix]
```
- **Time:** O(L²) insert due to slicing, O(1) sum — **Space:** O(total prefix characters)

## Approach 3 — Optimal
**Idea:** Store cumulative sums inside trie nodes and update each traversed node by the delta.
```python
class TrieNode:
    def __init__(self):
        self.children = {}
        self.score = 0


class MapSum:
    def __init__(self):
        self.root = TrieNode()
        self.values = {}

    def insert(self, key: str, val: int) -> None:
        delta = val - self.values.get(key, 0)
        self.values[key] = val
        node = self.root
        for ch in key:
            if ch not in node.children:
                node.children[ch] = TrieNode()
            node = node.children[ch]
            node.score += delta

    def sum(self, prefix: str) -> int:
        node = self.root
        for ch in prefix:
            if ch not in node.children:
                return 0
            node = node.children[ch]
        return node.score
```
- **Time:** O(L) per operation — **Space:** O(total trie nodes)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(1) insert, O(NL) sum | O(NL) |
| Better | O(L²) insert, O(1) sum | O(total prefix characters) |
| Optimal | O(L) per operation | O(total trie nodes) |

## Edge Cases & Pitfalls
- Re-inserting an existing key replaces the value.
- Prefixes with no matching key return `0`.
- Do not forget to update all prefix sums by delta.

## Related
- Implement Trie
- Prefix sum maps
