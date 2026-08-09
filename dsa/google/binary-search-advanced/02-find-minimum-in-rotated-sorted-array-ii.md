# 02. Find Minimum in Rotated Sorted Array II

- **Difficulty:** Hard
- **Pattern:** Advanced Binary Search
- **Asked at:** Google

## Problem
You are given an integer array `nums` that was sorted in non-decreasing order and then rotated at an unknown pivot. Duplicate values may appear.

Return the minimum value in the array.

**Input**
- `nums`: a rotated non-decreasing list of integers, possibly with duplicates.

**Output**
- The smallest value in `nums`.

## Constraints
- `1 <= nums.length <= 5000`
- `-5000 <= nums[i] <= 5000`

## Examples
```text
Input: nums = [2,2,2,0,1]
Output: 0
Explanation: The smallest value is `0`; the other values are `1` or `2`.
```

## Understanding & Intuition
The minimum is the only place where the sorted order wraps around. Comparing the middle with the right boundary tells whether the minimum is to the right, to the left including middle, or hidden by duplicates.

## Approach 1 — Naive / Brute Force
**Idea:** Check every value and keep the smallest.
```python
class Solution:
    def findMin(self, nums: list[int]) -> int:
        ans = nums[0]
        for x in nums:
            if x < ans:
                ans = x
        return ans
```
- **Time:** O(n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Remove consecutive duplicates, then binary search the compressed rotated list.
```python
class Solution:
    def findMin(self, nums: list[int]) -> int:
        arr = []
        for x in nums:
            if not arr or arr[-1] != x:
                arr.append(x)
        lo, hi = 0, len(arr) - 1
        while lo < hi:
            mid = (lo + hi) // 2
            if arr[mid] > arr[hi]:
                lo = mid + 1
            else:
                hi = mid
        return arr[lo]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Binary search directly; when `nums[mid] == nums[hi]`, discard one duplicate at `hi`.
```python
class Solution:
    def findMin(self, nums: list[int]) -> int:
        lo, hi = 0, len(nums) - 1
        while lo < hi:
            mid = (lo + hi) // 2
            if nums[mid] > nums[hi]:
                lo = mid + 1
            elif nums[mid] < nums[hi]:
                hi = mid
            else:
                hi -= 1
        return nums[lo]
```
- **Time:** O(n) worst case, O(log n) average — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) worst, O(log n) average | O(1) |

## Edge Cases & Pitfalls
- Duplicate right boundaries require `hi -= 1`, not arbitrary half elimination.
- Already sorted arrays return the first element.
- Arrays of length one are valid.

## Related
- Search in Rotated Sorted Array II
- Find Minimum in Rotated Sorted Array
