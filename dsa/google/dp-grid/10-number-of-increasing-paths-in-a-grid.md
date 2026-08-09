# 10. Number of Increasing Paths in a Grid

- **Difficulty:** Hard
- **Pattern:** Dynamic Programming on Grids/Matrices
- **Asked at:** Google

## Problem
You are given an `m x n` integer grid.

An increasing path may start at any cell and repeatedly move to a 4-directionally adjacent cell with a strictly larger value. Count all non-empty increasing paths, including single-cell paths, modulo `10^9 + 7`.

**Input**
- `grid`: an `m x n` matrix of integers.

**Output**
- The number of strictly increasing paths modulo `10^9 + 7`.

## Constraints
- `1 <= m, n <= 1000`
- `1 <= m * n <= 10^5`
- `1 <= grid[r][c] <= 10^5`

## Examples
```text
Input: grid = [[1,1],[3,4]]
Output: 8
Explanation: The four single cells count, and the increasing multi-cell paths add four more, for a total of `8`.
```

## Understanding & Intuition
Strictly increasing moves create an acyclic dependency by value. Each cell contributes its single-cell path plus all paths from larger neighbors. DFS memoization or sorted value order computes the recurrence.

## Approach 1 — Naive / Brute Force
**Idea:** Memoize DFS counts starting from each cell.
```python
class Solution:
    def countPaths(self, grid: list[list[int]]) -> int:
        from functools import lru_cache
        import sys
        sys.setrecursionlimit(1000000)
        mod = 10 ** 9 + 7
        m, n = len(grid), len(grid[0])
        @lru_cache(None)
        def dfs(r, c):
            total = 1
            for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and grid[nr][nc] > grid[r][c]:
                    total += dfs(nr, nc)
            return total % mod
        return sum(dfs(r, c) for r in range(m) for c in range(n)) % mod
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Sort cells descending and pull counts from larger neighbors.
```python
class Solution:
    def countPaths(self, grid: list[list[int]]) -> int:
        mod = 10 ** 9 + 7
        m, n = len(grid), len(grid[0])
        cells = sorted(((grid[r][c], r, c) for r in range(m) for c in range(n)), reverse=True)
        dp = [[1] * n for _ in range(m)]
        for val, r, c in cells:
            for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and grid[nr][nc] > val:
                    dp[r][c] = (dp[r][c] + dp[nr][nc]) % mod
        return sum(map(sum, dp)) % mod
```
- **Time:** O(mn log(mn)) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** Sort ascending and push each cell's path count to larger neighbors.
```python
class Solution:
    def countPaths(self, grid: list[list[int]]) -> int:
        mod = 10 ** 9 + 7
        m, n = len(grid), len(grid[0])
        cells = sorted((grid[r][c], r, c) for r in range(m) for c in range(n))
        dp = [[1] * n for _ in range(m)]
        ans = 0
        for val, r, c in cells:
            ans = (ans + dp[r][c]) % mod
            for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and grid[nr][nc] > val:
                    dp[nr][nc] = (dp[nr][nc] + dp[r][c]) % mod
        return ans
```
- **Time:** O(mn log(mn)) — **Space:** O(mn)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn) | O(mn) |
| Better | O(mn log(mn)) | O(mn) |
| Optimal | O(mn log(mn)) | O(mn) |

## Edge Cases & Pitfalls
- Equal neighbors are not increasing.
- Count every single-cell path.
- Take modulo on additions.

## Related
- Longest Increasing Path in a Matrix
- Paths in Matrix Whose Sum Is Divisible by K
