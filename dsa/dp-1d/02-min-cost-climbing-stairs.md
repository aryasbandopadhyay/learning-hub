# 02. Min Cost Climbing Stairs

- **Difficulty:** Easy
- **Pattern:** 1-D Dynamic Programming
- **Asked at:** Amazon, Google, Adobe, Apple

## Problem
A staircase has `n` steps indexed from `0` to `n - 1`. `cost[i]` is the cost paid when you step on
step `i`. After paying for a step, you may climb either 1 or 2 steps. You may start on step `0` or
step `1`, and the top is just after step `n - 1`.

Return the minimum total cost needed to reach the top.

**Input**
- `cost`: a list of non-negative integers where `cost[i]` is the cost of step `i`.

**Output**
- An integer: the minimum cost to reach the top.

## Constraints
- n == cost.length
- 2 <= n <= 1000
- 0 <= cost[i] <= 999

## Examples
```text
Input: cost = [10,15,20]
Output: 15
Explanation: Starting at step 1 costs `15`, then you can climb two steps directly to the top. Starting through step 0 and step 2 costs `10 + 20 = 30`, so the minimum is `15`.
```

## Understanding & Intuition
Let `dp[i]` be the minimum cost to reach stair `i`. To reach `i`, the previous stair is `i-1` or `i-2`, so `dp[i] = cost[i] + min(dp[i-1], dp[i-2])`. The answer is `min(dp[n-1], dp[n-2])` because the top is after the last stair.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively compute the cheapest route starting from stair 0 or 1.
```python
from typing import List

class Solution:
    def minCostClimbingStairs(self, cost: List[int]) -> int:
        n = len(cost)

        def dfs(i: int) -> int:
            if i >= n:
                return 0
            return cost[i] + min(dfs(i + 1), dfs(i + 2))

        return min(dfs(0), dfs(1))
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Cache the minimum cost from each stair.
```python
from typing import List

class Solution:
    def minCostClimbingStairs(self, cost: List[int]) -> int:
        n = len(cost)
        memo = {}

        def dfs(i: int) -> int:
            if i >= n:
                return 0
            if i not in memo:
                memo[i] = cost[i] + min(dfs(i + 1), dfs(i + 2))
            return memo[i]

        return min(dfs(0), dfs(1))
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Track only the minimum cost to reach the previous two stairs.
```python
from typing import List

class Solution:
    def minCostClimbingStairs(self, cost: List[int]) -> int:
        prev2, prev1 = cost[0], cost[1]
        for i in range(2, len(cost)):
            prev2, prev1 = prev1, cost[i] + min(prev1, prev2)
        return min(prev1, prev2)
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- You can start on stair 0 or stair 1.
- The top itself has no cost.

## Related
- Climbing Stairs
- House Robber
