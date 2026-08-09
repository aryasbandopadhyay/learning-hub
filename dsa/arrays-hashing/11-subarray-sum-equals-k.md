# 11. Subarray Sum Equals K

- **Difficulty:** Medium
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given an integer array `nums` and an integer `k`, count how many contiguous, non-empty subarrays have sum exactly `k`.

**Input**
- `nums`: a list of integers.
- `k`: the target subarray sum.

**Output**
- The number of contiguous subarrays whose elements sum to `k`.

## Constraints
- `1 <= nums.length <= 2 * 10^4`
- `-1000 <= nums[i] <= 1000`
- `-10^7 <= k <= 10^7`

## Examples
```text
Input: nums = [1,1,1], k = 2
Output: 2
Explanation: The subarrays `nums[0..1]` and `nums[1..2]` both sum to `2`, so there are two valid subarrays.
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
