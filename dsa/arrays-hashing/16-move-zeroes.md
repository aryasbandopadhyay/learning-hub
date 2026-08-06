# 16. Move Zeroes

- **Difficulty:** Easy
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Move all zeros to the end of `nums` in-place while preserving the relative order of nonzero values. Constraints: `n <= 10^4`.

## Examples
```text
Input: nums = [0,1,0,3,12]
Output: [1,3,12,0,0]
Explanation: Nonzero order is preserved.
```

## Understanding & Intuition
This is stable compaction. Write nonzeros to the front, then fill zeros; swapping with a write pointer is an in-place refinement.

## Approach 1 — Naive / Brute Force
**Idea:** Bubble nonzeros left across zeros.
```python
class Solution:
    def moveZeroes(self, nums: list[int]) -> None:
        n = len(nums)
        for _ in range(n):
            for i in range(1, n):
                if nums[i-1] == 0 and nums[i] != 0:
                    nums[i-1], nums[i] = nums[i], nums[i-1]
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Overwrite nonzeros then fill remaining slots with zero.
```python
class Solution:
    def moveZeroes(self, nums: list[int]) -> None:
        write = 0
        for x in nums:
            if x != 0:
                nums[write] = x
                write += 1
        while write < len(nums):
            nums[write] = 0
            write += 1
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Swap each nonzero into the next write position.
```python
class Solution:
    def moveZeroes(self, nums: list[int]) -> None:
        write = 0
        for read in range(len(nums)):
            if nums[read] != 0:
                nums[write], nums[read] = nums[read], nums[write]
                write += 1
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Preserve order.
- Mutate in-place and return None.
- All-zero arrays are valid.

## Related
- Sort Colors
- Remove Element
