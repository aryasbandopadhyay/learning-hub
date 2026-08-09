# 12. Minimum Points to Cover Intervals

- **Difficulty:** Medium
- **Pattern:** interval merging & sweep line
- **Asked at:** Google, Amazon, Microsoft

## Problem
Given inclusive intervals `intervals`, return the canonical greedy list of integer points formed by repeatedly choosing the end of the earliest-finishing uncovered interval. This list has the minimum possible number of points and covers every interval.

**Input**
- `intervals`: a `list[list[int]]`; the intervals.

**Output**
- A `list[int]`. Return the canonical greedy list of integer points formed by repeatedly choosing the end of the earliest-finishing uncovered interval. This judge compares the sequence exactly: return the greedy points in the order they are chosen after sorting intervals by end coordinate.

## Constraints
- `1 <= len(intervals) <= 10^5`, `0 <= start <= end <= 10^9`.

## Examples
```text
Input: intervals = [[1,3],[2,5],[3,6],[7,9]]
Output: [3,9]
Explanation: Point 3 covers the first three intervals, and point 9 covers [7,9]. The output is written in the required deterministic order.
```

## Understanding & Intuition
Choosing a point at the right end of the earliest-finishing uncovered interval covers as many upcoming intervals as possible. This classic greedy strategy gives the minimum number of points. Defining the output as this greedy list makes the answer deterministic.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly find the earliest-ending uncovered interval and choose its end point.
```python
class Solution:
    def minimumCoverPoints(self, intervals: list[list[int]]) -> list[int]:
        covered = [False] * len(intervals)
        ans = []
        remaining = len(intervals)
        while remaining:
            best_end = None
            for i, (s, e) in enumerate(intervals):
                if not covered[i] and (best_end is None or e < best_end):
                    best_end = e
            ans.append(best_end)
            for i, (s, e) in enumerate(intervals):
                if not covered[i] and s <= best_end <= e:
                    covered[i] = True
                    remaining -= 1
        return ans
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort intervals by start and maintain the current common intersection; emit a point when the next interval no longer intersects it.
```python
class Solution:
    def minimumCoverPoints(self, intervals: list[list[int]]) -> list[int]:
        intervals = sorted(intervals)
        ans = []
        cur_l, cur_r = intervals[0]
        for s, e in intervals[1:]:
            if s <= cur_r:
                cur_l = max(cur_l, s)
                cur_r = min(cur_r, e)
            else:
                ans.append(cur_r)
                cur_l, cur_r = s, e
        ans.append(cur_r)
        return ans
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort by end and greedily choose an interval's end whenever it is not already covered by the last chosen point.
```python
class Solution:
    def minimumCoverPoints(self, intervals: list[list[int]]) -> list[int]:
        intervals = sorted(intervals, key=lambda x: (x[1], x[0]))
        ans = []
        last = None
        for s, e in intervals:
            if last is None or last < s:
                last = e
                ans.append(last)
        return ans
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Intervals are inclusive.
- Sorting by end is the usual proof of optimality.
- Returning a canonical list avoids nondeterministic valid answers.

## Related
- Minimum Number of Arrows to Burst Balloons
- Non-overlapping Intervals
