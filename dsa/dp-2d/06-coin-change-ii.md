# 06. Coin Change II

- **Difficulty:** Medium
- **Pattern:** 2-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Bloomberg

## Problem
Given an integer `amount` and distinct coin denominations, return the number of combinations that make up that amount. Coin order does not matter and each coin may be used unlimited times. Constraints: `0 <= amount <= 5000`, `1 <= coins.length <= 300`.

## Examples
```text
Input: amount = 5, coins = [1,2,5]
Output: 4
Explanation: The combinations are 5, 2+2+1, 2+1+1+1, and five 1s.
```

## Understanding & Intuition
Let `dp[i][rem]` be ways to form `rem` using coins from index `i` onward. At each coin, either take it and stay at `i`, or skip it and move to `i + 1`. This avoids counting different orders as separate combinations.

## Approach 1 — Naive / Brute Force
**Idea:** (recursion)
```python
from typing import List

class SolutionRecursive:
    def change(self, amount: int, coins: List[int]) -> int:
        def dfs(i: int, rem: int) -> int:
            if rem == 0:
                return 1
            if rem < 0 or i == len(coins):
                return 0
            return dfs(i, rem - coins[i]) + dfs(i + 1, rem)

        return dfs(0, amount)
```
- **Time:** O(2^(amount+n)) — **Space:** O(amount+n)

## Approach 2 — Better
**Idea:** (memoization)
```python
from typing import List

class SolutionMemoized:
    def change(self, amount: int, coins: List[int]) -> int:
        memo = {}

        def dfs(i: int, rem: int) -> int:
            if rem == 0:
                return 1
            if rem < 0 or i == len(coins):
                return 0
            if (i, rem) not in memo:
                memo[(i, rem)] = dfs(i, rem - coins[i]) + dfs(i + 1, rem)
            return memo[(i, rem)]

        return dfs(0, amount)
```
- **Time:** O(n*amount) — **Space:** O(n*amount)

## Approach 3 — Optimal
**Idea:** (tabulation, space-optimized where possible)
```python
from typing import List

class Solution:
    def change(self, amount: int, coins: List[int]) -> int:
        dp = [0] * (amount + 1)
        dp[0] = 1
        for coin in coins:
            # Forward pass allows reusing the same coin.
            for total in range(coin, amount + 1):
                dp[total] += dp[total - coin]
        return dp[amount]
```
- **Time:** O(n*amount) — **Space:** O(amount)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^(amount+n)) | O(amount+n) |
| Better | O(n*amount) | O(n*amount) |
| Optimal | O(n*amount) | O(amount) |

## Edge Cases & Pitfalls
- Amount zero has one combination: choose no coins.
- Iterate coins outside totals to count combinations, not permutations.

## Related
- Coin Change
- 0/1 Knapsack
