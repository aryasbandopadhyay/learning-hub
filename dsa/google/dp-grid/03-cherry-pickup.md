# 03. Cherry Pickup

- **Difficulty:** Hard
- **Pattern:** Dynamic Programming on Grids/Matrices
- **Asked at:** Google

## Problem
In an `n x n` grid with cherries `1`, empty cells `0`, and thorns `-1`, go from `(0,0)` to `(n-1,n-1)` moving right/down and return moving left/up. Cherries are collected once. Return the maximum cherries, or `0` if no round trip exists.

Constraints: `1 <= n <= 50`, `grid[i][j] in {-1,0,1}`.

## Examples
```text
Input: grid = [[0,1,-1],[1,0,-1],[1,1,1]]
Output: 5
Explanation: The best synchronized pair of paths collects five cherries.
```

## Understanding & Intuition
The trip out and back is equivalent to two walkers moving from start to finish simultaneously. After the same number of moves, three coordinates determine the fourth. If both walkers share a cell, count its cherry once.

## Approach 1 — Naive / Brute Force
**Idea:** Memoize a recursive state `(r1,c1,r2)` and derive `c2`.
```python
class Solution:
    def cherryPickup(self, grid: list[list[int]]) -> int:
        from functools import lru_cache
        n = len(grid)
        neg = -10 ** 9
        @lru_cache(None)
        def dp(r1, c1, r2):
            c2 = r1 + c1 - r2
            if r1 >= n or c1 >= n or r2 >= n or c2 >= n or grid[r1][c1] == -1 or grid[r2][c2] == -1:
                return neg
            if r1 == c1 == n - 1:
                return grid[r1][c1]
            gain = grid[r1][c1] + (0 if (r1, c1) == (r2, c2) else grid[r2][c2])
            best = max(dp(r1 + 1, c1, r2 + 1), dp(r1 + 1, c1, r2), dp(r1, c1 + 1, r2 + 1), dp(r1, c1 + 1, r2))
            return gain + best
        return max(0, dp(0, 0, 0))
```
- **Time:** O(n^3) — **Space:** O(n^3)

## Approach 2 — Better
**Idea:** Iterate by shared step count and two row positions.
```python
class Solution:
    def cherryPickup(self, grid: list[list[int]]) -> int:
        n = len(grid)
        neg = -10 ** 9
        dp = [[neg] * n for _ in range(n)]
        dp[0][0] = grid[0][0] if grid[0][0] != -1 else neg
        for step in range(1, 2 * n - 1):
            ndp = [[neg] * n for _ in range(n)]
            for r1 in range(max(0, step - n + 1), min(n, step + 1)):
                c1 = step - r1
                if grid[r1][c1] == -1:
                    continue
                for r2 in range(max(0, step - n + 1), min(n, step + 1)):
                    c2 = step - r2
                    if grid[r2][c2] == -1:
                        continue
                    best = max(dp[pr1][pr2] for pr1 in (r1, r1 - 1) for pr2 in (r2, r2 - 1) if 0 <= pr1 < n and 0 <= pr2 < n)
                    if best == neg:
                        continue
                    gain = grid[r1][c1] + (0 if (r1, c1) == (r2, c2) else grid[r2][c2])
                    ndp[r1][r2] = best + gain
            dp = ndp
        return max(0, dp[n - 1][n - 1])
```
- **Time:** O(n^3) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Keep only reachable row-pair states in dictionaries.
```python
class Solution:
    def cherryPickup(self, grid: list[list[int]]) -> int:
        n = len(grid)
        states = {(0, 0): grid[0][0]} if grid[0][0] != -1 else {}
        for step in range(1, 2 * n - 1):
            nxt = {}
            for (pr1, pr2), val in states.items():
                for r1 in (pr1, pr1 + 1):
                    c1 = step - r1
                    if not (0 <= r1 < n and 0 <= c1 < n) or grid[r1][c1] == -1:
                        continue
                    for r2 in (pr2, pr2 + 1):
                        c2 = step - r2
                        if not (0 <= r2 < n and 0 <= c2 < n) or grid[r2][c2] == -1:
                            continue
                        gain = grid[r1][c1] + (0 if (r1, c1) == (r2, c2) else grid[r2][c2])
                        nxt[(r1, r2)] = max(nxt.get((r1, r2), -10 ** 9), val + gain)
            states = nxt
        return max(0, states.get((n - 1, n - 1), -10 ** 9))
```
- **Time:** O(n^3) — **Space:** O(n^2)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(n^3) |
| Better | O(n^3) | O(n^2) |
| Optimal | O(n^3) | O(n^2) |

## Edge Cases & Pitfalls
- Blocked endpoints make the answer `0`.
- Count a shared cell once.
- The synchronized move count is essential.

## Related
- Cherry Pickup II
- Unique Paths II
