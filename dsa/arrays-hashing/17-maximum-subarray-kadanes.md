# 17. Maximum Subarray (Kadane's)

- **Difficulty:** Medium
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given an integer array `nums`, find the maximum possible sum of a non-empty contiguous subarray.

**Input**
- `nums`: a non-empty list of integers.

**Output**
- The largest sum over all contiguous non-empty subarrays.

## Constraints
- `1 <= nums.length <= 10^5`
- `-10^4 <= nums[i] <= 10^4`

## Examples
```text
Input: nums = [-2,1,-3,4,-1,2,1,-5,4]
Output: 6
Explanation: The best subarray is `[4,-1,2,1]`, whose sum is `6`; every other contiguous choice has sum at most `6`.
```

## Understanding & Intuition
A negative prefix should be dropped before future growth. Kadane tracks the best subarray ending here and the best seen anywhere.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate subarrays and recompute sums.
```python
class Solution:
    def maxSubArray(self, nums: list[int]) -> int:
        best = nums[0]
        for l in range(len(nums)):
            for r in range(l, len(nums)):
                total = 0
                for i in range(l, r+1):
                    total += nums[i]
                best = max(best, total)
        return best
```
- **Time:** O(n^3) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Extend each start with a running sum.
```python
class Solution:
    def maxSubArray(self, nums: list[int]) -> int:
        best = nums[0]
        for l in range(len(nums)):
            total = 0
            for r in range(l, len(nums)):
                total += nums[r]
                best = max(best, total)
        return best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Kadane: extend previous sum or start at current value.
```python
class Solution:
    def maxSubArray(self, nums: list[int]) -> int:
        current = best = nums[0]
        for x in nums[1:]:
            current = max(x, current + x)
            best = max(best, current)
        return best
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(1) |
| Better | O(n^2) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Subarray is non-empty.
- All-negative arrays return the largest element.
- Initialize from nums[0].

## Related
- Maximum Product Subarray
- Best Time to Buy and Sell Stock
