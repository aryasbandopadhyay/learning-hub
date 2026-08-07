# 11. Find Pivot Index

- **Difficulty:** Easy
- **Pattern:** Prefix Sum
- **Asked at:** Salesforce, Amazon, Facebook

## Problem
Return the leftmost index whose left-side sum equals its right-side sum, or `-1` if none exists.

## Examples
```text
Input: nums = [1,7,3,6,5,6]
Output: 3
Explanation: 1+7+3 equals 5+6.
```

## Understanding & Intuition
At index `i`, right sum is `total - left - nums[i]`. Update the left sum after checking each index.

## Approach 1 — Naive / Brute Force
**Idea:** Recompute both sides for every index.
```python
class Solution:
    def pivotIndex(self, nums: list[int]) -> int:
        for i in range(len(nums)):
            if sum(nums[:i]) == sum(nums[i + 1:]):
                return i
        return -1
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Precompute prefix sums.
```python
class Solution:
    def pivotIndex(self, nums: list[int]) -> int:
        prefix = [0]
        for x in nums:
            prefix.append(prefix[-1] + x)
        total = prefix[-1]
        for i in range(len(nums)):
            if prefix[i] == total - prefix[i + 1]:
                return i
        return -1
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Store only the running left sum.
```python
class Solution:
    def pivotIndex(self, nums: list[int]) -> int:
        total = sum(nums); left = 0
        for i, x in enumerate(nums):
            if left == total - left - x:
                return i
            left += x
        return -1
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Return the leftmost pivot.
- Empty sides have sum 0.
- Negative values are allowed.

## Related
- Running Sum of 1d Array
- Subarray Sum Equals K
