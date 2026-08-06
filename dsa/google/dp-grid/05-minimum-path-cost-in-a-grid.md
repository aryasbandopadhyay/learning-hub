# 05. Minimum Path Cost in a Grid

- **Difficulty:** Medium
- **Pattern:** Dynamic Programming on Grids/Matrices
- **Asked at:** Google

## Problem
Given `grid` and `moveCost`, start in any first-row cell and choose one cell in each next row. Moving from a cell with value `v` to next-row column `c` costs `moveCost[v][c]`. Return the minimum total of visited grid values plus move costs.

Constraints: `1 <= m,n <= 50`; all grid values are valid moveCost row indices.

## Examples
```text
Input: grid = [[5,3],[4,0],[2,1]], moveCost = [[9,8],[1,5],[10,12],[18,6],[2,4],[14,3]]
Output: 17
Explanation: Path 5 -> 0 -> 1 costs 5 + 3 + 0 + 8 + 1 = 17.
```

## Understanding & Intuition
The best continuation from a cell depends only on that cell. For every next-row column, try all previous columns and add the move cost based on the previous value. Only one previous row of costs is required.

## Approach 1 — Naive / Brute Force
**Idea:** Memoize the best suffix path from each cell.
```python
class Solution:
    def minPathCost(self, grid: list[list[int]], moveCost: list[list[int]]) -> int:
        from functools import lru_cache
        m, n = len(grid), len(grid[0])
        @lru_cache(None)
        def dfs(r, c):
            if r == m - 1:
                return grid[r][c]
            return grid[r][c] + min(moveCost[grid[r][c]][nc] + dfs(r + 1, nc) for nc in range(n))
        return min(dfs(0, c) for c in range(n))
```
- **Time:** O(mn^2) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Fill a full row-by-row DP table.
```python
class Solution:
    def minPathCost(self, grid: list[list[int]], moveCost: list[list[int]]) -> int:
        m, n = len(grid), len(grid[0])
        dp = [[10 ** 18] * n for _ in range(m)]
        dp[0] = grid[0][:]
        for r in range(1, m):
            for pc in range(n):
                for c in range(n):
                    dp[r][c] = min(dp[r][c], dp[r - 1][pc] + moveCost[grid[r - 1][pc]][c] + grid[r][c])
        return min(dp[-1])
```
- **Time:** O(mn^2) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** Keep only the previous row's costs.
```python
class Solution:
    def minPathCost(self, grid: list[list[int]], moveCost: list[list[int]]) -> int:
        m, n = len(grid), len(grid[0])
        prev = grid[0][:]
        for r in range(1, m):
            cur = [10 ** 18] * n
            for pc, cost in enumerate(prev):
                for c in range(n):
                    cur[c] = min(cur[c], cost + moveCost[grid[r - 1][pc]][c] + grid[r][c])
            prev = cur
        return min(prev)
```
- **Time:** O(mn^2) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn^2) | O(mn) |
| Better | O(mn^2) | O(mn) |
| Optimal | O(mn^2) | O(n) |

## Edge Cases & Pitfalls
- The start column is unrestricted.
- Move cost uses the previous cell value.
- Include every visited grid value.

## Related
- Minimum Falling Path Sum
- Dungeon Game
