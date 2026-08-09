# 05. My Calendar I Bookings

- **Difficulty:** Medium
- **Pattern:** intervals
- **Asked at:** Google, Amazon, Meta

## Problem
Given `bookings`, where `bookings[i] = [start, end]` is a half-open interval `[start, end)`, process the requests in order. Accept a booking if it does not overlap any previously accepted booking; otherwise reject it. Return a list of booleans indicating whether each request was accepted.

Implement `Solution.calendarBookings` with the parameters below and return the requested value.

**Input**
- `bookings`: a `list[list[int]]`; booking requests in processing order.

**Output**
- A boolean list in the same order as `bookings`, where each value says whether that request was accepted.

## Constraints
- `1 <= len(bookings) <= 10^4`, `0 <= start < end <= 10^9`

## Examples
```text
Input: bookings = [[10,20],[15,25],[20,30]]
Output: [True, False, True]
Explanation: [15,25) overlaps [10,20), while [20,30) starts exactly when [10,20) ends.
```

## Understanding & Intuition
This is the usual calendar booking problem expressed as a pure function over all requests. A new half-open interval overlaps an accepted interval when `start < oldEnd` and `oldStart < end`. Accepted intervals can also be maintained in sorted order to check only neighbors.

## Approach 1 — Naive / Brute Force
**Idea:** Keep accepted bookings unsorted and compare each new request with every accepted interval.
```python
class Solution:
    def calendarBookings(self, bookings: list[list[int]]) -> list[bool]:
        accepted = []
        result = []
        for start, end in bookings:
            ok = True
            for old_start, old_end in accepted:
                if start < old_end and old_start < end:
                    ok = False
                    break
            result.append(ok)
            if ok:
                accepted.append([start, end])
        return result
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Keep accepted intervals sorted by start and use binary search to inspect only the previous and next intervals.
```python
class Solution:
    def calendarBookings(self, bookings: list[list[int]]) -> list[bool]:
        from bisect import bisect_left, insort
        accepted = []
        result = []
        for start, end in bookings:
            i = bisect_left(accepted, [start, end])
            ok = True
            if i > 0 and accepted[i - 1][1] > start:
                ok = False
            if i < len(accepted) and accepted[i][0] < end:
                ok = False
            result.append(ok)
            if ok:
                insort(accepted, [start, end])
        return result
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Maintain separate sorted starts and ends, then neighbor-check and insert using array positions.
```python
class Solution:
    def calendarBookings(self, bookings: list[list[int]]) -> list[bool]:
        from bisect import bisect_left
        starts = []
        ends = []
        result = []
        for start, end in bookings:
            i = bisect_left(starts, start)
            ok = True
            if i > 0 and ends[i - 1] > start:
                ok = False
            if i < len(starts) and starts[i] < end:
                ok = False
            result.append(ok)
            if ok:
                starts.insert(i, start)
                ends.insert(i, end)
        return result
```
- **Time:** O(n^2) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n^2) | O(n) |

## Edge Cases & Pitfalls
- Intervals are half-open, so `end == next_start` is allowed.
- Rejected bookings must not be added to the accepted set.
- Requests are processed online in the given order.

## Related
- My Calendar II
- Meeting Rooms
- Insert Interval
