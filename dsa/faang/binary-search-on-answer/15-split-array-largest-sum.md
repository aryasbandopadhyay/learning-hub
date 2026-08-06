# 15. Split Array Largest Sum

- **Difficulty:** Hard
- **Pattern:** Binary Search on the Answer
- **Asked at:** Google, Facebook, Microsoft

## Problem
Given an integer array `nums` and an integer `k`, split `nums` into `k` non-empty contiguous subarrays. Return the minimum possible value of the largest subarray sum. Constraints: `1 <= k <= len(nums) <= 1000`, `0 <= nums[i] <= 10^6`.

## Examples
```text
Input: nums = [7,2,5,10,8], k = 2
Output: 18
Explanation: Splitting as [7,2,5] and [10,8] makes the largest sum 18, which is minimal.
```

## Understanding & Intuition
Choosing split points determines the maximum subarray sum. A candidate largest sum is feasible if greedily starting a new subarray whenever necessary uses at most `k` subarrays. This feasibility is monotonic, so binary search applies.

## Approach 1 — Naive / Brute Force
**Idea:** scan possible largest sums from `max(nums)` upward until the greedy split count fits.
```python
class Solution:
    def splitArray(self, nums, k):
        def can_split(limit):
            pieces = 1
            total = 0
            for x in nums:
                if total + x > limit:
                    pieces += 1
                    total = 0
                total += x
            return pieces <= k

        for limit in range(max(nums), sum(nums) + 1):
            if can_split(limit):
                return limit
        return sum(nums)
```
- **Time:** O((sum(nums) - max(nums)) * n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** use prefix sums and dynamic programming over the number of parts and prefix length.
```python
class Solution:
    def splitArray(self, nums, k):
        n = len(nums)
        if k >= n:
            return max(nums)
        prefix = [0]
        for x in nums:
            prefix.append(prefix[-1] + x)
        prev = [0] + [prefix[i] for i in range(1, n + 1)]
        for parts in range(2, k + 1):
            curr = [0] * (n + 1)
            for i in range(1, n + 1):
                best = prefix[i]
                for cut in range(parts - 1, i):
                    best = min(best, max(prev[cut], prefix[i] - prefix[cut]))
                curr[i] = best
            prev = curr
        return prev[n]
```
- **Time:** O(k * n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** binary-search the answer and count how many subarrays are required for each maximum sum.
```python
class Solution:
    def splitArray(self, nums, k):
        def pieces_allowed(limit):
            pieces = 1
            total = 0
            for x in nums:
                if total + x > limit:
                    pieces += 1
                    total = 0
                total += x
            return pieces

        lo, hi = max(nums), sum(nums)
        while lo < hi:
            mid = (lo + hi) // 2
            if pieces_allowed(mid) <= k:
                hi = mid
            else:
                lo = mid + 1
        return lo
```
- **Time:** O(n log sum(nums)) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((sum(nums) - max(nums)) * n) | O(1) |
| Better | O(k * n^2) | O(n) |
| Optimal | O(n log sum(nums)) | O(1) |

## Edge Cases & Pitfalls
- Subarrays must be non-empty and contiguous.
- When `k == 1`, the answer is `sum(nums)`.
- When `k == len(nums)`, the answer is `max(nums)`.

## Related
- Capacity To Ship Packages Within D Days
- Allocate Minimum Number of Pages
