# 08. Longest Consecutive Sequence

- **Difficulty:** Medium
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Return the length of the longest run of consecutive integer values in an unsorted array. Optimal solution should be `O(n)`.

## Examples
```text
Input: nums = [100,4,200,1,3,2]
Output: 4
Explanation: The longest run is 1,2,3,4.
```

## Understanding & Intuition
Order in the array does not matter; membership does. A set lets us expand only from sequence starts where `x-1` is absent.

## Approach 1 — Naive / Brute Force
**Idea:** For every number, search the list for each next value.
```python
class Solution:
    def longestConsecutive(self, nums: list[int]) -> int:
        best = 0
        for x in nums:
            cur, length = x, 1
            while cur + 1 in nums:
                cur += 1
                length += 1
            best = max(best, length)
        return best
```
- **Time:** O(n^3) worst — **Space:** O(1)

## Approach 2 — Better
**Idea:** Sort unique values and count adjacent runs.
```python
class Solution:
    def longestConsecutive(self, nums: list[int]) -> int:
        if not nums:
            return 0
        vals = sorted(set(nums))
        best = length = 1
        for i in range(1, len(vals)):
            if vals[i] == vals[i-1] + 1:
                length += 1
            else:
                length = 1
            best = max(best, length)
        return best
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Expand in a set only from numbers that start a run.
```python
class Solution:
    def longestConsecutive(self, nums: list[int]) -> int:
        vals = set(nums)
        best = 0
        for x in vals:
            if x - 1 in vals:
                continue
            y = x
            while y + 1 in vals:
                y += 1
            best = max(best, y - x + 1)
        return best
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) worst | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Empty input returns 0.
- Duplicates should not lengthen runs.
- Only expand from starts.

## Related
- Missing Number
- Longest Increasing Subsequence
