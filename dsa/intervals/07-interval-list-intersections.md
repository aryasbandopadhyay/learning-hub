# 07. Interval List Intersections

- **Difficulty:** Medium
- **Pattern:** Intervals
- **Asked at:** Google, Amazon, Meta, Microsoft

## Problem
Given two sorted lists of pairwise disjoint closed intervals, return every intersection between one interval from each list. A shared endpoint counts as an interval such as `[5,5]`.

**Input**
- `firstList`: sorted non-overlapping intervals.
- `secondList`: sorted non-overlapping intervals.

**Output**
- All intersections from left to right. This judge compares exactly, so return intersections in ascending order as encountered.

## Constraints
- `0 <= firstList.length, secondList.length <= 1000`
- `firstList[i].length == secondList[j].length == 2`
- `0 <= start <= end <= 10^9`
- Each input list is sorted and non-overlapping.

## Examples
```text
Input: firstList = [[0,2],[5,10],[13,23],[24,25]], secondList = [[1,5],[8,12],[15,24],[25,26]]
Output: [[1,2],[5,5],[8,10],[15,23],[24,24],[25,25]]
Explanation: Each output is the overlap of one interval from each list; shared endpoints like `5` and `25` are included.
```

## Understanding & Intuition
The intersection of two intervals is `[max(starts), min(ends)]` if the start is not after the end. Because both lists are sorted and internally disjoint, advancing the interval that ends first cannot miss any future intersection.

## Approach 1 — Naive / Brute Force
**Idea:** Compare every interval in the first list with every interval in the second list.
```python
from typing import List

class Solution:
    def intervalIntersection(self, firstList: List[List[int]], secondList: List[List[int]]) -> List[List[int]]:
        result = []
        for a_start, a_end in firstList:
            for b_start, b_end in secondList:
                start = max(a_start, b_start)
                end = min(a_end, b_end)
                if start <= end:
                    result.append([start, end])
        return sorted(result)
```
- **Time:** O(mn + k log k) — **Space:** O(k)

## Approach 2 — Better
**Idea:** For each interval in the smaller list, scan forward in the larger sorted list until starts become too large.
```python
from typing import List

class Solution:
    def intervalIntersection(self, firstList: List[List[int]], secondList: List[List[int]]) -> List[List[int]]:
        result = []
        j = 0

        for a_start, a_end in firstList:
            while j < len(secondList) and secondList[j][1] < a_start:
                j += 1
            t = j
            while t < len(secondList) and secondList[t][0] <= a_end:
                start = max(a_start, secondList[t][0])
                end = min(a_end, secondList[t][1])
                if start <= end:
                    result.append([start, end])
                t += 1
        return result
```
- **Time:** O(m + k) when lists are disjoint internally — **Space:** O(k)

## Approach 3 — Optimal
**Idea:** Use two pointers and always advance the interval that finishes first.
```python
from typing import List

class Solution:
    def intervalIntersection(self, firstList: List[List[int]], secondList: List[List[int]]) -> List[List[int]]:
        i = j = 0
        result = []

        while i < len(firstList) and j < len(secondList):
            start = max(firstList[i][0], secondList[j][0])
            end = min(firstList[i][1], secondList[j][1])
            if start <= end:
                result.append([start, end])

            if firstList[i][1] < secondList[j][1]:
                i += 1
            else:
                j += 1
        return result
```
- **Time:** O(m + n) — **Space:** O(k)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn + k log k) | O(k) |
| Better | O(m + k) typical for disjoint sorted lists | O(k) |
| Optimal | O(m + n) | O(k) |

## Edge Cases & Pitfalls
- Single-point intersections like `[5,5]` are valid.
- Empty input list gives an empty result.
- Advance only the interval with the smaller end.

## Related
- Insert Interval
- Merge Intervals
