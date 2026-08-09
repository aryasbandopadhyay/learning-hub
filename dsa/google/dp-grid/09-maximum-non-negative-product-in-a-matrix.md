# 09. Maximum Non Negative Product in a Matrix

- **Difficulty:** Medium
- **Pattern:** Dynamic Programming on Grids/Matrices
- **Asked at:** Google

## Problem
You are given an `m x n` integer grid.

Start at the top-left cell and move only right or down to the bottom-right cell. The path product is the product of all visited values. Return the maximum non-negative product modulo `10^9 + 7`; if every path product is negative, return `-1`.

**Input**
- `grid`: an `m x n` matrix of integers.

**Output**
- The largest non-negative path product modulo `10^9 + 7`, or `-1` if none exists.

## Constraints
- `1 <= m, n <= 15`
- `-4 <= grid[r][c] <= 4`

## Examples
```text
Input: grid = [[-1,-2,-3],[-2,-3,-3],[-3,-3,-2]]
Output: -1
Explanation: Every top-left to bottom-right path has a negative product, so there is no non-negative product to return.
```

## Understanding & Intuition
A negative number swaps the best and worst products. Therefore each cell must track both minimum and maximum reachable product. Apply modulo only after choosing the final sign.

## Approach 1 — Naive / Brute Force
**Idea:** Memoize min/max products from each cell to the end.
```python
class Solution:
    def maxProductPath(self, grid: list[list[int]]) -> int:
        from functools import lru_cache
        mod = 10 ** 9 + 7
        m, n = len(grid), len(grid[0])
        @lru_cache(None)
        def dfs(r, c):
            if r == m - 1 and c == n - 1:
                return grid[r][c], grid[r][c]
            vals = []
            if r + 1 < m:
                vals += list(dfs(r + 1, c))
            if c + 1 < n:
                vals += list(dfs(r, c + 1))
            prod = [grid[r][c] * v for v in vals]
            return min(prod), max(prod)
        ans = dfs(0, 0)[1]
        return -1 if ans < 0 else ans % mod
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Fill 2D min-product and max-product tables.
```python
class Solution:
    def maxProductPath(self, grid: list[list[int]]) -> int:
        mod = 10 ** 9 + 7
        m, n = len(grid), len(grid[0])
        mn = [[0] * n for _ in range(m)]
        mx = [[0] * n for _ in range(m)]
        mn[0][0] = mx[0][0] = grid[0][0]
        for r in range(m):
            for c in range(n):
                if r == 0 and c == 0:
                    continue
                vals = []
                if r:
                    vals += [mn[r - 1][c] * grid[r][c], mx[r - 1][c] * grid[r][c]]
                if c:
                    vals += [mn[r][c - 1] * grid[r][c], mx[r][c - 1] * grid[r][c]]
                mn[r][c], mx[r][c] = min(vals), max(vals)
        return -1 if mx[-1][-1] < 0 else mx[-1][-1] % mod
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** Roll min/max product arrays by row.
```python
class Solution:
    def maxProductPath(self, grid: list[list[int]]) -> int:
        mod = 10 ** 9 + 7
        m, n = len(grid), len(grid[0])
        mn = [0] * n
        mx = [0] * n
        for r in range(m):
            for c in range(n):
                x = grid[r][c]
                if r == 0 and c == 0:
                    mn[c] = mx[c] = x
                    continue
                vals = []
                if r:
                    vals += [mn[c] * x, mx[c] * x]
                if c:
                    vals += [mn[c - 1] * x, mx[c - 1] * x]
                mn[c], mx[c] = min(vals), max(vals)
        return -1 if mx[-1] < 0 else mx[-1] % mod
```
- **Time:** O(mn) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn) | O(mn) |
| Better | O(mn) | O(mn) |
| Optimal | O(mn) | O(n) |

## Edge Cases & Pitfalls
- Track both extremes.
- Zero is a valid non-negative result.
- Do not modulo before checking sign.

## Related
- Dungeon Game
- Minimum Path Sum
