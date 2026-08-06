# 05. Search in Rotated Sorted Array

- **Difficulty:** Medium
- **Pattern:** Binary Search
- **Asked at:** Facebook, Amazon, Microsoft, Google

## Problem
Given a rotated sorted array of distinct integers and a target, return the target index or `-1` if absent. Constraints: `1 <= nums.length <= 5000`, `-10^4 <= nums[i], target <= 10^4`, all values are unique.

## Examples
```text
Input: nums = [4,5,6,7,0,1,2], target = 0
Output: 4
Explanation: 0 appears at index 4 after rotation.
```

## Understanding & Intuition
At least one half around `mid` is always sorted. Once we identify the sorted half, we can decide whether the target lies inside it. If not, discard that half and continue.

## Approach 1 — Naive / Brute Force
**Idea:** Linearly search all indices.
```python
from typing import List

class Solution:
    def search(self, nums: List[int], target: int) -> int:
        for i, value in enumerate(nums):
            if value == target:
                return i
        return -1
```
- **Time:** O(n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Find the rotation pivot, then binary search the correct sorted side.
```python
from typing import List

class Solution:
    def search(self, nums: List[int], target: int) -> int:
        n = len(nums)
        pivot = 0
        for i in range(1, n):
            if nums[i] < nums[i - 1]:
                pivot = i
                break

        if nums[pivot] <= target <= nums[-1]:
            left, right = pivot, n - 1
        else:
            left, right = 0, pivot - 1

        while left <= right:
            mid = (left + right) // 2
            if nums[mid] == target:
                return mid
            if nums[mid] < target:
                left = mid + 1
            else:
                right = mid - 1
        return -1
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** In one binary search, use the sorted half to discard impossible ranges.
```python
from typing import List

class Solution:
    def search(self, nums: List[int], target: int) -> int:
        left, right = 0, len(nums) - 1
        while left <= right:
            mid = (left + right) // 2
            if nums[mid] == target:
                return mid

            if nums[left] <= nums[mid]:
                # Left half is sorted.
                if nums[left] <= target < nums[mid]:
                    right = mid - 1
                else:
                    left = mid + 1
            else:
                # Right half is sorted.
                if nums[mid] < target <= nums[right]:
                    left = mid + 1
                else:
                    right = mid - 1
        return -1
```
- **Time:** O(log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(1) |
| Better | O(n) | O(1) |
| Optimal | O(log n) | O(1) |

## Edge Cases & Pitfalls
- Single-element arrays still need both equality and not-found handling.
- Use inclusive comparisons at sorted-half boundaries.
- Distinct values are required for this exact O(log n) logic.

## Related
- Find Minimum in Rotated Sorted Array
- Binary Search

