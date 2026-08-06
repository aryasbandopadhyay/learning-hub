# 09. Search Insert Position

- **Difficulty:** Easy
- **Pattern:** Binary Search
- **Asked at:** Google, Amazon, Microsoft, Adobe

## Problem
Given a sorted array of distinct integers and a target, return the index if found. Otherwise return the index where it would be inserted in order. Constraints: `1 <= nums.length <= 10^4`, `-10^4 <= nums[i], target <= 10^4`.

## Examples
```text
Input: nums = [1,3,5,6], target = 5
Output: 2
Explanation: 5 already exists at index 2.
```

## Understanding & Intuition
The desired index is the first position whose value is greater than or equal to `target`. If every value is smaller, the insertion point is `len(nums)`. This is the classic lower-bound binary search.

## Approach 1 — Naive / Brute Force
**Idea:** Scan until the first value greater than or equal to target.
```python
from typing import List

class Solution:
    def searchInsert(self, nums: List[int], target: int) -> int:
        for i, value in enumerate(nums):
            if value >= target:
                return i
        return len(nums)
```
- **Time:** O(n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use the standard library lower-bound helper.
```python
from bisect import bisect_left
from typing import List

class Solution:
    def searchInsert(self, nums: List[int], target: int) -> int:
        return bisect_left(nums, target)
```
- **Time:** O(log n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Implement lower-bound binary search manually.
```python
from typing import List

class Solution:
    def searchInsert(self, nums: List[int], target: int) -> int:
        left, right = 0, len(nums)
        while left < right:
            mid = (left + right) // 2
            if nums[mid] < target:
                left = mid + 1
            else:
                right = mid
        return left
```
- **Time:** O(log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(1) |
| Better | O(log n) | O(1) |
| Optimal | O(log n) | O(1) |

## Edge Cases & Pitfalls
- Target smaller than all values returns `0`.
- Target larger than all values returns `len(nums)`.
- Use half-open interval `[left, right)` for clean insertion-point logic.

## Related
- Binary Search
- Find First and Last Position of Element in Sorted Array

