# 03. Non-overlapping Intervals

- **Difficulty:** Medium
- **Pattern:** Intervals
- **Asked at:** Google, Amazon, Meta, Microsoft

## Problem
Given intervals, return the minimum number of intervals to remove so the remaining intervals do not overlap. Constraints: `1 <= len(intervals) <= 10^5`, each interval has `start < end`.

## Examples
```text
Input: intervals = [[1,2],[2,3],[3,4],[1,3]]
Output: 1
Explanation: Removing [1,3] leaves non-overlapping intervals.
```

## Understanding & Intuition
Minimizing removals is the same as keeping as many compatible intervals as possible. The interval that ends earliest leaves the most room for future intervals. This leads to a classic greedy activity-selection solution.

## Approach 1 — Naive / Brute Force
**Idea:** Try every subset and keep the largest subset that has no overlaps.
```python
from typing import List

class Solution:
    def eraseOverlapIntervals(self, intervals: List[List[int]]) -> int:
        n = len(intervals)
        best = 0

        for mask in range(1 << n):
            chosen = [intervals[i] for i in range(n) if mask & (1 << i)]
            chosen.sort()
            ok = True
            for i in range(1, len(chosen)):
                if chosen[i][0] < chosen[i - 1][1]:
                    ok = False
                    break
            if ok:
                best = max(best, len(chosen))
        return n - best
```
- **Time:** O(2ⁿ · n log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort by start and use dynamic programming for the longest non-overlapping subsequence.
```python
from typing import List

class Solution:
    def eraseOverlapIntervals(self, intervals: List[List[int]]) -> int:
        intervals.sort(key=lambda x: (x[0], x[1]))
        n = len(intervals)
        dp = [1] * n

        for i in range(n):
            for j in range(i):
                # Compatible because j ends before i starts.
                if intervals[j][1] <= intervals[i][0]:
                    dp[i] = max(dp[i], dp[j] + 1)
        return n - max(dp)
```
- **Time:** O(n²) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort by end time and greedily keep every interval that starts after the previous kept end.
```python
from typing import List

class Solution:
    def eraseOverlapIntervals(self, intervals: List[List[int]]) -> int:
        intervals.sort(key=lambda x: x[1])
        kept = 0
        last_end = float("-inf")

        for start, end in intervals:
            if start >= last_end:
                kept += 1
                last_end = end
        return len(intervals) - kept
```
- **Time:** O(n log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2ⁿ · n log n) | O(n) |
| Better | O(n²) | O(n) |
| Optimal | O(n log n) | O(1) |

## Edge Cases & Pitfalls
- Intervals that touch at an endpoint do not overlap.
- Greedy must sort by end time, not start time.
- Be careful with negative coordinates.

## Related
- Merge Intervals
- Meeting Rooms
