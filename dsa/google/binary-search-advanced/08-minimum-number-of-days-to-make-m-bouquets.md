# 08. Minimum Number of Days to Make m Bouquets

- **Difficulty:** Medium
- **Pattern:** Advanced Binary Search
- **Asked at:** Google

## Problem
You are given `bloomDay`, where `bloomDay[i]` is the day flower `i` blooms. You need `m` bouquets, each made from exactly `k` adjacent bloomed flowers.

Return the earliest day when the bouquets can be made. Each flower can be used at most once. If there are not enough flowers, return `-1`.

**Input**
- `bloomDay`: a list of bloom days.
- `m`: the number of bouquets required.
- `k`: the number of adjacent flowers per bouquet.

**Output**
- The minimum feasible day, or `-1` if the bouquets cannot be made.

## Constraints
- `1 <= bloomDay.length <= 10^5`
- `1 <= bloomDay[i] <= 10^9`
- `1 <= m <= 10^6`
- `1 <= k <= bloomDay.length`

## Examples
```text
Input: bloomDay = [1,10,3,10,2], m = 3, k = 1
Output: 3
Explanation: By day `3`, flowers at positions `0`, `2`, and `4` have bloomed. Since `k = 1`, they form three bouquets; before day `3`, only two are available.
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
