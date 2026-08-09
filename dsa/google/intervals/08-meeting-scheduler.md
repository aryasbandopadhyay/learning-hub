# 08. Meeting Scheduler

- **Difficulty:** Medium
- **Pattern:** intervals
- **Asked at:** Google, LinkedIn, Amazon

## Problem
Given two people's available time slots `slots1` and `slots2`, where each slot is `[start, end]`, and a required `duration`, return the earliest time interval `[start, start + duration]` that works for both people. If no such interval exists, return an empty list.

Implement `Solution.minAvailableDuration` with the parameters below and return the requested value.

**Input**
- `slots1`: a `list[list[int]]`; the first person's available intervals.
- `slots2`: a `list[list[int]]`; the second person's available intervals.
- `duration`: a `int`; the required meeting duration.

**Output**
- The earliest valid interval `[start, start + duration]`, or an empty list if no such overlap exists.

## Constraints
- `1 <= len(slots1), len(slots2) <= 10^4`, `0 <= start < end <= 10^9`, `1 <= duration <= 10^6`

## Examples
```text
Input: slots1 = [[10,50],[60,120],[140,210]], slots2 = [[0,15],[60,70]], duration = 8
Output: [60, 68]
Explanation: The overlap [60,70] has length 10, so the earliest duration-8 meeting starts at 60.
```

## Understanding & Intuition
A valid meeting must lie in the intersection of one slot from each person. The earliest answer comes from considering intervals by start time and advancing the slot that ends first. Sorting both lists enables a linear two-pointer scan.

## Approach 1 — Naive / Brute Force
**Idea:** Compare every pair of slots and keep the earliest overlap with enough length.
```python
class Solution:
    def minAvailableDuration(self, slots1: list[list[int]], slots2: list[list[int]], duration: int) -> list[int]:
        best = None
        for a_start, a_end in slots1:
            for b_start, b_end in slots2:
                start = max(a_start, b_start)
                end = min(a_end, b_end)
                if end - start >= duration:
                    if best is None or start < best:
                        best = start
        return [] if best is None else [best, best + duration]
```
- **Time:** O(nm) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Sort all slots together and keep the farthest-ending previous slot for each person.
```python
class Solution:
    def minAvailableDuration(self, slots1: list[list[int]], slots2: list[list[int]], duration: int) -> list[int]:
        combined = []
        for start, end in slots1:
            if end - start >= duration:
                combined.append((start, end, 1))
        for start, end in slots2:
            if end - start >= duration:
                combined.append((start, end, 2))
        combined.sort()
        best_end = {1: -1, 2: -1}
        for start, end, person in combined:
            other = 3 - person
            if min(end, best_end[other]) - start >= duration:
                return [start, start + duration]
            best_end[person] = max(best_end[person], end)
        return []
```
- **Time:** O((n + m) log(n + m)) — **Space:** O(n + m)

## Approach 3 — Optimal
**Idea:** Sort each availability list and use two pointers, advancing the interval that ends earlier.
```python
class Solution:
    def minAvailableDuration(self, slots1: list[list[int]], slots2: list[list[int]], duration: int) -> list[int]:
        slots1.sort()
        slots2.sort()
        i = j = 0
        while i < len(slots1) and j < len(slots2):
            start = max(slots1[i][0], slots2[j][0])
            end = min(slots1[i][1], slots2[j][1])
            if end - start >= duration:
                return [start, start + duration]
            if slots1[i][1] < slots2[j][1]:
                i += 1
            else:
                j += 1
        return []
```
- **Time:** O(n log n + m log m) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nm) | O(1) |
| Better | O((n + m) log(n + m)) | O(n + m) |
| Optimal | O(n log n + m log m) | O(1) |

## Edge Cases & Pitfalls
- Return the earliest valid interval, not the longest overlap.
- Filter out slots shorter than `duration` only as an optimization.
- An overlap of exactly `duration` is valid.

## Related
- Interval List Intersections
- Meeting Rooms
- Employee Free Time
