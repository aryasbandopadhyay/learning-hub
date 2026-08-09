# 18. Best Time to Buy and Sell Stock

- **Difficulty:** Easy
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given daily stock prices, choose one day to buy and a later day to sell to maximize profit. If no profitable transaction exists, return `0`.

**Input**
- `prices`: a list where `prices[i]` is the stock price on day `i`.

**Output**
- The maximum profit from one buy followed by one later sell.

## Constraints
- `1 <= prices.length <= 10^5`
- `0 <= prices[i] <= 10^4`

## Examples
```text
Input: prices = [7,1,5,3,6,4]
Output: 5
Explanation: Buy at price `1` on day 1 and sell at price `6` on day 4 for profit `5`, which is the maximum possible.
```

## Understanding & Intuition
For each sell day, the best buy is the minimum price seen earlier. One pass maintains that minimum and the best profit.

## Approach 1 — Naive / Brute Force
**Idea:** Try every buy/sell pair.
```python
class Solution:
    def maxProfit(self, prices: list[int]) -> int:
        best = 0
        for b in range(len(prices)):
            for s in range(b + 1, len(prices)):
                best = max(best, prices[s] - prices[b])
        return best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Precompute the best future sell price.
```python
class Solution:
    def maxProfit(self, prices: list[int]) -> int:
        n = len(prices)
        future = [0] * n
        future[-1] = prices[-1]
        for i in range(n-2, -1, -1):
            future[i] = max(prices[i], future[i+1])
        return max(future[i] - prices[i] for i in range(n))
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Scan once with minimum price so far.
```python
class Solution:
    def maxProfit(self, prices: list[int]) -> int:
        low = prices[0]
        best = 0
        for p in prices[1:]:
            best = max(best, p - low)
            low = min(low, p)
        return best
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Sell must be after buy.
- Decreasing prices return 0.
- Track min before future days only.

## Related
- Maximum Subarray
- Best Time to Buy and Sell Stock II
