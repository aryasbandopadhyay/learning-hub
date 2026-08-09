# 07. Maximum Ice Cream Bars

- **Difficulty:** Medium
- **Pattern:** greedy scheduling & assignment
- **Asked at:** Amazon, Google, Walmart Labs

## Problem
Implement `maxIceCream` for **Maximum Ice Cream Bars**. Given bar prices `costs` and a budget `coins`, return the maximum number of ice cream bars you can buy.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `costs`: list; cost list.
- `coins`: integer; available coins.

**Output**
- A single integer.

## Constraints
- Use the standard constraints for this problem as implied by the judge manifest and examples.

## Examples
```text
Input: costs = [1,3,2,4,1], coins = 7
Output: 4
Explanation: Buy costs 1, 1, 2, and 3. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Only the count matters, so cheaper bars dominate more expensive bars. If a solution buys an expensive bar while a cheaper unbought one exists, swapping keeps the count and reduces cost. Thus buying in increasing price order is optimal.

## Approach 1 — Naive / Brute Force
**Idea:** Use knapsack dynamic programming to maximize count under the coin budget.
```python
class Solution:
    def maxIceCream(self, costs: list[int], coins: int) -> int:
        dp = [-10**9] * (coins + 1)
        dp[0] = 0
        for cost in costs:
            if cost <= coins:
                for c in range(coins, cost - 1, -1):
                    dp[c] = max(dp[c], dp[c - cost] + 1)
        return max(dp)
```
- **Time:** O(nC) — **Space:** O(C)

## Approach 2 — Better
**Idea:** Sort prices and buy greedily from cheapest to most expensive.
```python
class Solution:
    def maxIceCream(self, costs: list[int], coins: int) -> int:
        ans = 0
        for cost in sorted(costs):
            if cost > coins:
                break
            coins -= cost
            ans += 1
        return ans
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Count prices and consume price buckets in increasing order.
```python
class Solution:
    def maxIceCream(self, costs: list[int], coins: int) -> int:
        if not costs:
            return 0
        freq = [0] * (max(costs) + 1)
        for cost in costs:
            freq[cost] += 1
        ans = 0
        for price in range(1, len(freq)):
            if freq[price] == 0:
                continue
            take = min(freq[price], coins // price)
            ans += take
            coins -= take * price
            if take < freq[price]:
                break
        return ans
```
- **Time:** O(n + U) — **Space:** O(U)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nC) | O(C) |
| Better | O(n log n) | O(n) |
| Optimal | O(n + U) | O(U) |

## Edge Cases & Pitfalls
- Stop when the next cheapest price is unaffordable.
- Counting sort needs bounded prices.
- The budget can be larger than the sum of all costs.

## Related
- Maximum Units on a Truck
- Reduce Array Size to the Half
