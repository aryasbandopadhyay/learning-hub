# 12. Minimum Time to Complete Trips

- **Difficulty:** Medium
- **Pattern:** Advanced Binary Search
- **Asked at:** Google

## Problem
Given `time`, where `time[i]` is the time one bus needs to complete one trip, and an integer `totalTrips`, return the minimum time needed for all buses together to complete at least `totalTrips` trips. Each bus can run trips back-to-back independently.

Constraints: `1 <= len(time) <= 10^5`, `1 <= time[i] <= 10^7`, `1 <= totalTrips <= 10^14`.

## Examples
```text
Input: time = [1,2,3], totalTrips = 5
Output: 3
Explanation: In 3 units of time, the buses complete 3 + 1 + 1 = 5 trips.
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
