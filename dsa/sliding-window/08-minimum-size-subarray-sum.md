# 08. Minimum Size Subarray Sum

- **Difficulty:** Medium
- **Pattern:** Sliding Window
- **Asked at:** Amazon, Microsoft, Google, Facebook

## Problem
Given a target sum and an array of positive integers, find the minimum length of a contiguous subarray whose sum is at least `target`.

**Input**
- `target`: the required minimum sum.
- `nums`: a list of positive integers.

**Output**
- The length of the shortest qualifying subarray, or `0` if no such subarray exists.

## Constraints
- `1 <= target <= 10^9`
- `1 <= nums.length <= 10^5`
- `1 <= nums[i] <= 10^4`

## Examples
```text
Input: target = 7, nums = [2,3,1,2,4,3]
Output: 2
Explanation: The subarray `[4,3]` has sum `7` and length `2`; no single element reaches the target.
```

## Understanding & Intuition
Because all numbers are positive, expanding the right boundary only increases the sum and shrinking the left boundary only decreases it. This monotonic behavior enables a linear sliding window. Prefix sums also work because they are strictly increasing.

## Approach 1 — Naive / Brute Force
**Idea:** Compute every subarray sum incrementally and keep the shortest valid length.
```python
from typing import List

class Solution:
    def minSubArrayLen(self, target: int, nums: List[int]) -> int:
        best = float("inf")
        for left in range(len(nums)):
            total = 0
            for right in range(left, len(nums)):
                total += nums[right]
                if total >= target:
                    best = min(best, right - left + 1)
                    break
        return 0 if best == float("inf") else best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use prefix sums and binary search the earliest end reaching `target`.
```python
from typing import List
import bisect

class Solution:
    def minSubArrayLen(self, target: int, nums: List[int]) -> int:
        prefix = [0]
        for num in nums:
            prefix.append(prefix[-1] + num)
        best = float("inf")
        for left in range(len(nums)):
            end = bisect.bisect_left(prefix, prefix[left] + target)
            if end <= len(nums):
                best = min(best, end - left)
        return 0 if best == float("inf") else best
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Expand until the sum is large enough, then shrink greedily.
```python
from typing import List

class Solution:
    def minSubArrayLen(self, target: int, nums: List[int]) -> int:
        left = total = 0
        best = float("inf")
        for right, value in enumerate(nums):
            total += value
            while total >= target:
                best = min(best, right - left + 1)
                total -= nums[left]
                left += 1
        return 0 if best == float("inf") else best
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- This sliding window relies on all `nums[i]` being positive.
- Return `0`, not infinity, when no subarray works.
- Shrink while valid to find the minimum length.

## Related
- Sliding Window Maximum
- Maximum Average Subarray I

