# 04. Meeting Rooms

- **Difficulty:** Easy
- **Pattern:** Intervals
- **Asked at:** Google, Amazon, Meta, Microsoft

## Problem
Given meeting intervals, return whether one person can attend all meetings. Meetings conflict when their time ranges overlap; one meeting ending exactly when another starts is allowed.

**Input**
- `intervals`: meeting intervals `[start, end]`.

**Output**
- A boolean: `True` if no meetings conflict, otherwise `False`.

## Constraints
- `0 <= intervals.length <= 10^4`
- `intervals[i].length == 2`
- `0 <= start < end <= 10^6`

## Examples
```text
Input: intervals = [[0,30],[5,10],[15,20]]
Output: false
Explanation: The meeting `[0,30]` overlaps the later meetings, so one person cannot attend all of them.
```

## Understanding & Intuition
The question asks whether any two meetings overlap. Without ordering, every pair may need checking. Once sorted by start time, only adjacent meetings can create the first conflict.

## Approach 1 — Naive / Brute Force
**Idea:** Compare every pair of meetings for overlap.
```python
from typing import List

class Solution:
    def canAttendMeetings(self, intervals: List[List[int]]) -> bool:
        n = len(intervals)
        for i in range(n):
            for j in range(i + 1, n):
                a, b = intervals[i], intervals[j]
                if a[0] < b[1] and b[0] < a[1]:
                    return False
        return True
```
- **Time:** O(n²) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Sort by start time and compare each meeting with the previous meeting.
```python
from typing import List

class Solution:
    def canAttendMeetings(self, intervals: List[List[int]]) -> bool:
        intervals = sorted(intervals, key=lambda x: x[0])
        for i in range(1, len(intervals)):
            if intervals[i][0] < intervals[i - 1][1]:
                return False
        return True
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort in place and sweep adjacent intervals with constant auxiliary space.
```python
from typing import List

class Solution:
    def canAttendMeetings(self, intervals: List[List[int]]) -> bool:
        intervals.sort(key=lambda x: x[0])
        for i in range(1, len(intervals)):
            # Back-to-back meetings are allowed; strict overlap is not.
            if intervals[i][0] < intervals[i - 1][1]:
                return False
        return True
```
- **Time:** O(n log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n²) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n log n) | O(1) |

## Edge Cases & Pitfalls
- Empty or single-meeting input is always possible.
- Use `<`, not `<=`, because meetings ending exactly when another starts do not overlap.
- Sorting by end also works if adjacent conflicts are checked consistently.

## Related
- Meeting Rooms II
- Non-overlapping Intervals
