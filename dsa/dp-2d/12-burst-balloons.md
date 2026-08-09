# 12. Burst Balloons

- **Difficulty:** Hard
- **Pattern:** 2-D Dynamic Programming
- **Asked at:** Google, Amazon, Microsoft, Meta

## Problem
You have balloons labeled by `nums`. When you burst balloon `i`, you gain
`nums[left] * nums[i] * nums[right]` coins, where `left` and `right` are the nearest still-unburst
balloons on each side. If there is no balloon on a side, use value `1` for that side.

Return the maximum coins obtainable by choosing the burst order optimally.

**Input**
- `nums`: a list of balloon values.

**Output**
- An integer: the maximum number of coins.

## Constraints
- 1 <= nums.length <= 300
- 0 <= nums[i] <= 100

## Examples
```text
Input: nums = [3,1,5,8]
Output: 167
Explanation: One optimal order bursts `1`, then `5`, then `3`, then `8`, earning `3*1*5 + 3*5*8 + 1*3*8 + 1*8*1 = 167`.
```

## Understanding & Intuition
Use interval DP: `dp[l][r]` is max coins from bursting balloons strictly between boundaries `l` and `r`. Choose which balloon `k` is burst last in that interval, so its neighbors are exactly `l` and `r`. Then combine left interval, right interval, and boundary product.

## Approach 1 — Naive / Brute Force
**Idea:** (recursion)
```python
from typing import List

class SolutionRecursive:
    def maxCoins(self, nums: List[int]) -> int:
        vals = [1] + nums + [1]

        def dfs(l: int, r: int) -> int:
            if l + 1 == r:
                return 0
            best = 0
            for k in range(l + 1, r):
                coins = vals[l] * vals[k] * vals[r]
                best = max(best, dfs(l, k) + coins + dfs(k, r))
            return best

        return dfs(0, len(vals) - 1)
```
- **Time:** O(n!) — **Space:** O(n)

## Approach 2 — Better
**Idea:** (memoization)
```python
from typing import List

class SolutionMemoized:
    def maxCoins(self, nums: List[int]) -> int:
        vals = [1] + nums + [1]
        memo = {}

        def dfs(l: int, r: int) -> int:
            if l + 1 == r:
                return 0
            if (l, r) not in memo:
                memo[(l, r)] = 0
                for k in range(l + 1, r):
                    coins = vals[l] * vals[k] * vals[r]
                    memo[(l, r)] = max(memo[(l, r)], dfs(l, k) + coins + dfs(k, r))
            return memo[(l, r)]

        return dfs(0, len(vals) - 1)
```
- **Time:** O(n^3) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** (tabulation, space-optimized where possible)
```python
from typing import List

class Solution:
    def maxCoins(self, nums: List[int]) -> int:
        vals = [1] + nums + [1]
        n = len(vals)
        dp = [[0] * n for _ in range(n)]
        for length in range(2, n):
            for l in range(0, n - length):
                r = l + length
                for k in range(l + 1, r):
                    coins = vals[l] * vals[k] * vals[r]
                    dp[l][r] = max(dp[l][r], dp[l][k] + coins + dp[k][r])
        return dp[0][n - 1]
```
- **Time:** O(n^3) — **Space:** O(n^2)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n!) | O(n) |
| Better | O(n^3) | O(n^2) |
| Optimal | O(n^3) | O(n^2) |

## Edge Cases & Pitfalls
- Think of choosing the last burst balloon, not the first.
- Keep the virtual boundary balloons with value one.

## Related
- Matrix Chain Multiplication
- Palindrome Partitioning
