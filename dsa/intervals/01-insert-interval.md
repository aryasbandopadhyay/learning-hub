# 01. Insert Interval

- **Difficulty:** Medium
- **Pattern:** Intervals
- **Asked at:** Google, Amazon, Microsoft, Meta

## Problem
Given a list of non-overlapping intervals sorted by start time and a new interval, insert the new interval so the final list is still sorted and non-overlapping. Return the merged intervals. Constraints: `0 <= len(intervals) <= 10^4`, each interval has `start <= end`, and interval values fit in standard integers.

## Examples
```text
Input: intervals = [[1,3],[6,9]], newInterval = [2,5]
Output: [[1,5],[6,9]]
Explanation: [1,3] overlaps [2,5], so they merge into [1,5].
```

## Understanding & Intuition
Intervals strictly before the new interval can be copied as-is. Intervals that overlap must be folded into one wider interval. Once the overlap ends, the rest can also be copied unchanged.

## Approach 1 — Naive / Brute Force
**Idea:** Add the interval, sort everything, then repeatedly merge adjacent overlaps.
```python
from typing import List

class Solution:
    def insert(self, intervals: List[List[int]], newInterval: List[int]) -> List[List[int]]:
        arr = intervals + [newInterval]
        arr.sort(key=lambda x: x[0])

        changed = True
        while changed:
            changed = False
            merged = []
            for interval in arr:
                # Merge if this interval touches or overlaps the previous one.
                if merged and interval[0] <= merged[-1][1]:
                    merged[-1][1] = max(merged[-1][1], interval[1])
                    changed = True
                else:
                    merged.append(interval[:])
            arr = merged
        return arr
```
- **Time:** O(n log n + n²) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Insert the new interval at its sorted position, then do one merge pass.
```python
from typing import List

class Solution:
    def insert(self, intervals: List[List[int]], newInterval: List[int]) -> List[List[int]]:
        arr = []
        placed = False

        for interval in intervals:
            if not placed and newInterval[0] < interval[0]:
                arr.append(newInterval[:])
                placed = True
            arr.append(interval[:])
        if not placed:
            arr.append(newInterval[:])

        merged = []
        for start, end in arr:
            if merged and start <= merged[-1][1]:
                merged[-1][1] = max(merged[-1][1], end)
            else:
                merged.append([start, end])
        return merged
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Copy left intervals, merge only overlapping intervals into `newInterval`, then copy right intervals.
```python
from typing import List

class Solution:
    def insert(self, intervals: List[List[int]], newInterval: List[int]) -> List[List[int]]:
        result = []
        i = 0
        n = len(intervals)

        while i < n and intervals[i][1] < newInterval[0]:
            result.append(intervals[i])
            i += 1

        while i < n and intervals[i][0] <= newInterval[1]:
            # Expand the new interval to cover every overlap.
            newInterval[0] = min(newInterval[0], intervals[i][0])
            newInterval[1] = max(newInterval[1], intervals[i][1])
            i += 1
        result.append(newInterval)

        while i < n:
            result.append(intervals[i])
            i += 1
        return result
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n log n + n²) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Empty interval list should return just the new interval.
- New interval may belong at the start or end.
- Mutating `newInterval` is fine on LeetCode but copy it if the caller needs it unchanged.

## Related
- Merge Intervals
- Interval List Intersections
