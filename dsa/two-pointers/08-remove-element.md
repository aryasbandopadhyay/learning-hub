# 08. Remove Element

- **Difficulty:** Easy
- **Pattern:** Two Pointers
- **Asked at:** Amazon, Microsoft, Bloomberg, Adobe

## Problem
Given an array `nums` and a value `val`, remove all occurrences of `val` in-place.

**Input**
- `nums`: a list of integers.
- `val`: the value to remove.

**Output**
- Return `k`, the count of elements not equal to `val`. The first `k` positions of `nums` must contain kept elements; their order does not matter to the judge.

## Constraints
- `0 <= nums.length <= 100`
- `0 <= nums[i] <= 50`
- `0 <= val <= 100`

## Examples
```text
Input: nums = [3,2,2,3], val = 3
Output: 2
Explanation: Removing both `3`s leaves the two `2`s, so the new logical length is `2`.
```

## Understanding & Intuition
We only need to keep values not equal to `val`. If order matters, copy kept values forward. If order does not matter, swapping with the end can reduce writes when many removed values occur.

## Approach 1 — Naive / Brute Force
**Idea:** Build a filtered list and copy it back to the input.
```python
from typing import List

class Solution:
    def removeElement(self, nums: List[int], val: int) -> int:
        kept = [x for x in nums if x != val]
        for i, x in enumerate(kept):
            nums[i] = x
        return len(kept)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Write non-`val` elements forward, preserving relative order.
```python
from typing import List

class Solution:
    def removeElement(self, nums: List[int], val: int) -> int:
        write = 0
        for read in range(len(nums)):
            if nums[read] != val:
                nums[write] = nums[read]
                write += 1
        return write
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Since order is irrelevant, replace removed values with the current last candidate.
```python
from typing import List

class Solution:
    def removeElement(self, nums: List[int], val: int) -> int:
        i, n = 0, len(nums)
        while i < n:
            if nums[i] == val:
                nums[i] = nums[n - 1]
                n -= 1
                # Do not increment i; the swapped value must be checked.
            else:
                i += 1
        return n
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- The remaining order is not required.
- Recheck an index after swapping from the end.
- Empty arrays return 0.

## Related
- Remove Duplicates from Sorted Array
- Move Zeroes
