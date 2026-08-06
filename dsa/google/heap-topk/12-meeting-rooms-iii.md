# 12. Meeting Rooms III

- **Difficulty:** Hard
- **Pattern:** Heaps / Top-K / Greedy-with-Heap
- **Asked at:** Google

## Problem
There are `n` rooms numbered `0` to `n - 1` and a list of meetings `[start, end]`. Meetings are processed by increasing start time. If rooms are free, use the smallest-numbered free room; otherwise delay the meeting until the earliest room becomes free, preserving its duration. Return the room that hosted the most meetings, breaking ties by smaller room number. Constraints: `1 <= n <= 100`, `1 <= len(meetings) <= 100000`.

## Examples
```text
Input: n = 2, meetings = [[0,10],[1,5],[2,7],[3,4]]
Output: 0
Explanation: Room 0 hosts two meetings and room 1 hosts two meetings, so the smaller room number wins.
```

## Understanding & Intuition
Two priorities control the simulation: the smallest available room and the busy room that frees earliest. If no room is available, delaying the current meeting to the earliest free time is forced. Heaps model both priorities directly.

## Approach 1 — Naive / Brute Force
**Idea:** Track each room's next free time and linearly choose a free room or the earliest busy room for every meeting.
```python
class Solution:
    def mostBooked(self, n: int, meetings: list[list[int]]) -> int:
        free_at = [0] * n
        count = [0] * n
        for start, end in sorted(meetings):
            duration = end - start
            chosen = -1
            for room in range(n):
                if free_at[room] <= start:
                    chosen = room
                    break
            if chosen == -1:
                chosen = min(range(n), key=lambda r: (free_at[r], r))
                start = free_at[chosen]
            free_at[chosen] = start + duration
            count[chosen] += 1
        return min(range(n), key=lambda r: (-count[r], r))
```
- **Time:** O(mn) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Maintain sorted lists of idle rooms and busy rooms, moving rooms that have become free before each meeting.
```python
class Solution:
    def mostBooked(self, n: int, meetings: list[list[int]]) -> int:
        import bisect
        idle = list(range(n))
        busy = []
        count = [0] * n
        for start, end in sorted(meetings):
            duration = end - start
            while busy and busy[0][0] <= start:
                free_time, room = busy.pop(0)
                bisect.insort(idle, room)
            if idle:
                room = idle.pop(0)
                finish = end
            else:
                free_time, room = busy.pop(0)
                finish = free_time + duration
            count[room] += 1
            bisect.insort(busy, (finish, room))
        return min(range(n), key=lambda r: (-count[r], r))
```
- **Time:** O(mn) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a min-heap for idle room numbers and another min-heap for busy `(free_time, room)` pairs.
```python
class Solution:
    def mostBooked(self, n: int, meetings: list[list[int]]) -> int:
        import heapq
        idle = list(range(n))
        heapq.heapify(idle)
        busy = []
        count = [0] * n
        for start, end in sorted(meetings):
            duration = end - start
            while busy and busy[0][0] <= start:
                _, room = heapq.heappop(busy)
                heapq.heappush(idle, room)
            if idle:
                room = heapq.heappop(idle)
                finish = end
            else:
                free_time, room = heapq.heappop(busy)
                finish = free_time + duration
            count[room] += 1
            heapq.heappush(busy, (finish, room))
        return min(range(n), key=lambda r: (-count[r], r))
```
- **Time:** O(m log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn) | O(n) |
| Better | O(mn) | O(n) |
| Optimal | O(m log n) | O(n) |

## Edge Cases & Pitfalls
- Delayed meetings keep their original duration.
- Free all rooms whose finish time is at or before the meeting start.
- Final tie-breaking is by smallest room number.

## Related
- Meeting Rooms II
- Single-Threaded CPU
