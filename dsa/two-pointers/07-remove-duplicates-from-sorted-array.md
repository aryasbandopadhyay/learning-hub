# 07. Remove Duplicates from Sorted Array

- **Difficulty:** Easy
- **Pattern:** Two Pointers
- **Asked at:** Microsoft, Amazon, Apple, Bloomberg

## Problem
Given a non-decreasing array `nums`, remove duplicates in-place so each distinct value appears once at the front.

**Input**
- `nums`: a sorted list of integers.

**Output**
- Return `k`, the number of distinct values. The first `k` positions of `nums` must contain those values in sorted order; values after `k` are ignored.

## Constraints
- `1 <= nums.length <= 3 * 10^4`
- `-100 <= nums[i] <= 100`
- `nums` is sorted in non-decreasing order.

## Examples
```text
Input: nums = [1,1,2]
Output: 2
Explanation: The distinct values are `1` and `2`, so `k = 2` and the front of the array should be `[1,2]`.
```

## Understanding & Intuition
Because the array is sorted, duplicates are adjacent. One pointer can read every value, while another marks where the next unique value should be written.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly delete adjacent duplicates from the list.
```python
from typing import List

class Solution:
    def removeDuplicates(self, nums: List[int]) -> int:
        i = 1
        while i < len(nums):
            # In a sorted array, equal neighbors are duplicates.
            if nums[i] == nums[i - 1]:
                del nums[i]
            else:
                i += 1
        return len(nums)
```
- **Time:** O(n²) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Build a new list of unique values, copy it back, and return its length.
```python
from typing import List

class Solution:
    def removeDuplicates(self, nums: List[int]) -> int:
        unique = []
        for value in nums:
            # Append only the first value from each duplicate run.
            if not unique or unique[-1] != value:
                unique.append(value)
        for i, value in enumerate(unique):
            nums[i] = value
        return len(unique)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Keep a write pointer for the next new value while scanning once.
```python
from typing import List

class Solution:
    def removeDuplicates(self, nums: List[int]) -> int:
        if not nums:
            return 0
        write = 1
        for read in range(1, len(nums)):
            if nums[read] != nums[write - 1]:
                nums[write] = nums[read]
                write += 1
        return write
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n²) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Only the first `k` elements matter after the call.
- The input is sorted; the optimal logic depends on this.
- Single-element arrays return 1.

## Related
- Remove Element
- Remove Duplicates from Sorted Array II
