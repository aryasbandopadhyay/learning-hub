# 12. Partition Equal Subset Sum

- **Difficulty:** Medium
- **Pattern:** 1-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Facebook

## Problem
Given positive integers `nums`, return whether they can be partitioned into two subsets with equal sum. Constraints: `1 <= nums.length <= 200`, `1 <= nums[i] <= 100`.

## Examples
```text
Input: nums = [1,5,11,5]
Output: True
Explanation: The array can be partitioned as [1,5,5] and [11].
```

## Understanding & Intuition
Equal partition is possible only when the total sum is even. Then the task becomes finding a subset with sum `total / 2`. Let `dp[s]` mean sum `s` is reachable using processed numbers.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively choose include-or-skip for each number toward the target.
```python
from typing import List

class Solution:
    def canPartition(self, nums: List[int]) -> bool:
        total = sum(nums)
        if total % 2:
            return False
        target = total // 2

        def dfs(i: int, rem: int) -> bool:
            if rem == 0:
                return True
            if i == len(nums) or rem < 0:
                return False
            return dfs(i + 1, rem - nums[i]) or dfs(i + 1, rem)

        return dfs(0, target)
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize `(index, remaining_sum)` states.
```python
from typing import List

class Solution:
    def canPartition(self, nums: List[int]) -> bool:
        total = sum(nums)
        if total % 2:
            return False
        target = total // 2
        memo = {}

        def dfs(i: int, rem: int) -> bool:
            if rem == 0:
                return True
            if i == len(nums) or rem < 0:
                return False
            key = (i, rem)
            if key not in memo:
                memo[key] = dfs(i + 1, rem - nums[i]) or dfs(i + 1, rem)
            return memo[key]

        return dfs(0, target)
```
- **Time:** O(n * target) — **Space:** O(n * target)

## Approach 3 — Optimal
**Idea:** Use 1-D subset-sum DP and iterate sums backward for each number.
```python
from typing import List

class Solution:
    def canPartition(self, nums: List[int]) -> bool:
        total = sum(nums)
        if total % 2:
            return False
        target = total // 2
        dp = [False] * (target + 1)
        dp[0] = True
        for num in nums:
            for s in range(target, num - 1, -1):
                dp[s] = dp[s] or dp[s - num]
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
- Odd total sum immediately returns false.
- Iterate sums backward so each number is used once.

## Related
- Target Sum
- Last Stone Weight II
