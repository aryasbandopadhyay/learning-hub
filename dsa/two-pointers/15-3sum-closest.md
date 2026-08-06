# 15. 3Sum Closest

- **Difficulty:** Medium
- **Pattern:** Two Pointers
- **Asked at:** Amazon, Meta, Bloomberg, Microsoft

## Problem
Given an integer array `nums` and integer `target`, return the sum of three integers in `nums` closest to `target`. Exactly one solution is assumed for the closest sum. Constraints: `3 <= len(nums) <= 500`.

## Examples
```text
Input: nums = [-1,2,1,-4], target = 1
Output: 2
Explanation: The sum closest to 1 is -1 + 2 + 1 = 2.
```

## Understanding & Intuition
The sorted array makes each fixed first value behave like a two-sum search. Moving the left pointer increases the sum, while moving the right pointer decreases it. Track the best absolute difference seen.

## Approach 1 — Naive / Brute Force
**Idea:** Check every triplet and keep the sum with the smallest distance to target.
```python
from typing import List

class Solution:
    def threeSumClosest(self, nums: List[int], target: int) -> int:
        best = nums[0] + nums[1] + nums[2]
        n = len(nums)
        for i in range(n):
            for j in range(i + 1, n):
                for k in range(j + 1, n):
                    total = nums[i] + nums[j] + nums[k]
                    if abs(total - target) < abs(best - target):
                        best = total
        return best
```
- **Time:** O(n³) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Sort, fix two numbers, and binary search for the best third candidate.
```python
from typing import List
import bisect

class Solution:
    def threeSumClosest(self, nums: List[int], target: int) -> int:
        nums.sort()
        best = nums[0] + nums[1] + nums[2]
        n = len(nums)
        for i in range(n - 2):
            for j in range(i + 1, n - 1):
                need = target - nums[i] - nums[j]
                pos = bisect.bisect_left(nums, need, j + 1)
                for k in (pos - 1, pos):
                    if j < k < n:
                        total = nums[i] + nums[j] + nums[k]
                        if abs(total - target) < abs(best - target):
                            best = total
        return best
```
- **Time:** O(n² log n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Sort, fix one number, and use two pointers to move toward the target.
```python
from typing import List

class Solution:
    def threeSumClosest(self, nums: List[int], target: int) -> int:
        nums.sort()
        best = nums[0] + nums[1] + nums[2]
        for i in range(len(nums) - 2):
            left, right = i + 1, len(nums) - 1
            while left < right:
                total = nums[i] + nums[left] + nums[right]
                if abs(total - target) < abs(best - target):
                    best = total
                if total == target:
                    return target
                if total < target:
                    left += 1
                else:
                    right -= 1
        return best
```
- **Time:** O(n²) — **Space:** O(1) extra

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n³) | O(1) |
| Better | O(n² log n) | O(1) |
| Optimal | O(n²) | O(1) extra |

## Edge Cases & Pitfalls
- Initialize `best` with a real triplet.
- Return immediately when the exact target is found.
- Sorting mutates `nums`; copy first if caller needs original order.

## Related
- 3Sum
- 4Sum
- Two Sum II
