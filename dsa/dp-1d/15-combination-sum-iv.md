# 15. Combination Sum IV

- **Difficulty:** Medium
- **Pattern:** 1-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Airbnb

## Problem
Given distinct positive integers `nums` and target `target`, return the number of possible ordered combinations that add up to `target`. Constraints: `1 <= nums.length <= 200`, `1 <= nums[i] <= 1000`, `1 <= target <= 1000`.

## Examples
```text
Input: nums = [1,2,3], target = 4
Output: 7
Explanation: The ordered combinations are (1,1,1,1), (1,1,2), (1,2,1), (1,3), (2,1,1), (2,2), and (3,1).
```

## Understanding & Intuition
Let `dp[t]` be the number of ordered combinations summing to `t`. Because order matters, for each target total we try every number as the last chosen value. Recurrence: `dp[t] += dp[t - num]` for every `num <= t`.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively choose the next number until the remaining target reaches zero.
```python
from typing import List

class Solution:
    def combinationSum4(self, nums: List[int], target: int) -> int:
        def dfs(rem: int) -> int:
            if rem == 0:
                return 1
            if rem < 0:
                return 0
            return sum(dfs(rem - num) for num in nums)

        return dfs(target)
```
- **Time:** O(k^target) — **Space:** O(target)

## Approach 2 — Better
**Idea:** Memoize the number of ordered combinations for each remaining target.
```python
from typing import List

class Solution:
    def combinationSum4(self, nums: List[int], target: int) -> int:
        memo = {0: 1}

        def dfs(rem: int) -> int:
            if rem < 0:
                return 0
            if rem not in memo:
                memo[rem] = sum(dfs(rem - num) for num in nums)
            return memo[rem]

        return dfs(target)
```
- **Time:** O(target * k) — **Space:** O(target)

## Approach 3 — Optimal
**Idea:** Bottom-up count ordered combinations by increasing total.
```python
from typing import List

class Solution:
    def combinationSum4(self, nums: List[int], target: int) -> int:
        dp = [0] * (target + 1)
        dp[0] = 1
        for total in range(1, target + 1):
            for num in nums:
                if total >= num:
                    dp[total] += dp[total - num]
        return dp[target]
```
- **Time:** O(target * k) — **Space:** O(target)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(k^target) | O(target) |
| Better | O(target * k) | O(target) |
| Optimal | O(target * k) | O(target) |

## Edge Cases & Pitfalls
- Order matters, so iterate target totals outside and numbers inside.
- All numbers are positive, preventing infinite recursion.

## Related
- Coin Change
- Combination Sum
