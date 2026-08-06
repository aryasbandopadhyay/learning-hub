# 05. 132 Pattern

- **Difficulty:** Medium
- **Pattern:** Monotonic Stack
- **Asked at:** Google, Amazon, Meta

## Problem
Given an integer array `nums`, return `True` if there exist indices `i < j < k` such that `nums[i] < nums[k] < nums[j]`; otherwise return `False`. Constraints: `1 <= len(nums) <= 2 * 10^5`, `-10^9 <= nums[i] <= 10^9`.

## Examples
```text
Input: nums = [3,1,4,2]
Output: True
Explanation: The values `1, 4, 2` form a 132 pattern.
```

## Understanding & Intuition
The middle index must hold the largest of the three values. Prefix minima make the left value easy to test. Scanning from the right with a stack tracks the best possible `2` value below the current `3`.

## Approach 1 — Naive / Brute Force
**Idea:** Test every triple of indices.
```python
from typing import List

class Solution:
    def find132pattern(self, nums: List[int]) -> bool:
        n = len(nums)
        for i in range(n):
            for j in range(i + 1, n):
                for k in range(j + 1, n):
                    if nums[i] < nums[k] < nums[j]:
                        return True
        return False
```
- **Time:** O(n^3) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Precompute the smallest value before each middle index, then scan possible right values.
```python
from typing import List

class Solution:
    def find132pattern(self, nums: List[int]) -> bool:
        n = len(nums)
        if n < 3:
            return False
        prefix_min = [0] * n
        prefix_min[0] = nums[0]
        for i in range(1, n):
            prefix_min[i] = min(prefix_min[i - 1], nums[i])
        for j in range(1, n - 1):
            if prefix_min[j] >= nums[j]:
                continue
            for k in range(j + 1, n):
                if prefix_min[j] < nums[k] < nums[j]:
                    return True
        return False
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Scan from right; popped values become the largest valid `2` below the current `3`.
```python
from typing import List

class Solution:
    def find132pattern(self, nums: List[int]) -> bool:
        third = float('-inf')
        stack = []
        for value in reversed(nums):
            if value < third:
                return True
            while stack and stack[-1] < value:
                third = max(third, stack.pop())
            stack.append(value)
        return False
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(1) |
| Better | O(n^2) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Arrays shorter than three cannot contain the pattern.
- The inequalities are strict.
- `third` is the current best candidate for `nums[k]`.

## Related
- Increasing Triplet Subsequence
- Next Greater Element
