# 05. Minimum Path Cost in a Grid

- **Difficulty:** Medium
- **Pattern:** Dynamic Programming on Grids/Matrices
- **Asked at:** Google

## Problem
You are given a grid of values and a move-cost table. A path starts at any cell in the first row and chooses one cell in each following row.

If the current cell value is `v` and the next chosen column is `c`, the move costs `moveCost[v][c]`. The total path cost is the sum of all visited grid values plus all move costs. Return the minimum total cost.

**Input**
- `grid`: an `m x n` matrix of distinct values.
- `moveCost`: a table where `moveCost[value][next_col]` gives transition cost.

**Output**
- The minimum total cost of a path from the first row to the last row.

## Constraints
- `2 <= m, n <= 50`
- `grid` contains distinct values from `0` to `m * n - 1`
- `moveCost.length == m * n`
- `moveCost[row].length == n`
- `1 <= moveCost[v][c] <= 100`

## Examples
```text
Input: grid = [[5,3],[4,0],[2,1]], moveCost = [[9,8],[1,5],[10,12],[18,6],[2,4],[14,3]]
Output: 17
Explanation: One cheapest path is `5 -> 0 -> 1`: grid values sum to `6`, and the moves cost `9` and `2`, totaling `17`.
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
