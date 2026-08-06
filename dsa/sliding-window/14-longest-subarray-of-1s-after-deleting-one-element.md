# 14. Longest Subarray of 1's After Deleting One Element

- **Difficulty:** Medium
- **Pattern:** Sliding Window
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given binary array `nums`, delete exactly one element and return the length of the longest non-empty subarray containing only `1`s after deletion. Constraints: `1 <= len(nums) <= 10^5`, `nums[i]` is `0` or `1`.

## Examples
```text
Input: nums = [1,1,0,1]
Output: 3
Explanation: Delete the zero to join the two runs of ones.
```

## Understanding & Intuition
Deleting one element means a chosen window may contain at most one zero, and its final ones length is `window_size - 1`. If there is no zero, we still must delete one `1`. This is a close variant of Max Consecutive Ones III with `k = 1`.

## Approach 1 — Naive / Brute Force
**Idea:** Delete each index and scan the remaining array for the longest run of ones.
```python
from typing import List

class Solution:
    def longestSubarray(self, nums: List[int]) -> int:
        best = 0
        for delete in range(len(nums)):
            current = 0
            for i, value in enumerate(nums):
                if i == delete:
                    continue
                if value == 1:
                    current += 1
                    best = max(best, current)
                else:
                    current = 0
        return best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use a window with at most one zero and subtract one deleted element from its length.
```python
from typing import List

class Solution:
    def longestSubarray(self, nums: List[int]) -> int:
        left = zeros = best = 0
        for right, value in enumerate(nums):
            zeros += value == 0
            while zeros > 1:
                zeros -= nums[left] == 0
                left += 1
            # Exactly one element must be deleted from the valid window.
            best = max(best, right - left)
        return best
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Track the last zero and jump `left` just after the previous zero.
```python
from typing import List

class Solution:
    def longestSubarray(self, nums: List[int]) -> int:
        left = 0
        last_zero = -1
        best = 0
        for right, value in enumerate(nums):
            if value == 0:
                # A second zero forces the window to start after the prior zero.
                left = last_zero + 1
                last_zero = right
            best = max(best, right - left)
        return best
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- All ones returns `len(nums) - 1` because deletion is mandatory.
- All zeros returns `0`.
- Use `right - left`, not `right - left + 1`, after deleting one element.

## Related
- Max Consecutive Ones III
- Count Number of Nice Subarrays

