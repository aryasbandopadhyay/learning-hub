# 06. Product of Array Except Self

- **Difficulty:** Medium
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Return an array where each position is the product of all other values. Do not use division for the optimal solution. Constraints: `2 <= n <= 10^5`.

## Examples
```text
Input: nums = [1,2,3,4]
Output: [24,12,8,6]
Explanation: Each result excludes its own value.
```

## Understanding & Intuition
Each answer is left product times right product. Prefix and suffix arrays teach the idea; storing prefix in the output and rolling suffix saves extra space.

## Approach 1 — Naive / Brute Force
**Idea:** For each index, multiply every other index.
```python
class Solution:
    def productExceptSelf(self, nums: list[int]) -> list[int]:
        ans = []
        for i in range(len(nums)):
            prod = 1
            for j, x in enumerate(nums):
                if i != j:
                    prod *= x
            ans.append(prod)
        return ans
```
- **Time:** O(n^2) — **Space:** O(1) extra

## Approach 2 — Better
**Idea:** Build separate prefix and suffix product arrays.
```python
class Solution:
    def productExceptSelf(self, nums: list[int]) -> list[int]:
        n = len(nums)
        pre = [1] * n
        suf = [1] * n
        for i in range(1, n):
            pre[i] = pre[i - 1] * nums[i - 1]
        for i in range(n - 2, -1, -1):
            suf[i] = suf[i + 1] * nums[i + 1]
        return [pre[i] * suf[i] for i in range(n)]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use output for prefixes and multiply by a rolling suffix.
```python
class Solution:
    def productExceptSelf(self, nums: list[int]) -> list[int]:
        ans = [1] * len(nums)
        left = 1
        for i, x in enumerate(nums):
            ans[i] = left
            left *= x
        right = 1
        for i in range(len(nums) - 1, -1, -1):
            ans[i] *= right
            right *= nums[i]
        return ans
```
- **Time:** O(n) — **Space:** O(1) extra

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) extra |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) extra |

## Edge Cases & Pitfalls
- Zeros are handled naturally.
- Initialize products to 1.
- Output array is usually excluded from extra-space count.

## Related
- Maximum Product Subarray
- Trapping Rain Water
