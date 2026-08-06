# 10. Make Sum Divisible by P

- **Difficulty:** Medium
- **Pattern:** Arrays & Prefix Sums
- **Asked at:** Google, Amazon, Microsoft

## Problem
Given an array of positive integers `nums` and positive integer `p`, remove the shortest non-empty subarray so that the sum of the remaining elements is divisible by `p`. Return the length of the shortest subarray, or `-1` if impossible. You may not remove the whole array. `1 <= len(nums) <= 10^5`, `1 <= nums[i] <= 10^9`, and `1 <= p <= 10^9`.

## Examples
```text
Input: nums = [3,1,4,2], p = 6
Output: 1
Explanation: Removing [4] leaves sum 6, which is divisible by 6.
```

## Understanding & Intuition
Let `need = sum(nums) % p`; the removed subarray must have sum congruent to `need` modulo `p`. Prefix remainders let us find the shortest subarray ending at each index with that modular sum. Storing the latest index for each remainder minimizes length.

## Approach 1 — Naive / Brute Force
**Idea:** Try every removable subarray and test the remaining sum.
```python
class Solution:
    def minSubarray(self, nums: list[int], p: int) -> int:
        total = sum(nums)
        if total % p == 0:
            return 0
        n = len(nums)
        best = n
        for i in range(n):
            removed = 0
            for j in range(i, n):
                removed += nums[j]
                if j - i + 1 < n and (total - removed) % p == 0:
                    best = min(best, j - i + 1)
        return -1 if best == n else best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Build prefix sums and compare all prefix pairs by the needed modulo difference.
```python
class Solution:
    def minSubarray(self, nums: list[int], p: int) -> int:
        need = sum(nums) % p
        if need == 0:
            return 0
        prefix = [0]
        for x in nums:
            prefix.append((prefix[-1] + x) % p)
        n = len(nums)
        best = n
        for i in range(n):
            for j in range(i + 1, n + 1):
                if j - i < n and (prefix[j] - prefix[i]) % p == need:
                    best = min(best, j - i)
        return -1 if best == n else best
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Track latest prefix remainder; for current remainder `cur`, find a prior remainder `(cur - need) % p`.
```python
class Solution:
    def minSubarray(self, nums: list[int], p: int) -> int:
        need = sum(nums) % p
        if need == 0:
            return 0
        seen = {0: -1}
        cur = 0
        best = len(nums)
        for i, x in enumerate(nums):
            cur = (cur + x) % p
            want = (cur - need) % p
            if want in seen:
                best = min(best, i - seen[want])
            seen[cur] = i
        return -1 if best == len(nums) else best
```
- **Time:** O(n) — **Space:** O(min(n, p))

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n^2) | O(n) |
| Optimal | O(n) | O(min(n, p)) |

## Edge Cases & Pitfalls
- Return `0` when the total is already divisible by `p`.
- The whole array cannot be removed.
- Use latest remainder positions to get the shortest candidate.

## Related
- Continuous Subarray Sum
- Subarray Sum Equals K
