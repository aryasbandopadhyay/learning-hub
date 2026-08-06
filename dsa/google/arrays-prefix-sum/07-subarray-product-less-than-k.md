# 07. Subarray Product Less Than K

- **Difficulty:** Medium
- **Pattern:** Arrays & Prefix Sums
- **Asked at:** Google, Facebook, Amazon

## Problem
Given an array of positive integers `nums` and an integer `k`, return the number of contiguous subarrays where the product of all elements is strictly less than `k`. `1 <= len(nums) <= 3 * 10^4`, `1 <= nums[i] <= 1000`, and `0 <= k <= 10^6`.

## Examples
```text
Input: nums = [10,5,2,6], k = 100
Output: 8
Explanation: The valid subarrays are [10], [5], [2], [6], [10,5], [5,2], [2,6], and [5,2,6].
```

## Understanding & Intuition
Because all numbers are positive, increasing the right boundary never decreases the product. This monotonicity allows a sliding window that shrinks from the left until the product is valid. Every suffix of a valid window ending at `right` is also valid.

## Approach 1 — Naive / Brute Force
**Idea:** Compute each subarray product and count those below `k`.
```python
class Solution:
    def numSubarrayProductLessThanK(self, nums: list[int], k: int) -> int:
        ans = 0
        n = len(nums)
        for i in range(n):
            prod = 1
            for j in range(i, n):
                prod *= nums[j]
                if prod < k:
                    ans += 1
        return ans
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Stop extending a start once the product reaches `k`, since all future products only grow or stay the same.
```python
class Solution:
    def numSubarrayProductLessThanK(self, nums: list[int], k: int) -> int:
        if k <= 1:
            return 0
        ans = 0
        n = len(nums)
        for i in range(n):
            prod = 1
            for j in range(i, n):
                prod *= nums[j]
                if prod >= k:
                    break
                ans += 1
        return ans
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Keep a product window below `k`; after shrinking, all subarrays ending at `right` and starting within the window are valid.
```python
class Solution:
    def numSubarrayProductLessThanK(self, nums: list[int], k: int) -> int:
        if k <= 1:
            return 0
        prod = 1
        left = 0
        ans = 0
        for right, x in enumerate(nums):
            prod *= x
            while prod >= k:
                prod //= nums[left]
                left += 1
            ans += right - left + 1
        return ans
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n^2) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- If `k <= 1`, no positive product can be strictly less than `k`.
- Count `right - left + 1` subarrays after shrinking.
- This sliding window depends on all values being positive.

## Related
- Minimum Size Subarray Sum
- Fruit Into Baskets
