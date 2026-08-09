# 01. Continuous Subarray Sum

- **Difficulty:** Medium
- **Pattern:** Arrays & Prefix Sums
- **Asked at:** Google, Meta, Amazon

## Problem
You are given an integer array `nums` and a positive integer `k`.

Return `True` if `nums` contains a contiguous subarray of length at least two whose sum is a multiple of `k`; otherwise return `False`. A multiple may be `0`, `k`, `2k`, and so on.

**Input**
- `nums`: a list of integers.
- `k`: the divisor used to test the subarray sum.

**Output**
- `True` if such a subarray exists; otherwise `False`.

## Constraints
- `1 <= nums.length <= 10^5`
- `0 <= nums[i] <= 10^9`
- `1 <= k <= 2^31 - 1`

## Examples
```text
Input: nums = [23,2,4,6,7], k = 6
Output: True
Explanation: The subarray `[2,4]` has sum `6`, which is a multiple of `6`, and its length is at least two.
```

## Understanding & Intuition
A subarray sum is divisible by `k` when two prefix sums have the same remainder modulo `k`. The length condition means the earlier prefix index must be at least two positions before the current element. Keeping the first index for each remainder maximizes the valid gap.

## Approach 1 — Naive / Brute Force
**Idea:** Try every subarray of length at least two and test divisibility.
```python
class Solution:
    def checkSubarraySum(self, nums: list[int], k: int) -> bool:
        n = len(nums)
        for i in range(n):
            total = 0
            for j in range(i, n):
                total += nums[j]
                if j - i + 1 >= 2 and total % k == 0:
                    return True
        return False
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Build prefix sums and compare all valid prefix pairs.
```python
class Solution:
    def checkSubarraySum(self, nums: list[int], k: int) -> bool:
        prefix = [0]
        for x in nums:
            prefix.append(prefix[-1] + x)
        n = len(nums)
        for i in range(n - 1):
            for j in range(i + 2, n + 1):
                if (prefix[j] - prefix[i]) % k == 0:
                    return True
        return False
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Store the earliest prefix index for each remainder and check whether the gap is at least two.
```python
class Solution:
    def checkSubarraySum(self, nums: list[int], k: int) -> bool:
        first = {0: -1}
        total = 0
        for i, x in enumerate(nums):
            total = (total + x) % k
            if total in first:
                if i - first[total] >= 2:
                    return True
            else:
                first[total] = i
        return False
```
- **Time:** O(n) — **Space:** O(min(n, k))

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n^2) | O(n) |
| Optimal | O(n) | O(min(n, k)) |

## Edge Cases & Pitfalls
- The subarray must contain at least two elements.
- Keep the first index for each remainder, not the latest.
- Initialize remainder `0` at index `-1`.

## Related
- Subarray Sum Equals K
- Make Sum Divisible by P
