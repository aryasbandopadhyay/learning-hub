# 08. Minimum Number of Days to Make m Bouquets

- **Difficulty:** Medium
- **Pattern:** Advanced Binary Search
- **Asked at:** Google

## Problem
Given `bloomDay`, an integer `m`, and an integer `k`, return the minimum day needed to make `m` bouquets. Each bouquet needs `k` adjacent flowers that have bloomed by that day, and each flower can be used once. Return `-1` if impossible.

Constraints: `1 <= len(bloomDay) <= 10^5`, `1 <= bloomDay[i] <= 10^9`, `1 <= m, k <= 10^6`.

## Examples
```text
Input: bloomDay = [1,10,3,10,2], m = 3, k = 1
Output: 3
Explanation: By day 3, flowers at days 1, 3, and 2 have bloomed, making three single-flower bouquets.
```

## Understanding & Intuition
For a chosen day, greedily counting adjacent bloomed groups tells whether enough bouquets can be made. Feasibility only improves as the day increases, so the minimum feasible day can be binary searched.

## Approach 1 — Naive / Brute Force
**Idea:** Try every distinct bloom day in increasing order and simulate bouquet creation.
```python
class Solution:
    def minDays(self, bloomDay: list[int], m: int, k: int) -> int:
        if m * k > len(bloomDay):
            return -1
        for day in sorted(set(bloomDay)):
            bouquets = 0
            run = 0
            for b in bloomDay:
                if b <= day:
                    run += 1
                    if run == k:
                        bouquets += 1
                        run = 0
                else:
                    run = 0
            if bouquets >= m:
                return day
        return -1
```
- **Time:** O(u log u + un) — **Space:** O(u), where `u` is the number of unique days

## Approach 2 — Better
**Idea:** Binary search between the smallest and largest bloom day.
```python
class Solution:
    def minDays(self, bloomDay: list[int], m: int, k: int) -> int:
        if m * k > len(bloomDay):
            return -1
        lo, hi = min(bloomDay), max(bloomDay)
        while lo < hi:
            mid = (lo + hi) // 2
            bouquets = 0
            run = 0
            for b in bloomDay:
                if b <= mid:
                    run += 1
                    if run == k:
                        bouquets += 1
                        run = 0
                else:
                    run = 0
            if bouquets >= m:
                hi = mid
            else:
                lo = mid + 1
        return lo
```
- **Time:** O(n log R) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Binary search with early success once `m` bouquets have been counted.
```python
class Solution:
    def minDays(self, bloomDay: list[int], m: int, k: int) -> int:
        if m * k > len(bloomDay):
            return -1
        lo, hi = min(bloomDay), max(bloomDay)
        while lo < hi:
            mid = (lo + hi) // 2
            bouquets = 0
            run = 0
            for b in bloomDay:
                if b <= mid:
                    run += 1
                    if run == k:
                        bouquets += 1
                        if bouquets == m:
                            break
                        run = 0
                else:
                    run = 0
            if bouquets >= m:
                hi = mid
            else:
                lo = mid + 1
        return lo
```
- **Time:** O(n log R) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(u log u + un) | O(u) |
| Better | O(n log R) | O(1) |
| Optimal | O(n log R) | O(1) |

## Edge Cases & Pitfalls
- Check impossibility before searching.
- Adjacent runs reset when a flower has not bloomed.
- After using `k` flowers, reset the run to avoid reusing flowers.

## Related
- Split Array Largest Sum
- Smallest Divisor Given a Threshold
