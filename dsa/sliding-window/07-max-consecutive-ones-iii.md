# 07. Max Consecutive Ones III

- **Difficulty:** Medium
- **Pattern:** Sliding Window
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given a binary array `nums` and an integer `k`, find the longest contiguous subarray containing only `1`s after flipping at most `k` zeros to ones.

**Input**
- `nums`: a list containing only `0` and `1`.
- `k`: the maximum number of zeros that may be flipped.

**Output**
- The maximum possible length of such a contiguous subarray.

## Constraints
- `1 <= nums.length <= 10^5`
- `nums[i]` is `0` or `1`.
- `0 <= k <= nums.length`

## Examples
```text
Input: nums = [1,1,1,0,0,0,1,1,1,1,0], k = 2
Output: 6
Explanation: Flipping two zeros in the middle allows a run spanning six positions of effective `1`s, and no longer run can be formed with only two flips.
```

## Understanding & Intuition
Flipping at most `k` zeros means a valid window may contain no more than `k` zeros. When the zero count exceeds `k`, shrink from the left. The longest valid window gives the answer.

## Approach 1 — Naive / Brute Force
**Idea:** Try every start and extend while counting zeros.
```python
from typing import List

class Solution:
    def longestOnes(self, nums: List[int], k: int) -> int:
        best = 0
        for left in range(len(nums)):
            zeros = 0
            for right in range(left, len(nums)):
                zeros += nums[right] == 0
                if zeros > k:
                    break
                best = max(best, right - left + 1)
        return best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Prefix-count zeros and binary search the farthest valid right endpoint for each left.
```python
from typing import List
import bisect

class Solution:
    def longestOnes(self, nums: List[int], k: int) -> int:
        zeros = [0]
        for value in nums:
            zeros.append(zeros[-1] + (value == 0))
        best = 0
        for left in range(len(nums)):
            # Need prefix_zeros[right + 1] <= prefix_zeros[left] + k.
            end = bisect.bisect_right(zeros, zeros[left] + k) - 1
            best = max(best, end - left)
        return best
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Maintain one window whose zero count never exceeds `k`.
```python
from typing import List

class Solution:
    def longestOnes(self, nums: List[int], k: int) -> int:
        left = zeros = best = 0
        for right, value in enumerate(nums):
            zeros += value == 0
            while zeros > k:
                zeros -= nums[left] == 0
                left += 1
            best = max(best, right - left + 1)
        return best
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- `k = 0` asks for the longest existing run of ones.
- Count zeros, not ones, because zeros are the limited resource.
- In the prefix approach, remember right endpoint is `end - 1`.

## Related
- Longest Repeating Character Replacement
- Longest Subarray of 1's After Deleting One Element

