# 05. Best Time to Buy and Sell Stock with Cooldown

- **Difficulty:** Medium
- **Pattern:** 2-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Meta

## Problem
You are given daily stock prices. You may complete as many buy/sell transactions as you like, but you
may hold at most one share at a time. After selling a share, you must wait one full day before buying
again.

Return the maximum profit possible.

**Input**
- `prices`: a list where `prices[i]` is the stock price on day `i`.

**Output**
- An integer: the maximum profit.

## Constraints
- 1 <= prices.length <= 5000
- 0 <= prices[i] <= 1000

## Examples
```text
Input: prices = [1,2,3,0,2]
Output: 3
Explanation: Buy on day 0 and sell on day 1 for profit `1`, cooldown on day 2, then buy on day 3 and sell on day 4 for profit `2`, totaling `3`.
```

## Understanding & Intuition
Use a 2-D state `(day, holding)` to represent best future profit from a day with or without stock. If holding, choose sell or keep. If not holding, choose buy or skip; after selling, the recurrence jumps two days because of cooldown.

## Approach 1 — Naive / Brute Force
**Idea:** (recursion)
```python
from typing import List

class SolutionRecursive:
    def maxProfit(self, prices: List[int]) -> int:
        def dfs(i: int, holding: bool) -> int:
            if i >= len(prices):
                return 0
            if holding:
                sell = prices[i] + dfs(i + 2, False)
                keep = dfs(i + 1, True)
                return max(sell, keep)
            buy = -prices[i] + dfs(i + 1, True)
            skip = dfs(i + 1, False)
            return max(buy, skip)

        return dfs(0, False)
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** (memoization)
```python
from typing import List

class SolutionMemoized:
    def maxProfit(self, prices: List[int]) -> int:
        memo = {}

        def dfs(i: int, holding: bool) -> int:
            if i >= len(prices):
                return 0
            if (i, holding) not in memo:
                if holding:
                    memo[(i, holding)] = max(prices[i] + dfs(i + 2, False), dfs(i + 1, True))
                else:
                    memo[(i, holding)] = max(-prices[i] + dfs(i + 1, True), dfs(i + 1, False))
            return memo[(i, holding)]

        return dfs(0, False)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** (tabulation, space-optimized where possible)
```python
from typing import List

class Solution:
    def maxProfit(self, prices: List[int]) -> int:
        # hold: holding stock; sold: just sold today; rest: no stock and can buy.
        hold = -prices[0]
        sold = 0
        rest = 0
        for price in prices[1:]:
            prev_sold = sold
            sold = hold + price
            hold = max(hold, rest - price)
            rest = max(rest, prev_sold)
        return max(sold, rest)
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- A single price gives zero profit.
- Use the profit from two days later after a sale in recursive states.

## Related
- Best Time to Buy and Sell Stock II
- Best Time to Buy and Sell Stock with Transaction Fee
