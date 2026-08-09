# 01. Binary Search

- **Difficulty:** Easy
- **Pattern:** Binary Search
- **Asked at:** Google, Amazon, Microsoft, Meta

## Problem
Given a sorted integer array `nums` with distinct values and an integer `target`, find the index of
`target`. If `target` is not present, return `-1`.

**Input**
- `nums`: a list of integers sorted in strictly increasing order.
- `target`: the value to search for.

**Output**
- An integer: the index of `target`, or `-1` if it is absent.

## Constraints
- 1 <= nums.length <= 10^4
- -10^4 <= nums[i], target <= 10^4
- All values in `nums` are unique.
- `nums` is sorted in ascending order.

## Examples
```text
Input: nums = [-1,0,3,5,9,12], target = 9
Output: 4
Explanation: `9` appears at index `4` in the array.
```

## Understanding & Intuition
The sorted order lets us discard half of the remaining search space after each comparison. If the middle value is too small, every value to its left is also too small. If it is too large, every value to its right is also too large.

## Approach 1 — Naive / Brute Force
**Idea:** Scan every value until the target is found.
```python
from typing import List

class Solution:
    def search(self, nums: List[int], target: int) -> int:
        # Linear scan checks each candidate directly.
        for i, value in enumerate(nums):
            if value == target:
                return i
        return -1
```
- **Time:** O(n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use recursive binary search to halve the interval.
```python
from typing import List

class Solution:
    def search(self, nums: List[int], target: int) -> int:
        def helper(left: int, right: int) -> int:
            if left > right:
                return -1
            mid = left + (right - left) // 2
            if nums[mid] == target:
                return mid
            if nums[mid] < target:
                return helper(mid + 1, right)
            return helper(left, mid - 1)

        return helper(0, len(nums) - 1)
```
- **Time:** O(log n) — **Space:** O(log n)

## Approach 3 — Optimal
**Idea:** Use iterative binary search to avoid recursion overhead.
```python
from typing import List

class Solution:
    def search(self, nums: List[int], target: int) -> int:
        left, right = 0, len(nums) - 1
        while left <= right:
            mid = left + (right - left) // 2
            if nums[mid] == target:
                return mid
            if nums[mid] < target:
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
| Better | O(log n) | O(log n) |
| Optimal | O(log n) | O(1) |

## Edge Cases & Pitfalls
- Empty intervals must stop with `-1`.
- Use `left + (right - left) // 2` to avoid overflow in fixed-width languages.
- Move bounds past `mid`; otherwise the loop can get stuck.

## Related
- Search Insert Position
- Find First and Last Position of Element in Sorted Array

