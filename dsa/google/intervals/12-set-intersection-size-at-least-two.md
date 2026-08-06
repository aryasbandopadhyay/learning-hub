# 12. Set Intersection Size At Least Two

- **Difficulty:** Hard
- **Pattern:** intervals
- **Asked at:** Google, Airbnb, Amazon

## Problem
Given intervals `intervals`, choose the smallest possible set of integers such that every interval contains at least two chosen integers. Return the size of that set.

Constraints: `1 <= len(intervals) <= 3000`, `0 <= start < end <= 10^8`.

## Examples
```text
Input: intervals = [[1,3],[1,4],[2,5],[3,5]]
Output: 3
Explanation: The set {2,3,4} intersects every interval in at least two points.
```

## Understanding & Intuition
To minimize future damage, process intervals by increasing end and place required points as far right as possible. If an interval already has two chosen points, add none; if it has one, add the largest missing endpoint; otherwise add the two largest points in the interval. Sorting ties by larger start first avoids wasting points.

## Approach 1 — Naive / Brute Force
**Idea:** For small coordinate ranges, try all subsets in increasing size and return the first one satisfying every interval.
```python
class Solution:
    def intersectionSizeTwo(self, intervals: list[list[int]]) -> int:
        intervals.sort(key=lambda x: (x[1], -x[0]))
        chosen = []
        for start, end in intervals:
            have = sum(1 for point in chosen if start <= point <= end)
            for point in range(end, start - 1, -1):
                if have >= 2:
                    break
                if point not in chosen:
                    chosen.append(point)
                    have += 1
        return len(chosen)
```
- **Time:** O(n^2 + nW) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Greedily maintain the explicit chosen set and count how many chosen points each interval already contains.
```python
class Solution:
    def intersectionSizeTwo(self, intervals: list[list[int]]) -> int:
        intervals.sort(key=lambda x: (x[1], -x[0]))
        chosen = []
        for start, end in intervals:
            have = 0
            for point in chosen:
                if start <= point <= end:
                    have += 1
            point = end
            while have < 2:
                if point >= start and point not in chosen:
                    chosen.append(point)
                    have += 1
                point -= 1
        return len(chosen)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Track only the two largest chosen points relevant to processed intervals, adding one or two rightmost points as needed.
```python
class Solution:
    def intersectionSizeTwo(self, intervals: list[list[int]]) -> int:
        intervals.sort(key=lambda x: (x[1], -x[0]))
        first = -1
        second = -1
        answer = 0
        for start, end in intervals:
            if start <= first:
                continue
            if start <= second:
                answer += 1
                first, second = second, end
            else:
                answer += 2
                first, second = end - 1, end
        return answer
```
- **Time:** O(n log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2 + nW) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n log n) | O(1) |

## Edge Cases & Pitfalls
- Intervals are inclusive, and each interval length is at least two integer points because `start < end`.
- Sort by end ascending, then start descending.
- The two tracked points should represent the largest chosen points, ordered as `first < second`.

## Related
- Non-overlapping Intervals
- Minimum Number of Arrows to Burst Balloons
- Maximum Number of Events That Can Be Attended
