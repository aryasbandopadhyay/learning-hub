# 08. Employee Free Time

- **Difficulty:** Hard
- **Pattern:** Intervals
- **Asked at:** Google, Amazon, Meta, Airbnb

## Problem
Given each employee's sorted, non-overlapping work schedule, return finite intervals when all employees are free. In LeetCode, intervals are `Interval` objects with `start` and `end` fields. Constraints: total number of intervals is up to `10^4`.

## Examples
```text
Input: schedule = [[[1,2],[5,6]],[[1,3]],[[4,10]]]
Output: [[3,4]]
Explanation: All employees are free only between time 3 and time 4.
```

## Understanding & Intuition
Common free time is the gap between merged busy intervals across all employees. We can flatten all busy intervals and merge them, or use a heap to stream the next busy interval from each employee. Infinite time before the first meeting and after the last meeting is not returned.

## Approach 1 — Naive / Brute Force
**Idea:** Flatten schedules, sort by start, merge busy time, then output gaps between merged busy intervals.
```python
from typing import List

# Definition for an Interval.
class Interval:
    def __init__(self, start: int = None, end: int = None):
        self.start = start
        self.end = end

class Solution:
    def employeeFreeTime(self, schedule: List[List[Interval]]) -> List[Interval]:
        busy = []
        for employee in schedule:
            for interval in employee:
                busy.append(interval)
        busy.sort(key=lambda x: x.start)

        merged = []
        for interval in busy:
            if not merged or interval.start > merged[-1].end:
                merged.append(Interval(interval.start, interval.end))
            else:
                merged[-1].end = max(merged[-1].end, interval.end)

        free = []
        for i in range(1, len(merged)):
            free.append(Interval(merged[i - 1].end, merged[i].start))
        return free
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** K-way merge employee schedules with a heap, tracking the latest busy end seen so far.
```python
from typing import List
import heapq

# Definition for an Interval.
class Interval:
    def __init__(self, start: int = None, end: int = None):
        self.start = start
        self.end = end

class Solution:
    def employeeFreeTime(self, schedule: List[List[Interval]]) -> List[Interval]:
        heap = []
        for emp, intervals in enumerate(schedule):
            if intervals:
                heapq.heappush(heap, (intervals[0].start, emp, 0))

        free = []
        prev_end = None
        while heap:
            _, emp, idx = heapq.heappop(heap)
            interval = schedule[emp][idx]
            if prev_end is not None and interval.start > prev_end:
                free.append(Interval(prev_end, interval.start))
            prev_end = interval.end if prev_end is None else max(prev_end, interval.end)

            if idx + 1 < len(schedule[emp]):
                nxt = schedule[emp][idx + 1]
                heapq.heappush(heap, (nxt.start, emp, idx + 1))
        return free
```
- **Time:** O(n log k) — **Space:** O(k + g)

## Approach 3 — Optimal
**Idea:** Sweep sorted start/end events; a free interval begins when active busy count drops to zero and ends at the next start.
```python
from typing import List

# Definition for an Interval.
class Interval:
    def __init__(self, start: int = None, end: int = None):
        self.start = start
        self.end = end

class Solution:
    def employeeFreeTime(self, schedule: List[List[Interval]]) -> List[Interval]:
        events = []
        for employee in schedule:
            for interval in employee:
                events.append((interval.start, 1))
                events.append((interval.end, -1))
        events.sort()

        free = []
        active = 0
        free_start = None
        for time, delta in events:
            previous = active
            active += delta
            if previous > 0 and active == 0:
                free_start = time
            elif previous == 0 and active > 0 and free_start is not None and free_start < time:
                free.append(Interval(free_start, time))
                free_start = None
        return free
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n log n) | O(n) |
| Better | O(n log k) | O(k + g) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Do not return infinite free time before the first busy interval or after the last.
- Use strict gap checks; touching intervals produce no free interval.
- Heap entries need employee and interval indices to avoid comparing `Interval` objects.

## Related
- Meeting Rooms II
- Merge Intervals
