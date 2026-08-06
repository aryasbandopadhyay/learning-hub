# 05. Find Right Interval

- **Difficulty:** Medium
- **Pattern:** Advanced Binary Search
- **Asked at:** Google

## Problem
Given intervals `intervals` where each interval has a unique start, return a list `ans` where `ans[i]` is the index of the interval with the smallest start point greater than or equal to `intervals[i][1]`. If no such interval exists, use `-1`.

Constraints: `1 <= len(intervals) <= 20000`, `len(intervals[i]) == 2`, `-10^6 <= start <= end <= 10^6`, starts are unique.

## Examples
```text
Input: intervals = [[3,4],[2,3],[1,2]]
Output: [-1,0,1]
Explanation: Ends 4, 3, and 2 map to starts none, interval 0, and interval 1.
```

## Understanding & Intuition
Only interval starts matter for choosing a right interval. Sorting starts creates an ordered search space where each end asks for a lower bound.

## Approach 1 — Naive / Brute Force
**Idea:** For every interval, scan all starts and keep the smallest valid one.
```python
class Solution:
    def findRightInterval(self, intervals: list[list[int]]) -> list[int]:
        ans = []
        for _, end in intervals:
            best_start = None
            best_idx = -1
            for j, pair in enumerate(intervals):
                start = pair[0]
                if start >= end and (best_start is None or start < best_start):
                    best_start = start
                    best_idx = j
            ans.append(best_idx)
        return ans
```
- **Time:** O(n^2) — **Space:** O(1) excluding output

## Approach 2 — Better
**Idea:** Sort starts and use `bisect_left` for each interval end.
```python
class Solution:
    def findRightInterval(self, intervals: list[list[int]]) -> list[int]:
        import bisect
        starts = sorted((iv[0], i) for i, iv in enumerate(intervals))
        only_starts = [x for x, _ in starts]
        ans = []
        for _, end in intervals:
            pos = bisect.bisect_left(only_starts, end)
            ans.append(starts[pos][1] if pos < len(starts) else -1)
        return ans
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Implement the same lower-bound search manually to avoid depending on library behavior.
```python
class Solution:
    def findRightInterval(self, intervals: list[list[int]]) -> list[int]:
        starts = sorted((iv[0], i) for i, iv in enumerate(intervals))
        ans = []
        for _, end in intervals:
            lo, hi = 0, len(starts)
            while lo < hi:
                mid = (lo + hi) // 2
                if starts[mid][0] < end:
                    lo = mid + 1
                else:
                    hi = mid
            ans.append(starts[lo][1] if lo < len(starts) else -1)
        return ans
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Start points are unique, but intervals may have equal ends.
- An interval can be its own right interval when `start == end`.
- Return original indexes, not sorted positions.

## Related
- Insert Interval
- Merge Intervals
