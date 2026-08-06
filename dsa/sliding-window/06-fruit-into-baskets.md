# 06. Fruit Into Baskets

- **Difficulty:** Medium
- **Pattern:** Sliding Window
- **Asked at:** Amazon, Google, Apple, Microsoft

## Problem
Given an array `fruits` where `fruits[i]` is a tree's fruit type, return the maximum number of fruits you can collect from a contiguous subarray containing at most two distinct types. Constraints: `1 <= len(fruits) <= 10^5`, `0 <= fruits[i] < len(fruits)`.

## Examples
```text
Input: fruits = [1,2,1]
Output: 3
Explanation: The whole array has only two fruit types.
```

## Understanding & Intuition
The baskets allow at most two distinct fruit types, so the task is the longest subarray with at most two distinct values. When a third type enters the window, move the left boundary until only two remain. Frequency counts make this adjustment efficient.

## Approach 1 — Naive / Brute Force
**Idea:** Start at every tree and extend until a third type appears.
```python
from typing import List

class Solution:
    def totalFruit(self, fruits: List[int]) -> int:
        best = 0
        for left in range(len(fruits)):
            types = set()
            for right in range(left, len(fruits)):
                types.add(fruits[right])
                if len(types) > 2:
                    break
                best = max(best, right - left + 1)
        return best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Track the last index of each current type and drop the type with the oldest last occurrence.
```python
from typing import List

class Solution:
    def totalFruit(self, fruits: List[int]) -> int:
        last = {}
        left = best = 0
        for right, fruit in enumerate(fruits):
            last[fruit] = right
            if len(last) > 2:
                # The type with the smallest last index must be fully excluded.
                drop = min(last, key=last.get)
                left = last[drop] + 1
                del last[drop]
            best = max(best, right - left + 1)
        return best
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use the general at-most-k-distinct frequency window with `k = 2`.
```python
from typing import List

class Solution:
    def totalFruit(self, fruits: List[int]) -> int:
        counts = {}
        left = best = 0
        for right, fruit in enumerate(fruits):
            counts[fruit] = counts.get(fruit, 0) + 1
            while len(counts) > 2:
                counts[fruits[left]] -= 1
                if counts[fruits[left]] == 0:
                    del counts[fruits[left]]
                left += 1
            best = max(best, right - left + 1)
        return best
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Arrays with one or two types can be fully collected.
- Update the answer after restoring validity.
- The fruit type values are arbitrary integers, so use a dictionary.

## Related
- Longest Substring with At Most K Distinct Characters
- Max Consecutive Ones III

