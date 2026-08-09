# 13. Interval List Intersections

- **Difficulty:** Medium
- **Pattern:** interval merging & sweep line
- **Asked at:** Google, Meta, Amazon

## Problem
Given two lists of closed intervals, `firstList` and `secondList`, where each list is sorted by start time and has pairwise disjoint intervals, return their intersections. An intersection of `[a, b]` and `[c, d]` exists when `max(a, c) <= min(b, d)`.

**Input**
- `firstList`: a `list[list[int]]`; the first sorted interval list.
- `secondList`: a `list[list[int]]`; the second sorted interval list.

**Output**
- A `list[list[int]]`. Return their intersections. The judge compares the collection as a set, so equivalent ordering is accepted unless otherwise stated.

## Constraints
- `0 <= len(firstList), len(secondList) <= 1000`, `0 <= start <= end <= 10^9`, and intervals in each list are sorted and non-overlapping.

## Examples
```text
Input: firstList = [[0,2],[5,10],[13,23],[24,25]], secondList = [[1,5],[8,12],[15,24],[25,26]]
Output: [[1,2],[5,5],[8,10],[15,23],[24,24],[25,25]]
Explanation: Each output interval is the overlap between one interval from each input list.
```

## Understanding & Intuition
Two closed intervals overlap when the later start is not after the earlier end. Because each input list is already sorted and internally disjoint, intersections also appear in sorted order. The most efficient approach advances the pointer whose interval ends first.

## Approach 1 — Naive / Brute Force
**Idea:** Compare every interval from the first list with every interval from the second list.
```python
class Solution:
    def intervalIntersection(self, firstList: list[list[int]], secondList: list[list[int]]) -> list[list[int]]:
        ans = []
        for a, b in firstList:
            for c, d in secondList:
                start = max(a, c)
                end = min(b, d)
                if start <= end:
                    ans.append([start, end])
        ans.sort()
        return ans
```
- **Time:** O(mn + k log k) — **Space:** O(k) besides output

## Approach 2 — Better
**Idea:** Sweep all interval endpoints and record spans where one interval from each list is active.
```python
class Solution:
    def intervalIntersection(self, firstList: list[list[int]], secondList: list[list[int]]) -> list[list[int]]:
        events = []
        for start, end in firstList:
            events.append((start, 0, 0))
            events.append((end, 1, 0))
        for start, end in secondList:
            events.append((start, 0, 1))
            events.append((end, 1, 1))
        events.sort()

        active = [0, 0]
        ans = []
        current = None
        i = 0
        while i < len(events):
            x = events[i][0]
            j = i
            while j < len(events) and events[j][0] == x and events[j][1] == 0:
                active[events[j][2]] += 1
                j += 1
            if active[0] and active[1] and current is None:
                current = x
            while j < len(events) and events[j][0] == x:
                if active[0] and active[1] and current is not None:
                    ans.append([current, x])
                    current = None
                active[events[j][2]] -= 1
                j += 1
            i = j
        return ans
```
- **Time:** O((m + n) log(m + n)) — **Space:** O(m + n)

## Approach 3 — Optimal
**Idea:** Use two pointers; after checking the current pair, advance the interval that ends first.
```python
class Solution:
    def intervalIntersection(self, firstList: list[list[int]], secondList: list[list[int]]) -> list[list[int]]:
        i = 0
        j = 0
        ans = []
        while i < len(firstList) and j < len(secondList):
            start = max(firstList[i][0], secondList[j][0])
            end = min(firstList[i][1], secondList[j][1])
            if start <= end:
                ans.append([start, end])
            if firstList[i][1] < secondList[j][1]:
                i += 1
            else:
                j += 1
        return ans
```
- **Time:** O(m + n) — **Space:** O(1) besides output

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn + k log k) | O(k) |
| Better | O((m + n) log(m + n)) | O(m + n) |
| Optimal | O(m + n) | O(1) |

## Edge Cases & Pitfalls
- Endpoints are inclusive, so `[5,5]` is a valid intersection.
- Either input list may be empty.
- Advance only the interval with the smaller end in the two-pointer method.

## Related
- Merge Intervals
- Insert Interval
