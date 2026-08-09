# 15. Teemo Attacking

- **Difficulty:** Easy
- **Pattern:** interval merging
- **Asked at:** Riot Games, Amazon, Google

## Problem
Teemo attacks at each time in the sorted list `timeSeries`. Each attack poisons Ashe for `duration` seconds, covering the half-open interval `[time, time + duration)`. If Ashe is already poisoned, the timer is refreshed. Return the total number of seconds Ashe is poisoned.

**Input**
- `timeSeries`: a `list[int]`; attack start times.
- `duration`: a `int`; the poison duration.

**Output**
- A `int`. Return the total number of seconds Ashe is poisoned.

## Constraints
- `0 <= len(timeSeries) <= 10^4`, `0 <= timeSeries[i] <= 10^7`, `timeSeries` is strictly increasing, and `0 <= duration <= 10^7`.

## Examples
```text
Input: timeSeries = [1,4], duration = 2
Output: 4
Explanation: The poisoned intervals are [1,3) and [4,6), for 4 total seconds.
```

## Understanding & Intuition
Each attack creates a time interval, and overlapping intervals should be counted only once. Since attack times are already sorted, overlaps can be merged in one pass. The optimal solution observes that consecutive attacks contribute only the gap between them if the next attack happens before the poison expires.

## Approach 1 — Naive / Brute Force
**Idea:** Mark every poisoned integer second in a set.
```python
class Solution:
    def findPoisonedDuration(self, timeSeries: list[int], duration: int) -> int:
        poisoned = set()
        for start in timeSeries:
            for t in range(start, start + duration):
                poisoned.add(t)
        return len(poisoned)
```
- **Time:** O(nd) — **Space:** O(nd)

## Approach 2 — Better
**Idea:** Build and merge the poison intervals, then sum their merged lengths.
```python
class Solution:
    def findPoisonedDuration(self, timeSeries: list[int], duration: int) -> int:
        if not timeSeries or duration == 0:
            return 0
        intervals = [[t, t + duration] for t in timeSeries]
        total = 0
        cur_start, cur_end = intervals[0]
        for start, end in intervals[1:]:
            if start <= cur_end:
                cur_end = max(cur_end, end)
            else:
                total += cur_end - cur_start
                cur_start, cur_end = start, end
        total += cur_end - cur_start
        return total
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Add each attack's new contribution, which is `duration` unless the next attack refreshes poison sooner.
```python
class Solution:
    def findPoisonedDuration(self, timeSeries: list[int], duration: int) -> int:
        if not timeSeries or duration == 0:
            return 0
        total = 0
        for i in range(len(timeSeries) - 1):
            total += min(duration, timeSeries[i + 1] - timeSeries[i])
        total += duration
        return total
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nd) | O(nd) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Poison intervals are half-open, so `[1,3)` has length 2.
- Overlapping attacks refresh the poison timer rather than adding full duration.
- Empty `timeSeries` or zero `duration` returns 0.

## Related
- Merge Intervals
- Insert Interval
