# 05. Profitable Schemes

- **Difficulty:** Hard
- **Pattern:** two-dimensional capped knapsack counting
- **Asked at:** Google, Amazon, Meta

## Problem
There are crimes with required members `group[i]` and profit `profit[i]`. Return the number of schemes using at most `n` members and earning at least `minProfit`, modulo `1_000_000_007`. Constraints: `1 <= len(group) <= 100`, `1 <= n <= 100`.

## Examples
```text
Input: n = 5, minProfit = 3, group = [2,2], profit = [2,3]
Output: 2
Explanation: Commit the second crime alone or commit both crimes.
```

## Understanding & Intuition
Each crime is a 0/1 item with member cost and profit. Profit can be capped at `minProfit` because all larger profits are equivalent. The DP counts schemes rather than maximizing a value.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate every subset of crimes and count feasible profitable ones.
```python
class Solution:
    def profitableSchemes(self, n: int, minProfit: int, group: list[int], profit: list[int]) -> int:
        MOD = 1000000007
        ans = 0
        def dfs(i: int, members: int, gain: int) -> None:
            nonlocal ans
            if members > n:
                return
            if i == len(group):
                if gain >= minProfit:
                    ans = (ans + 1) % MOD
                return
            dfs(i + 1, members, gain)
            dfs(i + 1, members + group[i], gain + profit[i])
        dfs(0, 0, 0)
        return ans
```
- **Time:** O(2^m) — **Space:** O(m)

## Approach 2 — Better
**Idea:** Memoize by crime index, members used, and capped profit.
```python
class Solution:
    def profitableSchemes(self, n: int, minProfit: int, group: list[int], profit: list[int]) -> int:
        from functools import lru_cache
        MOD = 1000000007
        @lru_cache(None)
        def dp(i: int, members: int, gain: int) -> int:
            if i == len(group):
                return 1 if gain >= minProfit else 0
            ans = dp(i + 1, members, gain)
            if members + group[i] <= n:
                ans += dp(i + 1, members + group[i], min(minProfit, gain + profit[i]))
            return ans % MOD
        return dp(0, 0, 0)
```
- **Time:** O(mnP) — **Space:** O(mnP)

## Approach 3 — Optimal
**Idea:** Use a 2D table over members and capped profit, iterating crimes backward.
```python
class Solution:
    def profitableSchemes(self, n: int, minProfit: int, group: list[int], profit: list[int]) -> int:
        MOD = 1000000007
        dp = [[0] * (minProfit + 1) for _ in range(n + 1)]
        dp[0][0] = 1
        for g, p in zip(group, profit):
            for members in range(n - g, -1, -1):
                for prof in range(minProfit, -1, -1):
                    if dp[members][prof]:
                        np = min(minProfit, prof + p)
                        dp[members + g][np] = (dp[members + g][np] + dp[members][prof]) % MOD
        return sum(dp[m][minProfit] for m in range(n + 1)) % MOD
```
- **Time:** O(mnP) — **Space:** O(nP)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^m) | O(m) |
| Better | O(mnP) | O(mnP) |
| Optimal | O(mnP) | O(nP) |

## Edge Cases & Pitfalls
- Cap profit at `minProfit`.
- The empty scheme is valid only when `minProfit` is zero.
- Iterate members backward so each crime is used once.

## Related
- Count Subsets With Sum
- Number of Ways to Earn Points
