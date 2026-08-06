# 16. Check if All the Integers in a Range Are Covered

- **Difficulty:** Easy
- **Pattern:** interval coverage
- **Asked at:** Amazon, Google, Microsoft

## Problem
Given inclusive integer ranges `ranges` and two integers `left` and `right`, return `True` if every integer from `left` through `right` is covered by at least one range. Otherwise, return `False`.

Constraints: `1 <= len(ranges) <= 50`, `1 <= start <= end <= 50`, and `1 <= left <= right <= 50`.

## Examples
```text
Input: ranges = [[1,2],[3,4],[5,6]], left = 2, right = 5
Output: True
Explanation: The integers 2, 3, 4, and 5 are each covered by at least one range.
```

## Understanding & Intuition
The domain is small, so direct coverage checks are easy. A difference array records how many ranges cover each integer after prefix summing. Sorting and merging can also determine whether the target interval is fully covered without checking every range repeatedly.

## Approach 1 — Naive / Brute Force
**Idea:** For each target integer, scan all ranges until one covers it.
```python
class Solution:
    def isCovered(self, ranges: list[list[int]], left: int, right: int) -> bool:
        for x in range(left, right + 1):
            found = False
            for start, end in ranges:
                if start <= x <= end:
                    found = True
                    break
            if not found:
                return False
        return True
```
- **Time:** O(nw) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Sort ranges, merge only the part that can cover the requested interval, and watch for gaps.
```python
class Solution:
    def isCovered(self, ranges: list[list[int]], left: int, right: int) -> bool:
        need = left
        for start, end in sorted(ranges):
            if end < need:
                continue
            if start > need:
                return False
            need = max(need, end + 1)
            if need > right:
                return True
        return False
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a difference array over the small coordinate range to compute coverage counts.
```python
class Solution:
    def isCovered(self, ranges: list[list[int]], left: int, right: int) -> bool:
        diff = [0] * 52
        for start, end in ranges:
            diff[start] += 1
            diff[end + 1] -= 1
        active = 0
        for x in range(1, 51):
            active += diff[x]
            if left <= x <= right and active == 0:
                return False
        return True
```
- **Time:** O(n + 50) — **Space:** O(50)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nw) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n + 50) | O(50) |

## Edge Cases & Pitfalls
- Ranges are inclusive, so `end + 1` is where coverage stops in the difference array.
- Adjacent ranges such as `[1,2]` and `[3,4]` cover without a gap.
- Return as soon as any integer in `[left, right]` has zero coverage.

## Related
- Range Addition
- Describe the Painting
