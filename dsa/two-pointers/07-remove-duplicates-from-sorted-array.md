# 07. Remove Duplicates from Sorted Array

- **Difficulty:** Easy
- **Pattern:** Two Pointers
- **Asked at:** Microsoft, Amazon, Apple, Bloomberg

## Problem
Given a sorted integer array `nums`, remove duplicates in-place so each unique element appears once. Return the number of unique elements `k`; the first `k` positions must contain them in order. Constraints: `1 <= len(nums) <= 3 * 10^4`.

## Examples
```text
Input: nums = [1,1,2]
Output: 2
Explanation: The first two elements become [1,2].
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
