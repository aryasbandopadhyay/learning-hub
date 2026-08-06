# 11. Longest Increasing Subsequence

- **Difficulty:** Medium
- **Pattern:** 1-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Meta

## Problem
Given integer array `nums`, return the length of the longest strictly increasing subsequence. Constraints: `1 <= nums.length <= 2500`, `-10^4 <= nums[i] <= 10^4`.

## Examples
```text
Input: nums = [10,9,2,5,3,7,101,18]
Output: 4
Explanation: One LIS is [2,3,7,101].
```

## Understanding & Intuition
Let `dp[i]` be the LIS length starting at index `i`. You may take a later index `j` only if `nums[j] > nums[i]`, giving `1 + dp[j]`. The answer is the maximum over all starts.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively decide whether to take each element based on the previous chosen value.
```python
from typing import List

class Solution:
    def lengthOfLIS(self, nums: List[int]) -> int:
        def dfs(i: int, prev: int) -> int:
            if i == len(nums):
                return 0
            skip = dfs(i + 1, prev)
            take = 0
            if prev == -1 or nums[i] > nums[prev]:
                take = 1 + dfs(i + 1, i)
            return max(take, skip)

        return dfs(0, -1)
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize `(i, prev_index)` decisions.
```python
from typing import List

class Solution:
    def lengthOfLIS(self, nums: List[int]) -> int:
        memo = {}

        def dfs(i: int, prev: int) -> int:
            if i == len(nums):
                return 0
            key = (i, prev)
            if key not in memo:
                best = dfs(i + 1, prev)
                if prev == -1 or nums[i] > nums[prev]:
                    best = max(best, 1 + dfs(i + 1, i))
                memo[key] = best
            return memo[key]

        return dfs(0, -1)
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Bottom-up 1-D DP for LIS starting at each index.
```python
from typing import List

class Solution:
    def lengthOfLIS(self, nums: List[int]) -> int:
        n = len(nums)
        dp = [1] * n
        for i in range(n - 1, -1, -1):
            for j in range(i + 1, n):
                if nums[j] > nums[i]:
                    dp[i] = max(dp[i], 1 + dp[j])
        return max(dp)
```
- **Time:** O(n^2) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n^2) | O(n^2) |
| Optimal | O(n^2) | O(n) |

## Edge Cases & Pitfalls
- The subsequence must be strictly increasing, not non-decreasing.
- Elements do not need to be contiguous.

## Related
- Russian Doll Envelopes
- Maximum Length of Pair Chain
