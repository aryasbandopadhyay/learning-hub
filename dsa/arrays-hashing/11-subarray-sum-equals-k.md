# 11. Subarray Sum Equals K

- **Difficulty:** Medium
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Return the number of contiguous subarrays whose sum equals `k`. Values may be negative; `n <= 2 * 10^4`.

## Examples
```text
Input: nums = [1,1,1], k = 2
Output: 2
Explanation: Two length-2 subarrays sum to 2.
```

## Understanding & Intuition
Prefix sums turn a subarray sum into a difference. Count prior prefixes equal to `current-k` to include duplicates and negative values.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate each subarray and recompute its sum.
```python
class Solution:
    def subarraySum(self, nums: list[int], k: int) -> int:
        ans = 0
        for l in range(len(nums)):
            for r in range(l, len(nums)):
                total = 0
                for i in range(l, r + 1):
                    total += nums[i]
                if total == k:
                    ans += 1
        return ans
```
- **Time:** O(n^3) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Fix a start and extend with a running sum.
```python
class Solution:
    def subarraySum(self, nums: list[int], k: int) -> int:
        ans = 0
        for l in range(len(nums)):
            total = 0
            for r in range(l, len(nums)):
                total += nums[r]
                if total == k:
                    ans += 1
        return ans
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Count prior prefix sums in a hash map.
```python
class Solution:
    def subarraySum(self, nums: list[int], k: int) -> int:
        counts = {0: 1}
        total = ans = 0
        for x in nums:
            total += x
            ans += counts.get(total - k, 0)
            counts[total] = counts.get(total, 0) + 1
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(1) |
| Better | O(n^2) | O(1) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Sliding window fails with negatives.
- Seed prefix 0 once.
- Store counts, not only existence.

## Related
- Contiguous Array
- Maximum Size Subarray Sum Equals k
