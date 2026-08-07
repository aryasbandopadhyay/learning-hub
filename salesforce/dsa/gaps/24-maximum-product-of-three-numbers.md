# 24. Maximum Product of Three Numbers

- **Difficulty:** Easy
- **Pattern:** Arrays / Math
- **Asked at:** Salesforce, Amazon, Microsoft

## Problem
Return the maximum product obtainable by multiplying any three numbers in the array.

## Examples
```text
Input: nums = [-10,-10,5,2]
Output: 500
Explanation: The two negatives and the largest positive produce 500.
```

## Understanding & Intuition
The best product is either the three largest values or the two smallest values times the largest value.

## Approach 1 — Naive / Brute Force
**Idea:** Test every triple.
```python
class Solution:
    def maximumProduct(self, nums: list[int]) -> int:
        best = -10**18; n = len(nums)
        for i in range(n):
            for j in range(i + 1, n):
                for k in range(j + 1, n): best = max(best, nums[i] * nums[j] * nums[k])
        return best
```
- **Time:** O(n^3) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Sort and compare the two extreme candidates.
```python
class Solution:
    def maximumProduct(self, nums: list[int]) -> int:
        nums.sort()
        return max(nums[-1] * nums[-2] * nums[-3], nums[0] * nums[1] * nums[-1])
```
- **Time:** O(n log n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Track the top three maximums and bottom two minimums in one pass.
```python
class Solution:
    def maximumProduct(self, nums: list[int]) -> int:
        a = b = c = -10**18; x = y = 10**18
        for v in nums:
            if v >= a: a, b, c = v, a, b
            elif v >= b: b, c = v, b
            elif v > c: c = v
            if v <= x: x, y = v, x
            elif v < y: y = v
        return max(a * b * c, x * y * a)
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(1) |
| Better | O(n log n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Two negatives can create the largest positive product.
- Zeros can be part of the best product.
- Arrays have at least three values.

## Related
- Third Maximum Number
- Product of Array Except Self
