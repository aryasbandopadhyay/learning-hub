# 25. Contiguous Array

- **Difficulty:** Medium
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given a binary array `nums`, find the maximum length of a contiguous subarray containing the same number of `0`s and `1`s.

**Input**
- `nums`: a list containing only `0` and `1`.

**Output**
- The length of the longest balanced contiguous subarray.

## Constraints
- `1 <= nums.length <= 10^5`
- `nums[i]` is `0` or `1`.

## Examples
```text
Input: nums = [0,1]
Output: 2
Explanation: The whole array `[0,1]` has one zero and one one, so the longest balanced length is `2`.
```

## Understanding & Intuition
Treat 0 as -1 and 1 as +1. Equal zeros and ones means equal prefix balance at two indices; keep the earliest index for each balance.

## Approach 1 — Naive / Brute Force
**Idea:** Check every subarray and recount zeros and ones.
```python
class Solution:
    def findMaxLength(self, nums: list[int]) -> int:
        best = 0
        for l in range(len(nums)):
            for r in range(l, len(nums)):
                z = o = 0
                for i in range(l, r + 1):
                    if nums[i] == 0:
                        z += 1
                    else:
                        o += 1
                if z == o:
                    best = max(best, r - l + 1)
        return best
```
- **Time:** O(n^3) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Extend each start with running counts.
```python
class Solution:
    def findMaxLength(self, nums: list[int]) -> int:
        best = 0
        for l in range(len(nums)):
            z = o = 0
            for r in range(l, len(nums)):
                if nums[r] == 0:
                    z += 1
                else:
                    o += 1
                if z == o:
                    best = max(best, r - l + 1)
        return best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Store first index for each prefix balance.
```python
class Solution:
    def findMaxLength(self, nums: list[int]) -> int:
        first = {0: -1}
        bal = best = 0
        for i, x in enumerate(nums):
            bal += 1 if x == 1 else -1
            if bal in first:
                best = max(best, i - first[bal])
            else:
                first[bal] = i
        return best
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(1) |
| Better | O(n^2) | O(1) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Seed balance 0 at -1.
- Store earliest balance only.
- Input is binary.

## Related
- Subarray Sum Equals K
- Longest Consecutive Sequence
