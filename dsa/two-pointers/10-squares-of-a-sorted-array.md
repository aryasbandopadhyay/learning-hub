# 10. Squares of a Sorted Array

- **Difficulty:** Easy
- **Pattern:** Two Pointers
- **Asked at:** Meta, Amazon, Apple, Microsoft

## Problem
Given a non-decreasing integer array `nums`, return an array of the squares of each number, also sorted in non-decreasing order. Constraints: `1 <= len(nums) <= 10^4`.

## Examples
```text
Input: nums = [-4,-1,0,3,10]
Output: [0,1,9,16,100]
Explanation: Squaring gives [16,1,0,9,100], then sorting gives the output.
```

## Understanding & Intuition
Negative numbers can produce large squares, so the largest square is often at either end. Filling the answer from right to left lets us place the current largest square first.

## Approach 1 — Naive / Brute Force
**Idea:** Square every value and sort the result.
```python
from typing import List

class Solution:
    def sortedSquares(self, nums: List[int]) -> List[int]:
        return sorted(x * x for x in nums)
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Split negative and non-negative squares, then merge two sorted lists.
```python
from typing import List

class Solution:
    def sortedSquares(self, nums: List[int]) -> List[int]:
        neg = []
        pos = []
        for x in nums:
            if x < 0:
                neg.append(x * x)
            else:
                pos.append(x * x)
        neg.reverse()
        i = j = 0
        result = []
        while i < len(neg) or j < len(pos):
            if j == len(pos) or (i < len(neg) and neg[i] <= pos[j]):
                result.append(neg[i])
                i += 1
            else:
                result.append(pos[j])
                j += 1
        return result
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Compare absolute values at both ends and fill the output from the back.
```python
from typing import List

class Solution:
    def sortedSquares(self, nums: List[int]) -> List[int]:
        n = len(nums)
        result = [0] * n
        left, right = 0, n - 1
        for write in range(n - 1, -1, -1):
            if abs(nums[left]) > abs(nums[right]):
                result[write] = nums[left] * nums[left]
                left += 1
            else:
                result[write] = nums[right] * nums[right]
                right -= 1
        return result
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n log n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- All-negative arrays should still sort by absolute value.
- Equal absolute values can choose either side.
- Output requires a new array on LeetCode.

## Related
- Merge Sorted Array
- Two Sum II
