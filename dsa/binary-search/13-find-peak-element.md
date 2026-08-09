# 13. Find Peak Element

- **Difficulty:** Medium
- **Pattern:** Binary Search
- **Asked at:** Facebook, Google, Amazon, Microsoft

## Problem
A peak element is an element strictly greater than its immediate neighbors. Values outside the array
are treated as negative infinity. Given `nums`, return the index of any peak element.

**Input**
- `nums`: a list of integers where adjacent values are different.

**Output**
- An integer: the index of a peak element. If multiple peaks exist, any peak index is acceptable.

## Constraints
- 1 <= nums.length <= 1000
- -2^31 <= nums[i] <= 2^31 - 1
- nums[i] != nums[i + 1] for all valid `i`.

## Examples
```text
Input: nums = [1,2,3,1]
Output: 2
Explanation: `3` at index `2` is greater than both neighbors `2` and `1`, so index `2` is a peak.
```

## Understanding & Intuition
If `nums[mid] < nums[mid + 1]`, the slope rises to the right, so a peak must exist on the right side. Otherwise, a peak exists at `mid` or to its left. This directional property enables binary search without global sorting.

## Approach 1 — Naive / Brute Force
**Idea:** Check each index against its neighbors.
```python
from typing import List

class Solution:
    def findPeakElement(self, nums: List[int]) -> int:
        n = len(nums)
        for i in range(n):
            left = float("-inf") if i == 0 else nums[i - 1]
            right = float("-inf") if i == n - 1 else nums[i + 1]
            if nums[i] > left and nums[i] > right:
                return i
        return 0
```
- **Time:** O(n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Linear scan for the first drop; the previous element is a peak.
```python
from typing import List

class Solution:
    def findPeakElement(self, nums: List[int]) -> int:
        for i in range(len(nums) - 1):
            if nums[i] > nums[i + 1]:
                return i
        return len(nums) - 1
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Binary search toward the side that must contain a peak.
```python
from typing import List

class Solution:
    def findPeakElement(self, nums: List[int]) -> int:
        left, right = 0, len(nums) - 1
        while left < right:
            mid = (left + right) // 2
            if nums[mid] < nums[mid + 1]:
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
| Better | O(n) | O(1) |
| Optimal | O(log n) | O(1) |

## Edge Cases & Pitfalls
- A single element is a peak.
- Since adjacent values differ, equality does not need special handling.
- Return any peak; multiple valid outputs may exist.

## Related
- Peak Index in a Mountain Array
- Find Minimum in Rotated Sorted Array

