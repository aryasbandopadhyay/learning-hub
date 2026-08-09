# 05. Count Subarrays With Fixed Bounds

- **Difficulty:** Hard
- **Pattern:** Arrays & Prefix Sums
- **Asked at:** Google, Amazon, Bloomberg

## Problem
You are given an integer array `nums` and two integers `minK` and `maxK`.

A fixed-bound subarray is a non-empty contiguous subarray whose minimum value is exactly `minK` and whose maximum value is exactly `maxK`. Count all fixed-bound subarrays.

**Input**
- `nums`: a list of integers.
- `minK`: the required minimum value.
- `maxK`: the required maximum value.

**Output**
- The number of contiguous subarrays with minimum `minK` and maximum `maxK`.

## Constraints
- `1 <= nums.length <= 10^5`
- `1 <= nums[i], minK, maxK <= 10^6`

## Examples
```text
Input: nums = [1,3,5,2,7,5], minK = 1, maxK = 5
Output: 2
Explanation: The valid subarrays are `[1,3,5]` and `[1,3,5,2]`. Any subarray containing `7` is too large.
```

## Understanding & Intuition
Any value outside `[minK, maxK]` breaks a valid subarray. Within a valid segment, a subarray ending at the current index is valid once it contains both a latest `minK` and latest `maxK`. Count all starts after the last invalid index and before the earlier required bound.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate every subarray and track its minimum and maximum.
```python
class Solution:
    def countSubarrays(self, nums: list[int], minK: int, maxK: int) -> int:
        n = len(nums)
        ans = 0
        for i in range(n):
            lo = nums[i]
            hi = nums[i]
            for j in range(i, n):
                lo = min(lo, nums[j])
                hi = max(hi, nums[j])
                if lo == minK and hi == maxK:
                    ans += 1
        return ans
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Split around invalid values, then scan each valid segment for subarrays containing both bounds.
```python
class Solution:
    def countSubarrays(self, nums: list[int], minK: int, maxK: int) -> int:
        ans = 0
        start = 0
        n = len(nums)
        while start < n:
            while start < n and not (minK <= nums[start] <= maxK):
                start += 1
            end = start
            while end < n and minK <= nums[end] <= maxK:
                end += 1
            for i in range(start, end):
                seen_min = False
                seen_max = False
                for j in range(i, end):
                    if nums[j] == minK:
                        seen_min = True
                    if nums[j] == maxK:
                        seen_max = True
                    if seen_min and seen_max:
                        ans += 1
            start = end + 1
        return ans
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Track the last invalid index plus the latest positions of `minK` and `maxK`; each ending contributes valid start count.
```python
class Solution:
    def countSubarrays(self, nums: list[int], minK: int, maxK: int) -> int:
        ans = 0
        last_bad = -1
        last_min = -1
        last_max = -1
        for i, x in enumerate(nums):
            if x < minK or x > maxK:
                last_bad = i
            if x == minK:
                last_min = i
            if x == maxK:
                last_max = i
            ans += max(0, min(last_min, last_max) - last_bad)
        return ans
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n^2) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- `minK` may equal `maxK`; then every valid value equal to it can satisfy both bounds.
- Values outside the range reset possible starts.
- The answer can exceed 32-bit integer range.

## Related
- Subarrays with K Different Integers
- Fruit Into Baskets
