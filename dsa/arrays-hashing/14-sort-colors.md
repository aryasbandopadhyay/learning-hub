# 14. Sort Colors

- **Difficulty:** Medium
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Sort an array containing only 0, 1, and 2 in-place in color order. Do not use library sort for the optimal solution.

## Examples
```text
Input: nums = [2,0,2,1,1,0]
Output: [0,0,1,1,2,2]
Explanation: Colors are ordered red, white, blue.
```

## Understanding & Intuition
With three values, counting is natural. Dutch National Flag partitions in one pass with low, mid, and high regions.

## Approach 1 — Naive / Brute Force
**Idea:** Bubble larger colors right.
```python
class Solution:
    def sortColors(self, nums: list[int]) -> None:
        n = len(nums)
        for _ in range(n):
            for i in range(1, n):
                if nums[i-1] > nums[i]:
                    nums[i-1], nums[i] = nums[i], nums[i-1]
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Count colors and overwrite the array.
```python
class Solution:
    def sortColors(self, nums: list[int]) -> None:
        counts = [0, 0, 0]
        for x in nums:
            counts[x] += 1
        i = 0
        for color in range(3):
            for _ in range(counts[color]):
                nums[i] = color
                i += 1
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use Dutch National Flag pointers.
```python
class Solution:
    def sortColors(self, nums: list[int]) -> None:
        low = mid = 0
        high = len(nums) - 1
        while mid <= high:
            if nums[mid] == 0:
                nums[low], nums[mid] = nums[mid], nums[low]
                low += 1; mid += 1
            elif nums[mid] == 1:
                mid += 1
            else:
                nums[mid], nums[high] = nums[high], nums[mid]
                high -= 1
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Do not increment mid after swapping with high.
- Mutate in-place.
- Only values 0,1,2 appear.

## Related
- Move Zeroes
- Partition Array
