# 01. Search in Rotated Sorted Array II

- **Difficulty:** Medium
- **Pattern:** Advanced Binary Search
- **Asked at:** Google

## Problem
Given an integer list `nums` sorted in nondecreasing order and then rotated at an unknown pivot, return `True` if `target` exists in `nums`, otherwise return `False`. The array may contain duplicates, so equal boundary values can hide which half is sorted.

Constraints: `1 <= len(nums) <= 5000`, `-10^4 <= nums[i], target <= 10^4`.

## Examples
```text
Input: nums = [2,5,6,0,0,1,2], target = 0
Output: True
Explanation: The value 0 is present after the rotation pivot.
```

## Understanding & Intuition
A rotated sorted array still has at least one sorted half unless duplicates make the boundaries ambiguous. When the boundaries are equal, discarding one equal boundary is safe because it cannot be the only copy of the target if the middle was not the target.

## Approach 1 — Naive / Brute Force
**Idea:** Scan every value and return as soon as the target is found.
```python
class Solution:
    def search(self, nums: list[int], target: int) -> bool:
        for x in nums:
            if x == target:
                return True
        return False
```
- **Time:** O(n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Sort a copy, then use ordinary binary search.
```python
class Solution:
    def search(self, nums: list[int], target: int) -> bool:
        arr = sorted(nums)
        lo, hi = 0, len(arr) - 1
        while lo <= hi:
            mid = (lo + hi) // 2
            if arr[mid] == target:
                return True
            if arr[mid] < target:
                lo = mid + 1
            else:
                hi = mid - 1
        return False
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Binary search the rotated array, shrinking duplicate boundaries when they obscure the sorted side.
```python
class Solution:
    def search(self, nums: list[int], target: int) -> bool:
        lo, hi = 0, len(nums) - 1
        while lo <= hi:
            mid = (lo + hi) // 2
            if nums[mid] == target:
                return True
            if nums[lo] == nums[mid] == nums[hi]:
                lo += 1
                hi -= 1
            elif nums[lo] <= nums[mid]:
                if nums[lo] <= target < nums[mid]:
                    hi = mid - 1
                else:
                    lo = mid + 1
            else:
                if nums[mid] < target <= nums[hi]:
                    lo = mid + 1
                else:
                    hi = mid - 1
        return False
```
- **Time:** O(n) worst case, O(log n) average — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) worst, O(log n) average | O(1) |

## Edge Cases & Pitfalls
- All values may be equal.
- Do not assume one half is strictly sorted when duplicates touch both boundaries.
- A one-element array should work.

## Related
- Search in Rotated Sorted Array
- Find Minimum in Rotated Sorted Array II
