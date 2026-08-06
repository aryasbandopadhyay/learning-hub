# 09. Remove Interval

- **Difficulty:** Medium
- **Pattern:** intervals
- **Asked at:** Google, Facebook, Bloomberg

## Problem
You are given a sorted list of non-overlapping intervals `intervals` and one interval `toBeRemoved`. Remove the half-open range `toBeRemoved` from every interval and return the remaining intervals in sorted order.

Constraints: `0 <= len(intervals) <= 10^4`, `intervals[i] = [start, end]`, intervals are sorted and non-overlapping, and `0 <= start < end <= 10^9`.

## Examples
```text
Input: intervals = [[0,2],[3,4],[5,7]], toBeRemoved = [1,6]
Output: [[0, 1], [6, 7]]
Explanation: The removal cuts the tail of [0,2], removes [3,4], and cuts the head of [5,7].
```

## Understanding & Intuition
Each input interval can be unaffected, fully removed, or split into left and right leftovers. Since input intervals are already sorted and disjoint, handling each independently preserves sorted output. The half-open convention makes boundaries simple.

## Approach 1 — Naive / Brute Force
**Idea:** Case-split each interval into unaffected, left-trimmed, right-trimmed, split, or fully removed.
```python
class Solution:
    def removeInterval(self, intervals: list[list[int]], toBeRemoved: list[int]) -> list[list[int]]:
        remove_start, remove_end = toBeRemoved
        answer = []
        for start, end in intervals:
            if end <= remove_start or start >= remove_end:
                answer.append([start, end])
            elif start < remove_start and remove_end < end:
                answer.append([start, remove_start])
                answer.append([remove_end, end])
            elif start < remove_start:
                answer.append([start, remove_start])
            elif remove_end < end:
                answer.append([remove_end, end])
        return answer
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** For each interval, compute its intersection with the removal range and add any leftovers.
```python
class Solution:
    def removeInterval(self, intervals: list[list[int]], toBeRemoved: list[int]) -> list[list[int]]:
        remove_start, remove_end = toBeRemoved
        answer = []
        for start, end in intervals:
            if end <= remove_start or remove_end <= start:
                answer.append([start, end])
            else:
                if start < remove_start:
                    answer.append([start, remove_start])
                if remove_end < end:
                    answer.append([remove_end, end])
        return answer
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Skip intervals ending before the removal, process overlapping intervals once, then append the untouched suffix.
```python
class Solution:
    def removeInterval(self, intervals: list[list[int]], toBeRemoved: list[int]) -> list[list[int]]:
        remove_start, remove_end = toBeRemoved
        answer = []
        i = 0
        while i < len(intervals) and intervals[i][1] <= remove_start:
            answer.append(intervals[i][:])
            i += 1
        while i < len(intervals) and intervals[i][0] < remove_end:
            start, end = intervals[i]
            if start < remove_start:
                answer.append([start, remove_start])
            if remove_end < end:
                answer.append([remove_end, end])
            i += 1
        while i < len(intervals):
            answer.append(intervals[i][:])
            i += 1
        return answer
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Do not output zero-length intervals.
- Boundaries equal to the removal start or end are kept correctly with half-open intervals.
- The removal range may not overlap any input interval.

## Related
- Insert Interval
- Merge Intervals
- Interval List Intersections
