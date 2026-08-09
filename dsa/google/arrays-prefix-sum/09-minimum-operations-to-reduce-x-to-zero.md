# 09. Minimum Operations to Reduce X to Zero

- **Difficulty:** Medium
- **Pattern:** Arrays & Prefix Sums
- **Asked at:** Google, Amazon, Bloomberg

## Problem
You are given an integer array `nums` and an integer `x`.

In one operation, remove the leftmost or rightmost element and subtract its value from `x`. Return the fewest operations needed to make `x` exactly `0`, or `-1` if impossible.

**Input**
- `nums`: a list of positive integers.
- `x`: the target amount to remove from the ends.

**Output**
- The minimum number of end removals, or `-1` if no sequence reaches exactly zero.

## Constraints
- `1 <= nums.length <= 10^5`
- `1 <= nums[i] <= 10^4`
- `1 <= x <= 10^9`

## Examples
```text
Input: nums = [1,1,4,2,3], x = 5
Output: 2
Explanation: Remove `2` and then `3` from the right side. Their sum is `5`, so `x` becomes zero in two operations.
```

## Understanding & Intuition
Removing ends leaves a contiguous middle subarray. If the removed sum is `x`, the kept middle sum is `sum(nums) - x`; minimizing removals means keeping the longest such subarray. Since all numbers are positive, a sliding window finds that longest subarray.

## Approach 1 — Naive / Brute Force
**Idea:** Try every count removed from the left and right and compute its removed sum.
```python
class Solution:
    def minOperations(self, nums: list[int], x: int) -> int:
        n = len(nums)
        best = n + 1
        for left in range(n + 1):
            for right in range(n - left + 1):
                total = 0
                for i in range(left):
                    total += nums[i]
                for i in range(n - right, n):
                    total += nums[i]
                if total == x:
                    best = min(best, left + right)
        return -1 if best == n + 1 else best
```
- **Time:** O(n^3) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Precompute left removal sums, then scan right removal sums for complements.
```python
class Solution:
    def minOperations(self, nums: list[int], x: int) -> int:
        n = len(nums)
        left_sums = {0: 0}
        total = 0
        for i, val in enumerate(nums):
            total += val
            if total not in left_sums:
                left_sums[total] = i + 1
        best = n + 1
        total = 0
        for right in range(0, n + 1):
            need = x - total
            if need in left_sums and left_sums[need] + right <= n:
                best = min(best, left_sums[need] + right)
            if right < n:
                total += nums[n - 1 - right]
        return -1 if best == n + 1 else best
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Convert to finding the longest middle subarray with sum `sum(nums) - x` using a positive-number sliding window.
```python
class Solution:
    def minOperations(self, nums: list[int], x: int) -> int:
        target = sum(nums) - x
        if target < 0:
            return -1
        if target == 0:
            return len(nums)
        left = 0
        total = 0
        best_len = -1
        for right, val in enumerate(nums):
            total += val
            while total > target and left <= right:
                total -= nums[left]
                left += 1
            if total == target:
                best_len = max(best_len, right - left + 1)
        return -1 if best_len == -1 else len(nums) - best_len
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- If `sum(nums) < x`, the answer is impossible.
- If `sum(nums) == x`, every element must be removed.
- The sliding window relies on all values being positive.

## Related
- Maximum Points You Can Obtain from Cards
- Minimum Size Subarray Sum
