# 11. Minimum Number of Arrows to Burst Balloons

- **Difficulty:** Medium
- **Pattern:** Greedy
- **Asked at:** Amazon, Google, Microsoft, Bloomberg

## Problem
Given intervals `points`, where `points[i] = [start, end]` is the horizontal diameter of a balloon, one vertical arrow at coordinate `x` bursts every balloon with `start <= x <= end`. Return the minimum number of arrows needed. Constraints: `1 <= len(points) <= 10^5`, `-2^31 <= start < end <= 2^31 - 1`.

## Examples
```text
Input: points = [[10,16],[2,8],[1,6],[7,12]]
Output: 2
Explanation: Shoot one arrow at x = 6 and another at x = 12.
```

## Understanding & Intuition
To maximize reuse of an arrow, shoot it at the end of the earliest-ending balloon. This leaves the most room to overlap with future balloons. If the next balloon starts after that arrow, a new arrow is unavoidable.

## Approach 1 — Naive / Brute Force
**Idea:** Try every balloon endpoint as a possible arrow position and recursively cover remaining balloons.
```python
from functools import lru_cache
from typing import List, Tuple

class Solution:
    def findMinArrowShots(self, points: List[List[int]]) -> int:
        intervals = tuple((a, b) for a, b in points)
        candidates = tuple(sorted({b for _, b in intervals}))

        @lru_cache(None)
        def solve(remaining: Tuple[Tuple[int, int], ...]) -> int:
            if not remaining:
                return 0
            best = len(remaining)
            for x in candidates:
                next_remaining = tuple((a, b) for a, b in remaining if not (a <= x <= b))
                if len(next_remaining) < len(remaining):
                    best = min(best, 1 + solve(next_remaining))
            return best

        return solve(intervals)
```
- **Time:** O(n * 2^n) — **Space:** O(2^n)

## Approach 2 — Better
**Idea:** Sort by start and maintain the current overlapping window.
```python
from typing import List

class Solution:
    def findMinArrowShots(self, points: List[List[int]]) -> int:
        points.sort()
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
- **Time:** O(n log n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Sort by end coordinate and place arrows greedily at interval ends.
```python
from typing import List

class Solution:
    def findMinArrowShots(self, points: List[List[int]]) -> int:
        points.sort(key=lambda p: p[1])
        arrows = 0
        arrow_x = None

        for start, end in points:
            if arrow_x is None or start > arrow_x:
                arrows += 1
                arrow_x = end
        return arrows
```
- **Time:** O(n log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * 2^n) | O(2^n) |
| Better | O(n log n) | O(1) |
| Optimal | O(n log n) | O(1) |

## Edge Cases & Pitfalls
- Touching intervals overlap because endpoints are inclusive.
- Sorting by end simplifies the proof and implementation.
- Do not create a new arrow when `start == arrow_x`.

## Related
- Non-overlapping Intervals
- Meeting Rooms
