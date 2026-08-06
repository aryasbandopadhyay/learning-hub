# 04. Maximum Sum of 3 Non-Overlapping Subarrays

- **Difficulty:** Hard
- **Pattern:** Arrays & Prefix Sums
- **Asked at:** Google, Facebook, Amazon

## Problem
Given an integer array `nums` and integer `k`, choose three non-overlapping subarrays of length `k` with maximum total sum. Return the starting indices of the three subarrays in lexicographically smallest order among optimal answers. `1 <= nums[i] < 2^16`, `1 <= k`, and `3 * k <= len(nums) <= 2 * 10^4`.

## Examples
```text
Input: nums = [1,2,1,2,6,7,5,1], k = 2
Output: [0,3,5]
Explanation: The chosen subarray sums are 3, 8, and 12 for total 23.
```

## Understanding & Intuition
Prefix sums let every length-`k` window sum be read in O(1). The challenge is selecting three windows without overlap while preserving lexicographic tie-breaking. Precomputing best windows to the left and right of each middle window makes the choice linear.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate all valid triples of starting indices and keep the best total.
```python
class Solution:
    def maxSumOfThreeSubarrays(self, nums: list[int], k: int) -> list[int]:
        n = len(nums)
        prefix = [0]
        for x in nums:
            prefix.append(prefix[-1] + x)
        def window(i):
            return prefix[i + k] - prefix[i]
        best_sum = -1
        ans = []
        for i in range(0, n - 3 * k + 1):
            for j in range(i + k, n - 2 * k + 1):
                for l in range(j + k, n - k + 1):
                    total = window(i) + window(j) + window(l)
                    cand = [i, j, l]
                    if total > best_sum or (total == best_sum and cand < ans):
                        best_sum = total
                        ans = cand
        return ans
```
- **Time:** O(n^3) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Enumerate a middle window and scan all possible left and right windows for it.
```python
class Solution:
    def maxSumOfThreeSubarrays(self, nums: list[int], k: int) -> list[int]:
        n = len(nums)
        prefix = [0]
        for x in nums:
            prefix.append(prefix[-1] + x)
        def window(i):
            return prefix[i + k] - prefix[i]
        best_sum = -1
        ans = []
        for mid in range(k, n - 2 * k + 1):
            best_left_sum = -1
            best_left = 0
            for left in range(0, mid - k + 1):
                s = window(left)
                if s > best_left_sum:
                    best_left_sum = s
                    best_left = left
            best_right_sum = -1
            best_right = mid + k
            for right in range(mid + k, n - k + 1):
                s = window(right)
                if s > best_right_sum:
                    best_right_sum = s
                    best_right = right
            total = best_left_sum + window(mid) + best_right_sum
            cand = [best_left, mid, best_right]
            if total > best_sum or (total == best_sum and cand < ans):
                best_sum = total
                ans = cand
        return ans
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Precompute best left and right windows, then test each middle window once.
```python
class Solution:
    def maxSumOfThreeSubarrays(self, nums: list[int], k: int) -> list[int]:
        n = len(nums)
        sums = []
        cur = sum(nums[:k])
        sums.append(cur)
        for i in range(k, n):
            cur += nums[i] - nums[i - k]
            sums.append(cur)
        m = len(sums)
        left = [0] * m
        best = 0
        for i in range(m):
            if sums[i] > sums[best]:
                best = i
            left[i] = best
        right = [0] * m
        best = m - 1
        for i in range(m - 1, -1, -1):
            if sums[i] >= sums[best]:
                best = i
            right[i] = best
        best_sum = -1
        ans = []
        for mid in range(k, m - k):
            l = left[mid - k]
            r = right[mid + k]
            total = sums[l] + sums[mid] + sums[r]
            cand = [l, mid, r]
            if total > best_sum or (total == best_sum and cand < ans):
                best_sum = total
                ans = cand
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Right-side tie handling uses `>=` when scanning backward.
- Left-side tie handling uses `>` to preserve the earliest left index.
- Middle starts range from `k` through `m - k - 1`.

## Related
- Maximum Sum of Two Non-Overlapping Subarrays
- Split Array Largest Sum
