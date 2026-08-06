# 04. Find Minimum in Rotated Sorted Array

- **Difficulty:** Medium
- **Pattern:** Binary Search
- **Asked at:** Amazon, Microsoft, Google, Bloomberg

## Problem
Given a sorted array of unique integers rotated between `1` and `n` times, return the minimum element. Constraints: `1 <= nums.length <= 5000`, `-5000 <= nums[i] <= 5000`, all values are unique.

## Examples
```text
Input: nums = [3,4,5,1,2]
Output: 1
Explanation: The sorted array was rotated so the minimum moved to index 3.
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

