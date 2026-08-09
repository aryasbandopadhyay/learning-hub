# 06. My Calendar II Bookings

- **Difficulty:** Medium
- **Pattern:** intervals
- **Asked at:** Google, Amazon, Bloomberg

## Problem
Given half-open booking requests `bookings`, process them in order. Accept a request if adding it would not make any time point covered by three accepted bookings; otherwise reject it. Return a list of booleans showing which requests are accepted.

Implement `Solution.calendarTwoBookings` with the parameters below and return the requested value.

**Input**
- `bookings`: a `list[list[int]]`; booking requests in processing order.

**Output**
- A boolean list in the same order as `bookings`, where each value says whether that request was accepted.

## Constraints
- `1 <= len(bookings) <= 10^4`, `0 <= start < end <= 10^9`

## Examples
```text
Input: bookings = [[10,20],[50,60],[10,40],[5,15],[5,10],[25,55]]
Output: [True, True, True, False, True, True]
Explanation: [5,15) is rejected because it would triple-book [10,15). The result is shown in the required order.
```

## Understanding & Intuition
Double bookings are allowed, but triple bookings are not. For a new interval, it is enough to know whether it overlaps any already double-booked segment. We can either recompute a sweep after each tentative booking or maintain those double-booked segments incrementally.

## Approach 1 — Naive / Brute Force
**Idea:** Tentatively add each booking and sweep all start/end events to see whether active bookings ever reach three.
```python
class Solution:
    def calendarTwoBookings(self, bookings: list[list[int]]) -> list[bool]:
        accepted = []
        result = []
        for start, end in bookings:
            events = []
            for a, b in accepted:
                events.append((a, 1))
                events.append((b, -1))
            events.append((start, 1))
            events.append((end, -1))
            events.sort(key=lambda x: (x[0], x[1]))
            active = 0
            ok = True
            for _, delta in events:
                active += delta
                if active >= 3:
                    ok = False
                    break
            result.append(ok)
            if ok:
                accepted.append([start, end])
        return result
```
- **Time:** O(n^2 log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Store accepted intervals and all double-booked intersections; reject when a new request overlaps a stored double-booked segment.
```python
class Solution:
    def calendarTwoBookings(self, bookings: list[list[int]]) -> list[bool]:
        accepted = []
        overlaps = []
        result = []
        for start, end in bookings:
            ok = True
            for a, b in overlaps:
                if start < b and a < end:
                    ok = False
                    break
            result.append(ok)
            if not ok:
                continue
            for a, b in accepted:
                left = max(start, a)
                right = min(end, b)
                if left < right:
                    overlaps.append([left, right])
            accepted.append([start, end])
        return result
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Maintain an ordered difference map; apply each request, sweep to check for triple booking, and roll back if needed.
```python
class Solution:
    def calendarTwoBookings(self, bookings: list[list[int]]) -> list[bool]:
        from collections import defaultdict
        diff = defaultdict(int)
        result = []
        for start, end in bookings:
            diff[start] += 1
            diff[end] -= 1
            active = 0
            ok = True
            for point in sorted(diff):
                active += diff[point]
                if active >= 3:
                    ok = False
                    break
            if not ok:
                diff[start] -= 1
                diff[end] += 1
                if diff[start] == 0:
                    del diff[start]
                if diff[end] == 0:
                    del diff[end]
            result.append(ok)
        return result
```
- **Time:** O(n^2 log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2 log n) | O(n) |
| Better | O(n^2) | O(n^2) |
| Optimal | O(n^2 log n) | O(n) |

## Edge Cases & Pitfalls
- Sort end events before start events at the same time for half-open intervals.
- Rejected requests must be rolled back completely.
- Overlap segments may duplicate; that is fine because any overlap with one means triple booking.

## Related
- My Calendar I
- Meeting Rooms II
- Car Pooling
