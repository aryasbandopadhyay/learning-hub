# 14. 0/1 Knapsack

- **Difficulty:** Medium
- **Pattern:** 2-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Adobe

## Problem
You are given `n` items. Item `i` has weight `wt[i]` and value `val[i]`. A knapsack can carry at
most total weight `W`. Each item can be chosen at most once.

Return the maximum total value that can fit in the knapsack.

**Input**
- `W`: the knapsack capacity.
- `wt`: a list of item weights.
- `val`: a list of item values aligned with `wt`.
- `n`: the number of available items to consider.

**Output**
- An integer: the maximum total value with total weight at most `W`.

## Constraints
- n == wt.length == val.length
- 1 <= n <= 1000
- 1 <= W <= 1000
- 1 <= wt[i], val[i] <= 1000

## Examples
```text
Input: W = 4, wt = [4,5,1], val = [1,2,3], n = 3
Output: 3
Explanation: The item with weight `1` and value `3` fits by itself and gives the best value under capacity `4`.
```

## Understanding & Intuition
Let `dp[i][cap]` be the best value using items from index `i` onward with remaining capacity `cap`. For each item, skip it or take it if its weight fits. Since each item is 0/1, taking moves to the next index.

## Approach 1 — Naive / Brute Force
**Idea:** (recursion)
```python
from typing import List

class SolutionRecursive:
    def knapSack(self, W: int, wt: List[int], val: List[int], n: int) -> int:
        def dfs(i: int, cap: int) -> int:
            if i == n:
                return 0
            best = dfs(i + 1, cap)
            if wt[i] <= cap:
                best = max(best, val[i] + dfs(i + 1, cap - wt[i]))
            return best

        return dfs(0, W)
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** (memoization)
```python
from typing import List

class SolutionMemoized:
    def knapSack(self, W: int, wt: List[int], val: List[int], n: int) -> int:
        memo = {}

        def dfs(i: int, cap: int) -> int:
            if i == n:
                return 0
            if (i, cap) not in memo:
                memo[(i, cap)] = dfs(i + 1, cap)
                if wt[i] <= cap:
                    memo[(i, cap)] = max(memo[(i, cap)], val[i] + dfs(i + 1, cap - wt[i]))
            return memo[(i, cap)]

        return dfs(0, W)
```
- **Time:** O(nW) — **Space:** O(nW)

## Approach 3 — Optimal
**Idea:** (tabulation, space-optimized where possible)
```python
from typing import List

class Solution:
    def knapSack(self, W: int, wt: List[int], val: List[int], n: int) -> int:
        dp = [0] * (W + 1)
        for i in range(n):
            # Reverse capacity prevents reusing the same item.
            for cap in range(W, wt[i] - 1, -1):
                dp[cap] = max(dp[cap], val[i] + dp[cap - wt[i]])
        return dp[W]
```
- **Time:** O(nW) — **Space:** O(W)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(nW) | O(nW) |
| Optimal | O(nW) | O(W) |

## Edge Cases & Pitfalls
- Capacity zero returns zero.
- Iterate capacities backward for 0/1 knapsack.

## Related
- Coin Change II
- Target Sum
