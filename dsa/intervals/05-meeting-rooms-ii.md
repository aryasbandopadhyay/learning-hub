# 05. Meeting Rooms II

- **Difficulty:** Medium
- **Pattern:** Intervals
- **Asked at:** Google, Amazon, Meta, Bloomberg

## Problem
Given meeting intervals, return the minimum number of conference rooms required so all meetings can happen. Constraints: `0 <= len(intervals) <= 10^4`, each interval has `start < end`.

## Examples
```text
Input: intervals = [[0,30],[5,10],[15,20]]
Output: 2
Explanation: The first meeting needs one room, and the overlapping short meetings need one more.
```

## Understanding & Intuition
The answer is the maximum number of meetings active at the same time. We can simulate starts and ends as events, or keep the earliest ending meeting in a min-heap. When a room frees before the next start, it can be reused.

## Approach 1 — Naive / Brute Force
**Idea:** For each meeting start time, count how many meetings are active at that moment.
```python
from typing import List

class Solution:
    def minMeetingRooms(self, intervals: List[List[int]]) -> int:
        best = 0
        for start, _ in intervals:
            active = 0
            for s, e in intervals:
                if s <= start < e:
                    active += 1
            best = max(best, active)
        return best
```
- **Time:** O(n²) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Sort start and end times separately, then sweep two pointers.
```python
from typing import List

class Solution:
    def minMeetingRooms(self, intervals: List[List[int]]) -> int:
        starts = sorted(s for s, _ in intervals)
        ends = sorted(e for _, e in intervals)
        rooms = end_ptr = 0

        for start in starts:
            if start >= ends[end_ptr]:
                end_ptr += 1
            else:
                rooms += 1
        return rooms
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort meetings by start and use a min-heap of room end times.
```python
from typing import List
import heapq

class Solution:
    def minMeetingRooms(self, intervals: List[List[int]]) -> int:
        intervals.sort(key=lambda x: x[0])
        heap = []

        for start, end in intervals:
            if heap and heap[0] <= start:
                heapq.heappop(heap)
            heapq.heappush(heap, end)
        return len(heap)
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n²) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Return `0` for no meetings.
- A meeting ending at time `t` frees a room for another starting at `t`.
- Heap size after processing all meetings equals the maximum active rooms because starts are processed in order.

## Related
- Meeting Rooms
- Employee Free Time
