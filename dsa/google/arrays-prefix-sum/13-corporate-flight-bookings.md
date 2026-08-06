# 13. Corporate Flight Bookings

- **Difficulty:** Medium
- **Pattern:** Arrays & Prefix Sums
- **Asked at:** Google, LeetCode

## Problem
There are `n` flights labeled from `1` to `n`. Each booking is given as `[first, last, seats]`, meaning `seats` seats were booked on every flight from `first` through `last`, inclusive. Return a list `answer` of length `n` where `answer[i]` is the total seats booked on flight `i + 1`.

Constraints: `1 <= n <= 20000`; `1 <= len(bookings) <= 20000`; `1 <= first <= last <= n`; `1 <= seats <= 10000`.

## Examples
```text
Input: bookings = [[1, 2, 10], [2, 3, 20], [2, 5, 25]], n = 5
Output: [10, 55, 45, 25, 25]
Explanation: Flight 1 has 10 seats, flight 2 has 10 + 20 + 25, flight 3 has 20 + 25, and flights 4-5 have 25.
```

## Understanding & Intuition
Each booking adds the same value over a contiguous range of flights. Updating every covered flight is direct but can repeat a lot of work. A difference array records only where a range addition starts and stops, then one prefix scan reconstructs every flight total.

## Approach 1 — Naive / Brute Force
**Idea:** Apply each booking directly to every flight in its inclusive range.
```python
class Solution:
    def corpFlightBookings(self, bookings: list[list[int]], n: int) -> list[int]:
        ans = [0] * n
        for first, last, seats in bookings:
            for flight in range(first - 1, last):
                ans[flight] += seats
        return ans
```
- **Time:** O(n * m) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Convert bookings to start/end events and sweep flights in order after sorting those events.
```python
class Solution:
    def corpFlightBookings(self, bookings: list[list[int]], n: int) -> list[int]:
        events = []
        for first, last, seats in bookings:
            events.append((first, seats))
            events.append((last + 1, -seats))
        events.sort()
        ans = [0] * n
        active = 0
        idx = 0
        for flight in range(1, n + 1):
            while idx < len(events) and events[idx][0] == flight:
                active += events[idx][1]
                idx += 1
            ans[flight - 1] = active
        return ans
```
- **Time:** O(m log m + n) — **Space:** O(n + m)

## Approach 3 — Optimal
**Idea:** Use a difference array so each booking changes only two positions, then prefix-sum it.
```python
class Solution:
    def corpFlightBookings(self, bookings: list[list[int]], n: int) -> list[int]:
        diff = [0] * (n + 1)
        for first, last, seats in bookings:
            diff[first - 1] += seats
            diff[last] -= seats
        ans = [0] * n
        running = 0
        for i in range(n):
            running += diff[i]
            ans[i] = running
        return ans
```
- **Time:** O(n + m) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * m) | O(n) |
| Better | O(m log m + n) | O(n + m) |
| Optimal | O(n + m) | O(n) |

## Edge Cases & Pitfalls
- Convert 1-indexed flight labels to 0-indexed list positions carefully.
- Subtract at `last`, not `last - 1`, in the difference array.
- Bookings can overlap many times, so totals may exceed a single booking's seats.

## Related
- Range Addition
- Car Pooling
