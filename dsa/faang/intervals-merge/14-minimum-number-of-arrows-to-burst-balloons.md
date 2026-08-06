# 14. Minimum Number of Arrows to Burst Balloons

- **Difficulty:** Medium
- **Pattern:** interval merging & greedy
- **Asked at:** Meta, Amazon, Google

## Problem
Each balloon is represented by an inclusive horizontal interval `[start, end]`. One vertical arrow shot at coordinate `x` bursts every balloon with `start <= x <= end`. Return the minimum number of arrows needed to burst all balloons.

Constraints: `0 <= len(points) <= 10^5`, `-2^31 <= start <= end <= 2^31 - 1`.

## Examples
```text
Input: points = [[10,16],[2,8],[1,6],[7,12]]
Output: 2
Explanation: One arrow at 6 bursts [2,8] and [1,6], and one arrow at 12 bursts [10,16] and [7,12].
```

## Understanding & Intuition
This is the same as covering intervals with the fewest points. Choosing the end of the earliest-ending remaining balloon is always safe because it leaves maximum room for future balloons. Sorting makes it easy to find groups that can share one arrow.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly choose the smallest end among unburst balloons and mark every balloon containing that point.
```python
class Solution:
    def findMinArrowShots(self, points: list[list[int]]) -> int:
        n = len(points)
        burst = [False] * n
        remaining = n
        arrows = 0
        while remaining:
            arrow = None
            for i, (_, end) in enumerate(points):
                if not burst[i] and (arrow is None or end < arrow):
                    arrow = end
            arrows += 1
            for i, (start, end) in enumerate(points):
                if not burst[i] and start <= arrow <= end:
                    burst[i] = True
                    remaining -= 1
        return arrows
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort by start and maintain the common overlap of the current group of balloons.
```python
class Solution:
    def findMinArrowShots(self, points: list[list[int]]) -> int:
        if not points:
            return 0
        points = sorted(points)
        arrows = 1
        overlap_start, overlap_end = points[0]
        for start, end in points[1:]:
            if start <= overlap_end:
                overlap_start = max(overlap_start, start)
                overlap_end = min(overlap_end, end)
            else:
                arrows += 1
                overlap_start, overlap_end = start, end
        return arrows
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort by end and shoot a new arrow at an interval's end only when the previous arrow cannot burst it.
```python
class Solution:
    def findMinArrowShots(self, points: list[list[int]]) -> int:
        points = sorted(points, key=lambda p: (p[1], p[0]))
        arrows = 0
        last_arrow = None
        for start, end in points:
            if last_arrow is None or start > last_arrow:
                arrows += 1
                last_arrow = end
        return arrows
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Touching endpoints overlap because balloon intervals are inclusive.
- Negative coordinates are valid.
- An empty input needs zero arrows.

## Related
- Minimum Points to Cover Intervals
- Non-overlapping Intervals
