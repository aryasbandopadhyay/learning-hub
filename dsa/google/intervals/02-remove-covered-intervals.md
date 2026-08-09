# 02. Remove Covered Intervals

- **Difficulty:** Medium
- **Pattern:** intervals
- **Asked at:** Google, Amazon, Bloomberg

## Problem
Given intervals `intervals`, remove every interval `[a, b]` that is covered by another interval `[c, d]`, meaning `c <= a` and `b <= d`. Return the number of intervals remaining.

Implement `Solution.removeCoveredIntervals` with the parameters below and return the requested value.

**Input**
- `intervals`: a `list[list[int]]`; the intervals described above.

**Output**
- A `int` value representing the result described above.

## Constraints
- `1 <= len(intervals) <= 10^4`, `0 <= start < end <= 10^5`

## Examples
```text
Input: intervals = [[1,4],[3,6],[2,8]]
Output: 2
Explanation: [3,6] is covered by [2,8], so [1,4] and [2,8] remain.
```

## Understanding & Intuition
Coverage is easiest to detect after placing intervals with earlier starts first. For equal starts, the longest interval should appear first so shorter duplicates are immediately covered. Then an interval survives exactly when its end extends farther than every previous end.

## Approach 1 — Naive / Brute Force
**Idea:** For each interval, scan all other intervals and test if any one covers it.
```python
class Solution:
    def removeCoveredIntervals(self, intervals: list[list[int]]) -> int:
        remaining = 0
        for i, (a, b) in enumerate(intervals):
            covered = False
            for j, (c, d) in enumerate(intervals):
                if i != j and c <= a and b <= d and (c < a or b < d or j < i):
                    covered = True
                    break
            if not covered:
                remaining += 1
        return remaining
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Sort by start and decreasing end, then mark intervals whose end is not beyond the best end seen.
```python
class Solution:
    def removeCoveredIntervals(self, intervals: list[list[int]]) -> int:
        ordered = sorted(intervals, key=lambda x: (x[0], -x[1]))
        covered = [False] * len(ordered)
        farthest = -1
        for i, (_, end) in enumerate(ordered):
            if end <= farthest:
                covered[i] = True
            else:
                farthest = end
        return covered.count(False)
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use the same ordering but count survivors directly with one variable.
```python
class Solution:
    def removeCoveredIntervals(self, intervals: list[list[int]]) -> int:
        intervals.sort(key=lambda x: (x[0], -x[1]))
        remaining = 0
        farthest = -1
        for _, end in intervals:
            if end > farthest:
                remaining += 1
                farthest = end
        return remaining
```
- **Time:** O(n log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n log n) | O(1) |

## Edge Cases & Pitfalls
- For equal starts, sort longer intervals before shorter intervals.
- Identical intervals should be counted once, so break ties consistently.
- Do not merge intervals; coverage is different from overlap.

## Related
- Merge Intervals
- Non-overlapping Intervals
- Insert Interval
