# 03. Maximum Number of Events That Can Be Attended

- **Difficulty:** Medium
- **Pattern:** intervals
- **Asked at:** Google, Amazon, Meta

## Problem
You are given `events`, where `events[i] = [startDay, endDay]`. You may attend at most one event on any day, and an event can be attended on any one day in its inclusive range. Return the maximum number of events you can attend.

Constraints: `1 <= len(events) <= 10^5`, `1 <= startDay <= endDay <= 10^5`.

## Examples
```text
Input: events = [[1,2],[2,3],[3,4]]
Output: 3
Explanation: Attend on days 1, 2, and 3 respectively.
```

## Understanding & Intuition
Each event needs exactly one available day inside its interval. The greedy choice is to fill days from left to right and always attend the currently available event that ends earliest. This preserves later days for events with more flexibility.

## Approach 1 — Naive / Brute Force
**Idea:** Model events and days as a bipartite graph, then find an augmenting-path matching.
```python
class Solution:
    def maxEvents(self, events: list[list[int]]) -> int:
        match_day_to_event = {}

        def can_assign(i, seen_days):
            start, end = events[i]
            for day in range(start, end + 1):
                if day in seen_days:
                    continue
                seen_days.add(day)
                if day not in match_day_to_event or can_assign(match_day_to_event[day], seen_days):
                    match_day_to_event[day] = i
                    return True
            return False

        attended = 0
        for i in range(len(events)):
            if can_assign(i, set()):
                attended += 1
        return attended
```
- **Time:** O(nE) — **Space:** O(n + D)

## Approach 2 — Better
**Idea:** Process events by earliest end and reserve the first free day in each interval by scanning forward.
```python
class Solution:
    def maxEvents(self, events: list[list[int]]) -> int:
        used = set()
        attended = 0
        for start, end in sorted(events, key=lambda x: (x[1], x[0])):
            for day in range(start, end + 1):
                if day not in used:
                    used.add(day)
                    attended += 1
                    break
        return attended
```
- **Time:** O(nD + n log n) — **Space:** O(D)

## Approach 3 — Optimal
**Idea:** Sweep days, add events that have started to a min-heap by end day, and attend the one ending earliest.
```python
class Solution:
    def maxEvents(self, events: list[list[int]]) -> int:
        import heapq
        events.sort()
        i = 0
        day = 0
        attended = 0
        heap = []
        n = len(events)
        while i < n or heap:
            if not heap:
                day = max(day, events[i][0])
            while i < n and events[i][0] <= day:
                heapq.heappush(heap, events[i][1])
                i += 1
            while heap and heap[0] < day:
                heapq.heappop(heap)
            if heap:
                heapq.heappop(heap)
                attended += 1
                day += 1
        return attended
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nE) | O(n + D) |
| Better | O(nD + n log n) | O(D) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Intervals are inclusive, so an event ending on `day` can still be attended that day.
- Remove expired events before choosing from the heap.
- If no event is available, jump directly to the next start day.

## Related
- Meeting Rooms II
- Task Scheduler
- Minimum Interval to Include Each Query
