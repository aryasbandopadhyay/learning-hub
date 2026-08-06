# 11. Capacity To Ship Packages Within D Days

- **Difficulty:** Medium
- **Pattern:** Binary Search
- **Asked at:** Amazon, Google, Bloomberg, Microsoft

## Problem
Given package weights in order and an integer `days`, return the least ship capacity needed to ship all packages within `days`. Packages must be shipped in the given order. Constraints: `1 <= days <= weights.length <= 5 * 10^4`, `1 <= weights[i] <= 500`.

## Examples
```text
Input: weights = [1,2,3,4,5,6,7,8,9,10], days = 5
Output: 15
Explanation: Capacity 15 can ship as [1,2,3,4,5], [6,7], [8], [9], [10].
```

## Understanding & Intuition
This is binary search on the answer. The predicate "capacity can ship within `days`" is monotonic: if one capacity works, any larger capacity also works. Lower bound is the heaviest package, upper bound is the sum of all weights.

## Approach 1 — Naive / Brute Force
**Idea:** Try every capacity from heaviest package to total weight.
```python
from typing import List

class Solution:
    def shipWithinDays(self, weights: List[int], days: int) -> int:
        def needed(capacity: int) -> int:
            used_days, load = 1, 0
            for weight in weights:
                if load + weight > capacity:
                    used_days += 1
                    load = 0
                load += weight
            return used_days

        for capacity in range(max(weights), sum(weights) + 1):
            if needed(capacity) <= days:
                return capacity
        return sum(weights)
```
- **Time:** O(nS) where `S = sum(weights)` — **Space:** O(1)

## Approach 2 — Better
**Idea:** Start from the average-load lower bound, then scan upward.
```python
from typing import List

class Solution:
    def shipWithinDays(self, weights: List[int], days: int) -> int:
        def can_ship(capacity: int) -> bool:
            used_days, load = 1, 0
            for weight in weights:
                if load + weight > capacity:
                    used_days += 1
                    load = 0
                load += weight
            return used_days <= days

        start = max(max(weights), (sum(weights) + days - 1) // days)
        for capacity in range(start, sum(weights) + 1):
            if can_ship(capacity):
                return capacity
        return sum(weights)
```
- **Time:** O(n(S - avg)) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Binary search the first feasible capacity.
```python
from typing import List

class Solution:
    def shipWithinDays(self, weights: List[int], days: int) -> int:
        def can_ship(capacity: int) -> bool:
            used_days, load = 1, 0
            for weight in weights:
                if load + weight > capacity:
                    used_days += 1
                    load = 0
                load += weight
            return used_days <= days

        left, right = max(weights), sum(weights)
        while left < right:
            mid = (left + right) // 2
            if can_ship(mid):
                right = mid
            else:
                left = mid + 1
        return left
```
- **Time:** O(n log S) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nS) | O(1) |
| Better | O(n(S - avg)) | O(1) |
| Optimal | O(n log S) | O(1) |

## Edge Cases & Pitfalls
- Capacity must be at least `max(weights)`.
- Do not reorder packages.
- Start `used_days` at 1 because the first day exists before loading begins.

## Related
- Koko Eating Bananas
- Split Array Largest Sum

