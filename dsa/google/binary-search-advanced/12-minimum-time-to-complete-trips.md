# 12. Minimum Time to Complete Trips

- **Difficulty:** Medium
- **Pattern:** Advanced Binary Search
- **Asked at:** Google

## Problem
You are given `time`, where `time[i]` is how long bus `i` needs for one trip, and an integer `totalTrips`.

All buses can run trips independently and repeatedly. Return the minimum time by which the buses complete at least `totalTrips` trips in total.

**Input**
- `time`: a list of positive trip durations.
- `totalTrips`: the required number of completed trips.

**Output**
- The earliest integer time when at least `totalTrips` trips are complete.

## Constraints
- `1 <= time.length <= 10^5`
- `1 <= time[i], totalTrips <= 10^7`

## Examples
```text
Input: time = [1,2,3], totalTrips = 5
Output: 3
Explanation: At time `3`, the buses complete `3 + 1 + 1 = 5` trips. At time `2`, they complete only `4`.
```

## Understanding & Intuition
For a candidate time `t`, bus `i` completes `t // time[i]` trips. The total number of trips is monotonic in `t`, so the first feasible time can be found with binary search.

## Approach 1 — Naive / Brute Force
**Idea:** Check time values from 1 upward until enough trips are completed.
```python
class Solution:
    def minimumTime(self, time: list[int], totalTrips: int) -> int:
        t = 1
        while True:
            trips = 0
            for bus in time:
                trips += t // bus
            if trips >= totalTrips:
                return t
            t += 1
```
- **Time:** O(nT) — **Space:** O(1), where `T` is the answer

## Approach 2 — Better
**Idea:** Binary search from 1 to the time needed by the fastest bus doing every trip.
```python
class Solution:
    def minimumTime(self, time: list[int], totalTrips: int) -> int:
        lo, hi = 1, min(time) * totalTrips
        while lo < hi:
            mid = (lo + hi) // 2
            trips = sum(mid // bus for bus in time)
            if trips >= totalTrips:
                hi = mid
            else:
                lo = mid + 1
        return lo
```
- **Time:** O(n log T) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use the same binary search but stop counting trips as soon as the target is reached.
```python
class Solution:
    def minimumTime(self, time: list[int], totalTrips: int) -> int:
        lo, hi = 1, min(time) * totalTrips
        while lo < hi:
            mid = (lo + hi) // 2
            trips = 0
            for bus in time:
                trips += mid // bus
                if trips >= totalTrips:
                    break
            if trips >= totalTrips:
                hi = mid
            else:
                lo = mid + 1
        return lo
```
- **Time:** O(n log T) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nT) | O(1) |
| Better | O(n log T) | O(1) |
| Optimal | O(n log T) | O(1) |

## Edge Cases & Pitfalls
- The fastest bus alone gives a safe upper bound.
- Use integer division for completed trips.
- Stop at the first feasible time, not merely any feasible time.

## Related
- Capacity to Ship Packages Within D Days
- Koko Eating Bananas
