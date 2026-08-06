# 07. Amount of New Area Painted Each Day

- **Difficulty:** Hard
- **Pattern:** intervals
- **Asked at:** Google, DoorDash, Meta

## Problem
You are given `paint`, where `paint[i] = [start, end]` means that on day `i` you paint the half-open interval `[start, end)`. Return a list where each element is the amount of new, previously unpainted area painted on that day.

Constraints: `1 <= len(paint) <= 10^5`, `0 <= start < end <= 10^5`.

## Examples
```text
Input: paint = [[1,4],[4,7],[2,5]]
Output: [3, 3, 0]
Explanation: The first two days paint [1,4) and [4,7); the third day is already covered on [2,5).
```

## Understanding & Intuition
The intervals are half-open, so unit segments such as `[x, x+1)` are the atomic painted pieces. A direct set of painted units is simple but can be slow. The optimal trick uses a disjoint-set next pointer to jump over already painted coordinates.

## Approach 1 — Naive / Brute Force
**Idea:** Store every already painted unit coordinate in a set and count newly inserted coordinates.
```python
class Solution:
    def amountPainted(self, paint: list[list[int]]) -> list[int]:
        seen = set()
        answer = []
        for start, end in paint:
            fresh = 0
            for x in range(start, end):
                if x not in seen:
                    seen.add(x)
                    fresh += 1
            answer.append(fresh)
        return answer
```
- **Time:** O(total length) — **Space:** O(U)

## Approach 2 — Better
**Idea:** Maintain sorted disjoint painted intervals, subtract overlaps, then merge the new interval into the painted union.
```python
class Solution:
    def amountPainted(self, paint: list[list[int]]) -> list[int]:
        from bisect import bisect_left
        intervals = []
        answer = []
        for start, end in paint:
            fresh = end - start
            i = bisect_left(intervals, [start, -1])
            if i > 0 and intervals[i - 1][1] >= start:
                i -= 1
            new_start, new_end = start, end
            j = i
            while j < len(intervals) and intervals[j][0] <= end:
                a, b = intervals[j]
                overlap = max(0, min(end, b) - max(start, a))
                fresh -= overlap
                new_start = min(new_start, a)
                new_end = max(new_end, b)
                j += 1
            intervals[i:j] = [[new_start, new_end]]
            answer.append(fresh)
        return answer
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a next-unpainted pointer; once coordinate `x` is painted, union it to `x + 1` so future days skip it.
```python
class Solution:
    def amountPainted(self, paint: list[list[int]]) -> list[int]:
        parent = {}

        def find(x):
            if x not in parent:
                parent[x] = x
                return x
            if parent[x] != x:
                parent[x] = find(parent[x])
            return parent[x]

        answer = []
        for start, end in paint:
            fresh = 0
            x = find(start)
            while x < end:
                fresh += 1
                parent[x] = find(x + 1)
                x = find(x)
            answer.append(fresh)
        return answer
```
- **Time:** O((U + n) alpha(U)) — **Space:** O(U)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(total length) | O(U) |
| Better | O(n^2) | O(n) |
| Optimal | O((U + n) alpha(U)) | O(U) |

## Edge Cases & Pitfalls
- Treat intervals as half-open; `[1,4)` has length 3.
- Fully covered days must append zero.
- When merging intervals, adjacent half-open intervals can be merged safely.

## Related
- Data Stream as Disjoint Intervals
- Merge Intervals
- Range Module
