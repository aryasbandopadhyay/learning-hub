# 04. House Robber II

- **Difficulty:** Medium
- **Pattern:** 1-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Bloomberg

## Problem
Houses are arranged in a circle, so the first and last houses are adjacent. Return the maximum amount you can rob without robbing adjacent houses. Constraints: `1 <= nums.length <= 100`, `0 <= nums[i] <= 1000`.

## Examples
```text
Input: nums = [2,3,2]
Output: 3
Explanation: Rob only the middle house.
```

## Understanding & Intuition
The circle prevents taking both first and last houses. Split the problem into two linear robber cases: exclude the last house, or exclude the first house. The answer is the maximum of those two linear DP results.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively solve each allowed linear range without memoization.
```python
from typing import List

class Solution:
    def rob(self, nums: List[int]) -> int:
        if len(nums) == 1:
            return nums[0]

        def dfs(lo: int, hi: int, i: int) -> int:
            if i > hi:
                return 0
            return max(nums[i] + dfs(lo, hi, i + 2), dfs(lo, hi, i + 1))

        return max(dfs(0, len(nums) - 2, 0), dfs(1, len(nums) - 1, 1))
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize each linear range.
```python
from typing import List

class Solution:
    def rob(self, nums: List[int]) -> int:
        if len(nums) == 1:
            return nums[0]

        def solve(lo: int, hi: int) -> int:
            memo = {}

            def dfs(i: int) -> int:
                if i > hi:
                    return 0
                if i not in memo:
                    memo[i] = max(nums[i] + dfs(i + 2), dfs(i + 1))
                return memo[i]

            return dfs(lo)

        return max(solve(0, len(nums) - 2), solve(1, len(nums) - 1))
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Run the linear space-optimized robber DP on both valid ranges.
```python
from typing import List

class Solution:
    def rob(self, nums: List[int]) -> int:
        if len(nums) == 1:
            return nums[0]

        def rob_line(arr: List[int]) -> int:
            rob_prev, skip_prev = 0, 0
            for money in arr:
                rob_prev, skip_prev = skip_prev + money, max(rob_prev, skip_prev)
            return max(rob_prev, skip_prev)

        return max(rob_line(nums[:-1]), rob_line(nums[1:]))
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Handle one house before slicing ranges.
- Do not allow first and last houses in the same case.

## Related
- House Robber
- House Robber III
