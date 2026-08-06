# 01. Car Pooling

- **Difficulty:** Medium
- **Pattern:** intervals
- **Asked at:** Google, Uber, Lyft

## Problem
You are given `trips`, where `trips[i] = [passengers, start, end]` means that many passengers enter at location `start` and leave before location `end`. Given the vehicle `capacity`, return `True` if all trips can be completed without ever exceeding capacity, otherwise return `False`.

Constraints: `1 <= len(trips) <= 10^4`, `1 <= passengers <= 10^5`, `0 <= start < end <= 10^5`, and `1 <= capacity <= 10^9`.

## Examples
```text
Input: trips = [[2,1,5],[3,3,7]], capacity = 4
Output: False
Explanation: At location 3, both trips are active and need 5 seats.
```

## Understanding & Intuition
Each trip is an interval on a one-dimensional route. Pickups increase the active passenger count and drop-offs decrease it; because `end` is exclusive, dropping passengers at `end` happens before any later segment. The task is to test whether the maximum overlap weight exceeds capacity.

## Approach 1 — Naive / Brute Force
**Idea:** Check every unit road segment and sum passengers from trips active on that segment.
```python
class Solution:
    def carPooling(self, trips: list[list[int]], capacity: int) -> bool:
        last = max(end for _, _, end in trips)
        for pos in range(last):
            riders = 0
            for passengers, start, end in trips:
                if start <= pos < end:
                    riders += passengers
            if riders > capacity:
                return False
        return True
```
- **Time:** O(nR) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use a difference array over route positions, then prefix-sum passenger changes.
```python
class Solution:
    def carPooling(self, trips: list[list[int]], capacity: int) -> bool:
        last = max(end for _, _, end in trips)
        diff = [0] * (last + 1)
        for passengers, start, end in trips:
            diff[start] += passengers
            diff[end] -= passengers
        riders = 0
        for change in diff:
            riders += change
            if riders > capacity:
                return False
        return True
```
- **Time:** O(n + R) — **Space:** O(R)

## Approach 3 — Optimal
**Idea:** Sweep only pickup and drop-off event coordinates, aggregating deltas in sorted order.
```python
class Solution:
    def carPooling(self, trips: list[list[int]], capacity: int) -> bool:
        events = []
        for passengers, start, end in trips:
            events.append((start, passengers))
            events.append((end, -passengers))
        events.sort()
        riders = 0
        for _, change in events:
            riders += change
            if riders > capacity:
                return False
        return True
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nR) | O(1) |
| Better | O(n + R) | O(R) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Drop-offs at `end` are exclusive and should reduce capacity before the next segment.
- Multiple events can happen at the same coordinate.
- Capacity can be much larger than any individual trip but smaller than an overlap.

## Related
- Meeting Rooms II
- Corporate Flight Bookings
- Minimum Number of Arrows to Burst Balloons
