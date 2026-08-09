# 13. Corporate Flight Bookings

- **Difficulty:** Medium
- **Pattern:** Arrays & Prefix Sums
- **Asked at:** Google, LeetCode

## Problem
There are `n` flights numbered from `1` to `n`. Each booking `[first, last, seats]` adds `seats` booked seats to every flight from `first` through `last`, inclusive.

Return the total booked seats for every flight after all bookings are applied.

**Input**
- `bookings`: a list of `[first, last, seats]` records.
- `n`: the number of flights.

**Output**
- A length-`n` list where index `i - 1` is the total for flight `i`. **This judge compares exactly**, so return totals in flight-number order from `1` to `n`.

## Constraints
- `1 <= n <= 2 * 10^4`
- `1 <= bookings.length <= 2 * 10^4`
- `1 <= first <= last <= n`
- `1 <= seats <= 10^4`

## Examples
```text
Input: bookings = [[1, 2, 10], [2, 3, 20], [2, 5, 25]], n = 5
Output: [10, 55, 45, 25, 25]
Explanation: Flight totals are `10`, `10+20+25=55`, `20+25=45`, `25`, and `25`, in order from flight `1` to flight `5`.
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
