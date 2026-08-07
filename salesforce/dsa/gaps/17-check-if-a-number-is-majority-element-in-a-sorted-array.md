# 17. Check If a Number Is Majority Element in a Sorted Array

- **Difficulty:** Easy
- **Pattern:** Binary Search
- **Asked at:** Salesforce, Google, Amazon

## Problem
Given a sorted array and target, return whether target appears more than half the array length.

## Examples
```text
Input: nums = [2,4,5,5,5,5,5,6,6], target = 5
Output: true
Explanation: 5 appears five times in nine values.
```

## Understanding & Intuition
All target occurrences are contiguous. A target is majority if it still appears at index `first + n//2`.

## Approach 1 — Naive / Brute Force
**Idea:** Count target occurrences by scanning.
```python
class Solution:
    def isMajorityElement(self, nums: list[int], target: int) -> bool:
        return sum(x == target for x in nums) > len(nums) // 2
```
- **Time:** O(n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Find the first target with binary search, then check the majority offset.
```python
class Solution:
    def isMajorityElement(self, nums: list[int], target: int) -> bool:
        lo, hi = 0, len(nums)
        while lo < hi:
            mid = (lo + hi) // 2
            if nums[mid] < target: lo = mid + 1
            else: hi = mid
        idx = lo + len(nums) // 2
        return idx < len(nums) and nums[idx] == target
```
- **Time:** O(log n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use lower and upper binary-search bounds.
```python
from bisect import bisect_left, bisect_right
class Solution:
    def isMajorityElement(self, nums: list[int], target: int) -> bool:
        return bisect_right(nums, target) - bisect_left(nums, target) > len(nums) // 2
```
- **Time:** O(log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(1) |
| Better | O(log n) | O(1) |
| Optimal | O(log n) | O(1) |

## Edge Cases & Pitfalls
- Majority means strictly greater than half.
- Bounds checks matter when target is absent.
- Sorted input enables binary search.

## Related
- Find First and Last Position of Element in Sorted Array
- First Bad Version
