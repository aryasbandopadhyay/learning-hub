# 08. Maximum Subarray

- **Difficulty:** Medium
- **Pattern:** Greedy
- **Asked at:** Amazon, Microsoft, Google, Meta, Apple

## Problem
Given an integer array `nums`, find the contiguous non-empty subarray with the largest possible sum.
Return that sum.

**Input**
- `nums`: a list of integers.

**Output**
- An integer: the maximum subarray sum.

## Constraints
- 1 <= nums.length <= 10^5
- -10^4 <= nums[i] <= 10^4

## Examples
```text
Input: nums = [-2,1,-3,4,-1,2,1,-5,4]
Output: 6
Explanation: The best subarray is `[4,-1,2,1]`, whose sum is `6`.
```

## Understanding & Intuition
A negative running sum hurts any future subarray, so it is safe to discard it and restart. The greedy choice is to either extend the previous subarray or start at the current element. This is Kadane's algorithm.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate every subarray and compute its sum.
```python
from typing import List

class Solution:
    def maxSubArray(self, nums: List[int]) -> int:
        best = nums[0]
        for left in range(len(nums)):
            for right in range(left, len(nums)):
                best = max(best, sum(nums[left:right + 1]))
        return best
```
- **Time:** O(n^3) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Fix the left endpoint and extend the running sum to the right.
```python
from typing import List

class Solution:
    def maxSubArray(self, nums: List[int]) -> int:
        best = nums[0]
        for left in range(len(nums)):
            current = 0
            for right in range(left, len(nums)):
                current += nums[right]
                best = max(best, current)
        return best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Keep the best subarray ending at the current index and the best seen overall.
```python
from typing import List

class Solution:
    def maxSubArray(self, nums: List[int]) -> int:
        current = nums[0]
        best = nums[0]

        for value in nums[1:]:
            current = max(value, current + value)
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
- All-negative arrays should return the largest single element.
- The subarray must be non-empty.
- Do not initialize the answer to `0`.

## Related
- Best Time to Buy and Sell Stock
- Maximum Product Subarray
