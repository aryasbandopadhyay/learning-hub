# 08. Count Subsets With Sum

- **Difficulty:** Medium
- **Pattern:** subset sum counting DP
- **Asked at:** Amazon, Microsoft, Google

## Problem
Given non-negative integers `nums` and integer `target`, return how many subsets have sum exactly `target`. Different index choices count separately. Constraints: `1 <= len(nums) <= 40`, `0 <= nums[i] <= 1000`.

## Examples
```text
Input: nums = [2,3,5,6,8,10], target = 10
Output: 3
Explanation: The subsets are [10], [2,8], and [2,3,5].
```

## Understanding & Intuition
This is subset-sum, but the table stores counts instead of booleans. Zeros matter because choosing or skipping a zero creates two different subsets. Backward iteration preserves 0/1 usage.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate all subsets and count exact sums.
```python
class Solution:
    def countSubsets(self, nums: list[int], target: int) -> int:
        ans = 0
        def dfs(i: int, total: int) -> None:
            nonlocal ans
            if i == len(nums):
                if total == target:
                    ans += 1
                return
            dfs(i + 1, total)
            dfs(i + 1, total + nums[i])
        dfs(0, 0)
        return ans
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize by index and current sum.
```python
class Solution:
    def countSubsets(self, nums: list[int], target: int) -> int:
        from functools import lru_cache
        @lru_cache(None)
        def dp(i: int, total: int) -> int:
            if total > target:
                return 0
            if i == len(nums):
                return 1 if total == target else 0
            return dp(i + 1, total) + dp(i + 1, total + nums[i])
        return dp(0, 0)
```
- **Time:** O(n * target) — **Space:** O(n * target)

## Approach 3 — Optimal
**Idea:** Use a one-dimensional count table over sums.
```python
class Solution:
    def countSubsets(self, nums: list[int], target: int) -> int:
        dp = [0] * (target + 1)
        dp[0] = 1
        for x in nums:
            for s in range(target, x - 1, -1):
                dp[s] += dp[s - x]
        return dp[target]
```
- **Time:** O(n * target) — **Space:** O(target)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n * target) | O(n * target) |
| Optimal | O(n * target) | O(target) |

## Edge Cases & Pitfalls
- Non-negative numbers are required for this DP.
- Zeros double existing counts, which the backward loop handles.
- Pruning by `total > target` is valid only for non-negative inputs.

## Related
- Target Sum
- Coin Change II
