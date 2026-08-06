# 07. Where Will the Ball Fall

- **Difficulty:** Medium
- **Pattern:** Dynamic Programming on Grids/Matrices
- **Asked at:** Google

## Problem
A grid contains diagonal boards. `1` sends a ball down-right and `-1` sends it down-left. Drop one ball from each top column and return each final column, or `-1` if it gets stuck.

Constraints: `1 <= m,n <= 100`, `grid[i][j] in {-1,1}`.

## Examples
```text
Input: grid = [[1,1,1,-1,-1],[1,1,1,-1,-1],[-1,-1,-1,1,1],[1,1,1,1,-1],[-1,-1,-1,-1,-1]]
Output: [1,-1,-1,-1,-1]
Explanation: Only the first ball exits, at column 1.
```

## Understanding & Intuition
A transition is valid only when the current board and the adjacent board point the same way. Otherwise the ball hits a wall or V-shape. Cell exits can be simulated, memoized, or computed bottom-up.

## Approach 1 — Naive / Brute Force
**Idea:** Simulate every ball independently.
```python
class Solution:
    def findBall(self, grid: list[list[int]]) -> list[int]:
        m, n = len(grid), len(grid[0])
        ans = []
        for start in range(n):
            c = start
            for r in range(m):
                nc = c + grid[r][c]
                if nc < 0 or nc >= n or grid[r][nc] != grid[r][c]:
                    c = -1
                    break
                c = nc
            ans.append(c)
        return ans
```
- **Time:** O(mn) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Memoize the exit column from each cell.
```python
class Solution:
    def findBall(self, grid: list[list[int]]) -> list[int]:
        from functools import lru_cache
        m, n = len(grid), len(grid[0])
        @lru_cache(None)
        def fall(r, c):
            if r == m:
                return c
            nc = c + grid[r][c]
            if nc < 0 or nc >= n or grid[r][nc] != grid[r][c]:
                return -1
            return fall(r + 1, nc)
        return [fall(0, c) for c in range(n)]
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** Compute exits from the bottom row upward.
```python
class Solution:
    def findBall(self, grid: list[list[int]]) -> list[int]:
        n = len(grid[0])
        dp = list(range(n))
        for r in range(len(grid) - 1, -1, -1):
            ndp = [-1] * n
            for c in range(n):
                nc = c + grid[r][c]
                if 0 <= nc < n and grid[r][nc] == grid[r][c]:
                    ndp[c] = dp[nc]
            dp = ndp
        return dp
```
- **Time:** O(mn) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn) | O(1) |
| Better | O(mn) | O(mn) |
| Optimal | O(mn) | O(n) |

## Edge Cases & Pitfalls
- Boundaries and V-shapes both trap balls.
- Return one value per starting column.
- The board shifts the ball before it moves to the next row.

## Related
- Diagonal Traverse
- Game of Life
