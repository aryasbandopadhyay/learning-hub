# 13. Cherry Pickup II

- **Difficulty:** Hard
- **Pattern:** Dynamic Programming on Grids/Matrices
- **Asked at:** Google

## Problem
Two robots start at the top row, columns `0` and `n-1`. Each row, both move to the next row and shift by `-1`, `0`, or `1` columns. If both visit the same cell, count it once. Return the maximum cherries collected.

Constraints: `2 <= m,n <= 70`, `0 <= grid[i][j] <= 100`.

## Examples
```text
Input: grid = [[3,1,1],[2,5,1],[1,5,5],[2,1,1]]
Output: 24
Explanation: Optimal robot routes collect 24 cherries.
```

## Understanding & Intuition
At any row, the state is the pair of robot columns. The next row has up to nine column-pair transitions. A same-column collision contributes the cell value only once.

## Approach 1 — Naive / Brute Force
**Idea:** Memoize recursion on `(row, col1, col2)`.
```python
class Solution:
    def cherryPickup(self, grid: list[list[int]]) -> int:
        from functools import lru_cache
        m, n = len(grid), len(grid[0])
        @lru_cache(None)
        def dfs(r, c1, c2):
            if not (0 <= c1 < n and 0 <= c2 < n):
                return -10 ** 9
            gain = grid[r][c1] + (0 if c1 == c2 else grid[r][c2])
            if r == m - 1:
                return gain
            return gain + max(dfs(r + 1, c1 + d1, c2 + d2) for d1 in (-1, 0, 1) for d2 in (-1, 0, 1))
        return dfs(0, 0, n - 1)
```
- **Time:** O(mn^2) — **Space:** O(mn^2)

## Approach 2 — Better
**Idea:** Fill a 3D bottom-up DP table.
```python
class Solution:
    def cherryPickup(self, grid: list[list[int]]) -> int:
        m, n = len(grid), len(grid[0])
        dp = [[[-10 ** 9] * n for _ in range(n)] for _ in range(m)]
        for c1 in range(n):
            for c2 in range(n):
                dp[-1][c1][c2] = grid[-1][c1] + (0 if c1 == c2 else grid[-1][c2])
        for r in range(m - 2, -1, -1):
            for c1 in range(n):
                for c2 in range(n):
                    gain = grid[r][c1] + (0 if c1 == c2 else grid[r][c2])
                    best = -10 ** 9
                    for d1 in (-1, 0, 1):
                        for d2 in (-1, 0, 1):
                            nc1, nc2 = c1 + d1, c2 + d2
                            if 0 <= nc1 < n and 0 <= nc2 < n:
                                best = max(best, dp[r + 1][nc1][nc2])
                    dp[r][c1][c2] = gain + best
        return dp[0][0][n - 1]
```
- **Time:** O(mn^2) — **Space:** O(mn^2)

## Approach 3 — Optimal
**Idea:** Keep only the next row's column-pair matrix.
```python
class Solution:
    def cherryPickup(self, grid: list[list[int]]) -> int:
        m, n = len(grid), len(grid[0])
        nxt = [[0] * n for _ in range(n)]
        for r in range(m - 1, -1, -1):
            cur = [[0] * n for _ in range(n)]
            for c1 in range(n):
                for c2 in range(n):
                    gain = grid[r][c1] + (0 if c1 == c2 else grid[r][c2])
                    if r == m - 1:
                        cur[c1][c2] = gain
                    else:
                        best = -10 ** 9
                        for d1 in (-1, 0, 1):
                            for d2 in (-1, 0, 1):
                                nc1, nc2 = c1 + d1, c2 + d2
                                if 0 <= nc1 < n and 0 <= nc2 < n:
                                    best = max(best, nxt[nc1][nc2])
                        cur[c1][c2] = gain + best
            nxt = cur
        return nxt[0][n - 1]
```
- **Time:** O(mn^2) — **Space:** O(n^2)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn^2) | O(mn^2) |
| Better | O(mn^2) | O(mn^2) |
| Optimal | O(mn^2) | O(n^2) |

## Edge Cases & Pitfalls
- Count a shared cell once.
- Both robots move to the next row together.
- Try all nine shift pairs.

## Related
- Cherry Pickup
- Minimum Falling Path Sum
