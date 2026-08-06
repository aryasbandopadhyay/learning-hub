# 01. Sum of Subarray Ranges

- **Difficulty:** Medium
- **Pattern:** Monotonic Stack
- **Asked at:** Google, Amazon, Meta

## Problem
Given an integer array `nums`, return the sum over every contiguous subarray of `(maximum value - minimum value)`. Constraints: `1 <= len(nums) <= 1000`, `-10^9 <= nums[i] <= 10^9`.

## Examples
```text
Input: nums = [1,2,3]
Output: 4
Explanation: The subarray ranges are 0, 1, 2, 0, 1, and 0, summing to 4.
```

## Understanding & Intuition
A range is max minus min, so the answer is total max contribution minus total min contribution. Brute force maintains both while extending a subarray. Monotonic stacks count how many subarrays choose each index as the tie-broken min or max.

## Approach 1 — Naive / Brute Force
**Idea:** Extend every start and update current min and max.
```python
from typing import List

class Solution:
    def subArrayRanges(self, nums: List[int]) -> int:
        total = 0
        for left in range(len(nums)):
            mn = mx = nums[left]
            for right in range(left, len(nums)):
                mn = min(mn, nums[right])
                mx = max(mx, nums[right])
                total += mx - mn
        return total
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** For each index, scan outward to count tie-broken max and min spans.
```python
from typing import List

class Solution:
    def subArrayRanges(self, nums: List[int]) -> int:
        n = len(nums)
        total = 0
        for i, value in enumerate(nums):
            l = i
            while l > 0 and nums[l - 1] <= value:
                l -= 1
            r = i
            while r + 1 < n and nums[r + 1] < value:
                r += 1
            total += value * (i - l + 1) * (r - i + 1)
            l = i
            while l > 0 and nums[l - 1] >= value:
                l -= 1
            r = i
            while r + 1 < n and nums[r + 1] > value:
                r += 1
            total -= value * (i - l + 1) * (r - i + 1)
        return total
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use decreasing and increasing stacks to compute contribution counts in linear time.
```python
from typing import List

class Solution:
    def subArrayRanges(self, nums: List[int]) -> int:
        n = len(nums)
        def contrib(as_max: bool) -> int:
            left = [0] * n
            right = [0] * n
            stack = []
            for i, x in enumerate(nums):
                if as_max:
                    while stack and nums[stack[-1]] <= x:
                        stack.pop()
                else:
                    while stack and nums[stack[-1]] >= x:
                        stack.pop()
                left[i] = i - stack[-1] if stack else i + 1
                stack.append(i)
            stack = []
            for i in range(n - 1, -1, -1):
                x = nums[i]
                if as_max:
                    while stack and nums[stack[-1]] < x:
                        stack.pop()
                else:
                    while stack and nums[stack[-1]] > x:
                        stack.pop()
                right[i] = stack[-1] - i if stack else n - i
                stack.append(i)
            return sum(nums[i] * left[i] * right[i] for i in range(n))
        return contrib(True) - contrib(False)
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n^2) | O(1) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- A single element contributes `0`.
- Duplicate values require consistent tie-breaking.
- The answer can exceed 32-bit range.

## Related
- Sum of Subarray Minimums
- Largest Rectangle in Histogram
