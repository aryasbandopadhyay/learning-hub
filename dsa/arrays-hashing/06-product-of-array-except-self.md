# 06. Product of Array Except Self

- **Difficulty:** Medium
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given an integer array `nums`, build `answer` where `answer[i]` is the product of every element except `nums[i]`. Do not use division.

**Input**
- `nums`: a list of integers.

**Output**
- A list of products in the same index order as `nums`. **This judge compares exactly** by index.

## Constraints
- `2 <= nums.length <= 10^5`
- `-30 <= nums[i] <= 30`
- The product of any prefix or suffix fits in a 32-bit signed integer.

## Examples
```text
Input: nums = [1,2,3,4]
Output: [24,12,8,6]
Explanation: For index `0`, multiply `2*3*4 = 24`; for index `1`, multiply `1*3*4 = 12`, and similarly for the other positions.
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
