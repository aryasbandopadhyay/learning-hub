# 08. Find First and Last Position of Element in Sorted Array

- **Difficulty:** Medium
- **Pattern:** Binary Search
- **Asked at:** Facebook, Amazon, Google, Microsoft

## Problem
Given a sorted integer array `nums` and a target, return the starting and ending position of target. If the target is absent, return `[-1, -1]`. Constraints: `0 <= nums.length <= 10^5`, `-10^9 <= nums[i], target <= 10^9`.

## Examples
```text
Input: nums = [5,7,7,8,8,10], target = 8
Output: [3,4]
Explanation: The value 8 appears from index 3 through index 4.
```

## Understanding & Intuition
The first and last occurrence are boundary positions. Binary search can find the left boundary by seeking the first value `>= target`, and the right boundary by seeking the first value `> target`. Their difference gives the target range.

## Approach 1 — Naive / Brute Force
**Idea:** Scan the array and record the first and last matching indices.
```python
from typing import List

class Solution:
    def searchRange(self, nums: List[int], target: int) -> List[int]:
        first = last = -1
        for i, value in enumerate(nums):
            if value == target:
                if first == -1:
                    first = i
                last = i
        return [first, last]
```
- **Time:** O(n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use `bisect_left` and `bisect_right` from the standard library.
```python
from bisect import bisect_left, bisect_right
from typing import List

class Solution:
    def searchRange(self, nums: List[int], target: int) -> List[int]:
        left = bisect_left(nums, target)
        right = bisect_right(nums, target) - 1
        if left <= right:
            return [left, right]
        return [-1, -1]
```
- **Time:** O(log n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Implement two lower-bound binary searches manually.
```python
from typing import List

class Solution:
    def searchRange(self, nums: List[int], target: int) -> List[int]:
        def lower_bound(value: int) -> int:
            left, right = 0, len(nums)
            while left < right:
                mid = (left + right) // 2
                if nums[mid] < value:
                    left = mid + 1
                else:
                    right = mid
            return left

        first = lower_bound(target)
        last_exclusive = lower_bound(target + 1)
        if first == len(nums) or nums[first] != target:
            return [-1, -1]
        return [first, last_exclusive - 1]
```
- **Time:** O(log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(1) |
| Better | O(log n) | O(1) |
| Optimal | O(log n) | O(1) |

## Edge Cases & Pitfalls
- Empty arrays should return `[-1, -1]`.
- Do not assume target exists after computing a boundary.
- In languages with fixed-width integers, avoid `target + 1` overflow by searching for first `> target`.

## Related
- Binary Search
- Search Insert Position

