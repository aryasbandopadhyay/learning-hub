# 01. Two Sum

- **Difficulty:** Easy
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given integer array `nums` and target, return indices of two different elements whose values sum to target. Exactly one answer exists; `2 <= n <= 10^4`.

## Examples
```text
Input: nums = [2,7,11,15], target = 9
Output: [0,1]
Explanation: 2 + 7 = 9.
```

## Understanding & Intuition
A complement lookup turns pair search into membership testing. Sorting gives a middle tier, but a hash map finds the answer in one pass.

## Approach 1 — Naive / Brute Force
**Idea:** Check every pair until the sum matches.
```python
class Solution:
    def twoSum(self, nums: list[int], target: int) -> list[int]:
        for i in range(len(nums)):
            for j in range(i + 1, len(nums)):
                if nums[i] + nums[j] == target:
                    return [i, j]
        return []
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Sort value/index pairs and move two pointers.
```python
class Solution:
    def twoSum(self, nums: list[int], target: int) -> list[int]:
        pairs = sorted((x, i) for i, x in enumerate(nums))
        l, r = 0, len(pairs) - 1
        while l < r:
            s = pairs[l][0] + pairs[r][0]
            if s == target:
                return [pairs[l][1], pairs[r][1]]
            if s < target:
                l += 1
            else:
                r -= 1
        return []
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Store seen values and ask whether the complement was seen.
```python
class Solution:
    def twoSum(self, nums: list[int], target: int) -> list[int]:
        seen = {}
        for i, x in enumerate(nums):
            need = target - x
            if need in seen:
                return [seen[need], i]
            seen[x] = i
        return []
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Do not reuse the same index.
- Duplicates are valid at different indices.
- Check before inserting current value.

## Related
- 3Sum
- Two Sum II - Input Array Is Sorted
