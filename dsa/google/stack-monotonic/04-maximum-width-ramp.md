# 04. Maximum Width Ramp

- **Difficulty:** Medium
- **Pattern:** Monotonic Stack
- **Asked at:** Google, Amazon, Meta

## Problem
A ramp is a pair `(i, j)` with `i < j` and `nums[i] <= nums[j]`. Return the maximum width `j - i` among all ramps, or `0` if none exists. Constraints: `2 <= len(nums) <= 10^5`, `0 <= nums[i] <= 10^5`.

## Examples
```text
Input: nums = [6,0,8,2,1,5]
Output: 4
Explanation: The widest ramp is `(1, 5)` because `0 <= 5`.
```

## Understanding & Intuition
The left endpoint should be early and small. Sorting indexes by value tracks the smallest index seen among all no-larger values. A decreasing stack of candidate left endpoints plus a right-to-left scan finds the widest valid pair in linear time.

## Approach 1 — Naive / Brute Force
**Idea:** Try every pair and keep the widest valid ramp.
```python
from typing import List

class Solution:
    def maxWidthRamp(self, nums: List[int]) -> int:
        best = 0
        for i in range(len(nums)):
            for j in range(i + 1, len(nums)):
                if nums[i] <= nums[j]:
                    best = max(best, j - i)
        return best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Sort indexes by value and maintain the smallest index seen so far.
```python
from typing import List

class Solution:
    def maxWidthRamp(self, nums: List[int]) -> int:
        best = 0
        min_index = len(nums)
        for i in sorted(range(len(nums)), key=lambda idx: (nums[idx], idx)):
            best = max(best, i - min_index)
            min_index = min(min_index, i)
        return best
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Store decreasing candidate left endpoints, then scan right endpoints from the end.
```python
from typing import List

class Solution:
    def maxWidthRamp(self, nums: List[int]) -> int:
        stack = []
        for i, value in enumerate(nums):
            if not stack or value < nums[stack[-1]]:
                stack.append(i)
        best = 0
        for j in range(len(nums) - 1, -1, -1):
            while stack and nums[stack[-1]] <= nums[j]:
                best = max(best, j - stack.pop())
        return best
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Equal values can form a ramp.
- Push a left index only if it is strictly smaller than earlier candidates.
- Scan right to left so widths are maximized before popping.

## Related
- Container With Most Water
- Monotonic Stack
