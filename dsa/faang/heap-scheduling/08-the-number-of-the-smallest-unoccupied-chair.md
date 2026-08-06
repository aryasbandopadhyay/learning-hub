# 08. The Number of the Smallest Unoccupied Chair

- **Difficulty:** Medium
- **Pattern:** heaps for scheduling/simulation
- **Asked at:** Google, Amazon, Bloomberg

## Problem
There are infinitely many chairs numbered from `0`. Friend `i` arrives at `times[i][0]` and leaves at `times[i][1]`. When a friend arrives, they sit in the smallest-numbered unoccupied chair. If another friend leaves at the same time, their chair becomes available before arrivals at that time.

Return the chair number assigned to `targetFriend`.

Constraints: `2 <= len(times) <= 10^4`, all arrival times are distinct.

## Examples
```text
Input: times = [[1,4],[2,3],[4,6]], targetFriend = 1
Output: 1
Explanation: Friend 0 takes chair 0, friend 1 takes chair 1, then friend 0 frees chair 0 at time 4.
```

## Understanding & Intuition
The simulation needs two priorities: chairs becoming free by leaving time, and the smallest currently free chair. Since arrivals are processed in chronological order, releasing chairs before seating each arrival is sufficient. The target can be returned as soon as it is seated.

## Approach 1 — Naive / Brute Force
**Idea:** Process arrivals in order, scan occupied chairs to release them, and linearly find the smallest free chair.
```python
class Solution:
    def smallestChair(self, times: list[list[int]], targetFriend: int) -> int:
        arrivals = sorted((a, b, i) for i, (a, b) in enumerate(times))
        occupied = {}
        for arrive, leave, friend in arrivals:
            for chair, end in list(occupied.items()):
                if end <= arrive:
                    del occupied[chair]
            chair = 0
            while chair in occupied:
                chair += 1
            if friend == targetFriend:
                return chair
            occupied[chair] = leave
        return -1
```
- **Time:** O(n²) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a heap for leaving friends, but keep available chairs in a sorted list.
```python
class Solution:
    def smallestChair(self, times: list[list[int]], targetFriend: int) -> int:
        import heapq
        import bisect
        arrivals = sorted((a, b, i) for i, (a, b) in enumerate(times))
        leaving = []
        free = []
        next_chair = 0
        for arrive, leave, friend in arrivals:
            while leaving and leaving[0][0] <= arrive:
                _, chair = heapq.heappop(leaving)
                bisect.insort(free, chair)
            if free:
                chair = free.pop(0)
            else:
                chair = next_chair
                next_chair += 1
            if friend == targetFriend:
                return chair
            heapq.heappush(leaving, (leave, chair))
        return -1
```
- **Time:** O(n²) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a min-heap for free chairs and another min-heap for `(leave_time, chair)`.
```python
class Solution:
    def smallestChair(self, times: list[list[int]], targetFriend: int) -> int:
        import heapq
        arrivals = sorted((a, b, i) for i, (a, b) in enumerate(times))
        free = []
        leaving = []
        next_chair = 0
        for arrive, leave, friend in arrivals:
            while leaving and leaving[0][0] <= arrive:
                _, chair = heapq.heappop(leaving)
                heapq.heappush(free, chair)
            if free:
                chair = heapq.heappop(free)
            else:
                chair = next_chair
                next_chair += 1
            if friend == targetFriend:
                return chair
            heapq.heappush(leaving, (leave, chair))
        return -1
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n²) | O(n) |
| Better | O(n²) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Departures at time `t` happen before arrivals at time `t`.
- New chairs are allocated sequentially only when no freed chair exists.
- Arrival times are distinct, so target seating is unambiguous.

## Related
- Meeting Rooms II
- Process Tasks Using Servers
