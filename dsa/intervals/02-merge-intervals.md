# 02. Merge Intervals

- **Difficulty:** Medium
- **Pattern:** Intervals
- **Asked at:** Google, Amazon, Microsoft, Bloomberg

## Problem
Given an array of intervals, merge all overlapping intervals and return an array of non-overlapping intervals covering the same ranges. Constraints: `1 <= len(intervals) <= 10^4`, each interval has `start <= end`.

## Examples
```text
Input: intervals = [[1,3],[2,6],[8,10],[15,18]]
Output: [[1,6],[8,10],[15,18]]
Explanation: [1,3] and [2,6] overlap.
```

## Understanding & Intuition
Two intervals overlap when the next start is at most the current end. Sorting by start makes all possible overlaps appear next to each other. Then one sweep is enough to build merged ranges.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly find any overlapping pair, merge it, and restart until no pair overlaps.
```python
from typing import List

class Solution:
    def merge(self, intervals: List[List[int]]) -> List[List[int]]:
        arr = [x[:] for x in intervals]

        changed = True
        while changed:
            changed = False
            for i in range(len(arr)):
                for j in range(i + 1, len(arr)):
                    if arr[i][0] <= arr[j][1] and arr[j][0] <= arr[i][1]:
                        arr[i] = [min(arr[i][0], arr[j][0]), max(arr[i][1], arr[j][1])]
                        arr.pop(j)
                        changed = True
                        break
                if changed:
                    break
        return sorted(arr, key=lambda x: x[0])
```
- **Time:** O(n³) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort intervals and append merged copies to a new result list.
```python
from typing import List

class Solution:
    def merge(self, intervals: List[List[int]]) -> List[List[int]]:
        intervals = sorted(intervals, key=lambda x: x[0])
        result = []

        for start, end in intervals:
            if not result or start > result[-1][1]:
                result.append([start, end])
            else:
                result[-1][1] = max(result[-1][1], end)
        return result
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort in place and reuse the interval objects while sweeping.
```python
from typing import List

class Solution:
    def merge(self, intervals: List[List[int]]) -> List[List[int]]:
        intervals.sort(key=lambda x: x[0])
        write = 0

        for interval in intervals:
            if write == 0 or interval[0] > intervals[write - 1][1]:
                intervals[write] = interval
                write += 1
            else:
                intervals[write - 1][1] = max(intervals[write - 1][1], interval[1])
        return intervals[:write]
```
- **Time:** O(n log n) — **Space:** O(1) auxiliary besides output

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n³) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n log n) | O(1) auxiliary |

## Edge Cases & Pitfalls
- Sort before sweeping; input order can be arbitrary.
- Adjacent ranges like `[1,4]` and `[4,5]` count as overlapping.
- Preserve the maximum end when one interval fully contains another.

## Related
- Insert Interval
- Non-overlapping Intervals
