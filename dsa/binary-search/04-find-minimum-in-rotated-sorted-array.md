# 04. Find Minimum in Rotated Sorted Array

- **Difficulty:** Medium
- **Pattern:** Binary Search
- **Asked at:** Amazon, Microsoft, Google, Bloomberg

## Problem
A strictly increasing array was rotated between `1` and `n` times. Given the resulting array `nums`,
return its minimum element. The array contains no duplicates.

**Input**
- `nums`: a rotated sorted list of distinct integers.

**Output**
- An integer: the smallest value in `nums`.

## Constraints
- 1 <= nums.length <= 5000
- -5000 <= nums[i] <= 5000
- All values in `nums` are unique.
- `nums` is a rotation of a strictly increasing array.

## Examples
```text
Input: nums = [3,4,5,1,2]
Output: 1
Explanation: The sorted order would be `[1,2,3,4,5]`, so the minimum in the rotated array is `1`.
```

## Understanding & Intuition
Rotation creates two sorted parts, and the minimum is the pivot. Comparing `nums[mid]` with `nums[right]` tells which side contains the pivot. If `nums[mid] > nums[right]`, the minimum must be to the right.

## Approach 1 — Naive / Brute Force
**Idea:** Return the smallest value by scanning.
```python
from typing import List

class Solution:
    def findMin(self, nums: List[int]) -> int:
        best = nums[0]
        for value in nums:
            best = min(best, value)
        return best
```
- **Time:** O(n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Scan for the drop where rotation wraps around.
```python
from typing import List

class Solution:
    def findMin(self, nums: List[int]) -> int:
        for i in range(1, len(nums)):
            if nums[i] < nums[i - 1]:
                return nums[i]
        return nums[0]
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Binary search the pivot using the rightmost value as a reference.
```python
from typing import List

class Solution:
    def findMin(self, nums: List[int]) -> int:
        left, right = 0, len(nums) - 1
        while left < right:
            mid = (left + right) // 2
            if nums[mid] > nums[right]:
                left = mid + 1
            else:
                right = mid
        return nums[left]
```
- **Time:** O(log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(1) |
| Better | O(n) | O(1) |
| Optimal | O(log n) | O(1) |

## Edge Cases & Pitfalls
- A non-rotated array returns `nums[0]`.
- Keep `right = mid`, not `mid - 1`, because `mid` may be the minimum.
- This version assumes no duplicates.

## Related
- Search in Rotated Sorted Array
- Find Minimum in Rotated Sorted Array II

