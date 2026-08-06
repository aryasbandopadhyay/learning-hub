# 03. House Robber

- **Difficulty:** Medium
- **Pattern:** 1-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Meta

## Problem
Given `nums`, where `nums[i]` is money in house `i`, return the maximum amount you can rob without robbing adjacent houses. Constraints: `1 <= nums.length <= 100`, `0 <= nums[i] <= 400`.

## Examples
```text
Input: nums = [1,2,3,1]
Output: 4
Explanation: Rob houses 0 and 2 for 1 + 3 = 4.
```

## Understanding & Intuition
Let `dp[i]` be the best amount from houses `i..end`. At each house, either rob it and skip the next, or skip it. Recurrence: `dp[i] = max(nums[i] + dp[i+2], dp[i+1])`.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively choose rob-or-skip for each index.
```python
from typing import List

class Solution:
    def rob(self, nums: List[int]) -> int:
        def dfs(i: int) -> int:
            if i >= len(nums):
                return 0
            return max(nums[i] + dfs(i + 2), dfs(i + 1))

        return dfs(0)
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize the best answer starting at each index.
```python
from typing import List

class Solution:
    def rob(self, nums: List[int]) -> int:
        memo = {}

        def dfs(i: int) -> int:
            if i >= len(nums):
                return 0
            if i not in memo:
                memo[i] = max(nums[i] + dfs(i + 2), dfs(i + 1))
            return memo[i]

        return dfs(0)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Iterate once, keeping the best totals when excluding or including the current house.
```python
from typing import List

class Solution:
    def rob(self, nums: List[int]) -> int:
        rob_prev, skip_prev = 0, 0
        for money in nums:
            new_rob = skip_prev + money
            new_skip = max(skip_prev, rob_prev)
            rob_prev, skip_prev = new_rob, new_skip
        return max(rob_prev, skip_prev)
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- A single house returns its value.
- Never add adjacent houses together.

## Related
- House Robber II
- Delete and Earn
