# 12. Non-overlapping Intervals

- **Difficulty:** Medium
- **Pattern:** Greedy
- **Asked at:** Google, Amazon, Microsoft, Meta

## Problem
Given a list of intervals, remove the fewest intervals so that the remaining intervals do not
overlap. Intervals that only touch at an endpoint, such as `[1,2]` and `[2,3]`, do not overlap.

**Input**
- `intervals`: a list of intervals `[start, end]`.

**Output**
- An integer: the minimum number of intervals to remove.

## Constraints
- 1 <= intervals.length <= 10^5
- intervals[i].length == 2
- -5 * 10^4 <= start < end <= 5 * 10^4

## Examples
```text
Input: intervals = [[1,2],[2,3],[3,4],[1,3]]
Output: 1
Explanation: Removing `[1,3]` leaves `[1,2]`, `[2,3]`, and `[3,4]`, which do not overlap.
```

## Understanding & Intuition
Keeping the interval with the earliest end leaves the most space for future intervals. Therefore, maximizing the number of non-overlapping intervals by end time is safe. The minimum removals equal total intervals minus kept intervals.

## Approach 1 — Naive / Brute Force
**Idea:** Try every subset, keep valid non-overlapping subsets, and minimize removals.
```python
from typing import List

class Solution:
    def eraseOverlapIntervals(self, intervals: List[List[int]]) -> int:
        n = len(intervals)
        best_keep = 0
        for mask in range(1 << n):
            chosen = [intervals[i] for i in range(n) if mask & (1 << i)]
            chosen.sort()
            valid = all(chosen[i][0] >= chosen[i - 1][1] for i in range(1, len(chosen)))
            if valid:
                best_keep = max(best_keep, len(chosen))
        return n - best_keep
```
- **Time:** O(2^n * n log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort by start and greedily remove the interval with the larger end when overlap appears.
```python
from typing import List

class Solution:
    def eraseOverlapIntervals(self, intervals: List[List[int]]) -> int:
        intervals.sort()
        removals = 0
        current_end = intervals[0][1]

        for start, end in intervals[1:]:
            if start < current_end:
                removals += 1
                current_end = min(current_end, end)
            else:
                current_end = end
        return removals
```
- **Time:** O(n log n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Sort by end and count how many intervals can be kept.
```python
from typing import List

class Solution:
    def eraseOverlapIntervals(self, intervals: List[List[int]]) -> int:
        intervals.sort(key=lambda x: x[1])
        kept = 0
        end = float("-inf")

        for start, finish in intervals:
            if start >= end:
                kept += 1
                end = finish

        return len(intervals) - kept
```
- **Time:** O(n log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n * n log n) | O(n) |
| Better | O(n log n) | O(1) |
| Optimal | O(n log n) | O(1) |

## Edge Cases & Pitfalls
- `[1,2]` and `[2,3]` are non-overlapping.
- When sorting by start, keep the smaller end during an overlap.
- The problem asks for removals, not the kept count.

## Related
- Minimum Number of Arrows to Burst Balloons
- Activity Selection
