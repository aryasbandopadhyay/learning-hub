# 03. Minimum Path Sum

- **Difficulty:** Medium
- **Pattern:** 2-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Apple

## Problem
Given an `m x n` grid of non-negative integers, find a path from the top-left cell to the
bottom-right cell with the minimum possible sum of visited cell values. You may move only down or
right.

**Input**
- `grid`: an `m x n` matrix of non-negative integers.

**Output**
- An integer: the minimum path sum.

## Constraints
- m == grid.length
- n == grid[r].length
- 1 <= m, n <= 200
- 0 <= grid[r][c] <= 200

## Examples
```text
Input: grid = [[1,3,1],[1,5,1],[4,2,1]]
Output: 7
Explanation: The minimum-sum route is `1 -> 3 -> 1 -> 1 -> 1`, for a total of `7`.
```

## Understanding & Intuition
Let `dp[r][c]` be the minimum cost from `(r, c)` to the goal. The recurrence is `grid[r][c] + min(down, right)`. Invalid moves are treated as infinity so they are never chosen.

## Approach 1 — Naive / Brute Force
**Idea:** (recursion)
```python
from typing import List

class SolutionRecursive:
    def minPathSum(self, grid: List[List[int]]) -> int:
        m, n = len(grid), len(grid[0])

        def dfs(r: int, c: int) -> int:
            if r == m or c == n:
                return float("inf")
            if r == m - 1 and c == n - 1:
                return grid[r][c]
            return grid[r][c] + min(dfs(r + 1, c), dfs(r, c + 1))

        return dfs(0, 0)
```
- **Time:** O(2^(m+n)) — **Space:** O(m+n)

## Approach 2 — Better
**Idea:** (memoization)
```python
from typing import List

class SolutionMemoized:
    def minPathSum(self, grid: List[List[int]]) -> int:
        m, n = len(grid), len(grid[0])
        memo = {}

        def dfs(r: int, c: int) -> int:
            if r == m or c == n:
                return float("inf")
            if r == m - 1 and c == n - 1:
                return grid[r][c]
            if (r, c) not in memo:
                memo[(r, c)] = grid[r][c] + min(dfs(r + 1, c), dfs(r, c + 1))
            return memo[(r, c)]

        return dfs(0, 0)
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** (tabulation, space-optimized where possible)
```python
from typing import List

class Solution:
    def minPathSum(self, grid: List[List[int]]) -> int:
        m, n = len(grid), len(grid[0])
        dp = [float("inf")] * (n + 1)
        dp[n - 1] = 0
        for r in range(m - 1, -1, -1):
            for c in range(n - 1, -1, -1):
                dp[c] = grid[r][c] + min(dp[c], dp[c + 1])
        return dp[0]
```
- **Time:** O(mn) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^(m+n)) | O(m+n) |
| Better | O(mn) | O(mn) |
| Optimal | O(mn) | O(n) |

## Edge Cases & Pitfalls
- A one-cell grid returns its only value.
- Use infinity for out-of-bounds paths, not zero.

## Related
- Unique Paths
- Triangle
