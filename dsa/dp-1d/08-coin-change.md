# 08. Coin Change

- **Difficulty:** Medium
- **Pattern:** 1-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Uber

## Problem
Given coin denominations `coins` and an integer `amount`, return the fewest coins needed to make that amount, or `-1` if impossible. Constraints: `1 <= coins.length <= 12`, `1 <= coins[i] <= 2^31 - 1`, `0 <= amount <= 10^4`.

## Examples
```text
Input: coins = [1,2,5], amount = 11
Output: 3
Explanation: 11 = 5 + 5 + 1.
```

## Understanding & Intuition
Let `dp[x]` be the minimum coins needed for amount `x`. For each coin, a candidate is `1 + dp[x - coin]`. Take the minimum over all valid coins, using infinity for unreachable amounts.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively subtract every coin and keep the smallest count.
```python
from typing import List

class Solution:
    def coinChange(self, coins: List[int], amount: int) -> int:
        def dfs(rem: int) -> int:
            if rem == 0:
                return 0
            if rem < 0:
                return float("inf")
            return 1 + min(dfs(rem - coin) for coin in coins)

        ans = dfs(amount)
        return -1 if ans == float("inf") else ans
```
- **Time:** O(k^amount) — **Space:** O(amount)

## Approach 2 — Better
**Idea:** Memoize the best answer for each remaining amount.
```python
from typing import List

class Solution:
    def coinChange(self, coins: List[int], amount: int) -> int:
        memo = {}

        def dfs(rem: int) -> int:
            if rem == 0:
                return 0
            if rem < 0:
                return float("inf")
            if rem not in memo:
                memo[rem] = 1 + min(dfs(rem - coin) for coin in coins)
            return memo[rem]

        ans = dfs(amount)
        return -1 if ans == float("inf") else ans
```
- **Time:** O(amount * k) — **Space:** O(amount)

## Approach 3 — Optimal
**Idea:** Bottom-up tabulation from amount `0` to target.
```python
from typing import List

class Solution:
    def coinChange(self, coins: List[int], amount: int) -> int:
        dp = [float("inf")] * (amount + 1)
        dp[0] = 0
        for total in range(1, amount + 1):
            for coin in coins:
                if total >= coin:
                    dp[total] = min(dp[total], 1 + dp[total - coin])
        return -1 if dp[amount] == float("inf") else dp[amount]
```
- **Time:** O(amount * k) — **Space:** O(amount)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(k^amount) | O(amount) |
| Better | O(amount * k) | O(amount) |
| Optimal | O(amount * k) | O(amount) |

## Edge Cases & Pitfalls
- `amount = 0` needs zero coins.
- Greedy is not correct for arbitrary coin systems.

## Related
- Coin Change II
- Perfect Squares
