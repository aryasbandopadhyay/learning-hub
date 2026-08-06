# 11. Paths in Matrix Whose Sum Is Divisible by K

- **Difficulty:** Hard
- **Pattern:** Dynamic Programming on Grids/Matrices
- **Asked at:** Google

## Problem
Given `grid` and integer `k`, count paths from top-left to bottom-right moving only right or down whose path sum is divisible by `k`. Return the count modulo `1_000_000_007`.

Constraints: `1 <= m,n <= 50`, `1 <= k <= 50`, `0 <= grid[i][j] <= 100`.

## Examples
```text
Input: grid = [[5,2,4],[3,0,5],[0,7,2]], k = 3
Output: 2
Explanation: Exactly two top-left to bottom-right paths have sums divisible by 3.
```

## Understanding & Intuition
Only the running sum modulo `k` matters. For each cell, store how many paths arrive with each remainder. Entering a cell shifts previous remainders by that cell's value.

## Approach 1 — Naive / Brute Force
**Idea:** Push transitions through a full 3D DP table.
```python
class Solution:
    def numberOfPaths(self, grid: list[list[int]], k: int) -> int:
        mod = 10 ** 9 + 7
        m, n = len(grid), len(grid[0])
        dp = [[[0] * k for _ in range(n)] for _ in range(m)]
        dp[0][0][grid[0][0] % k] = 1
        for r in range(m):
            for c in range(n):
                for rem, cnt in enumerate(dp[r][c]):
                    if cnt == 0:
                        continue
                    if r + 1 < m:
                        nr = (rem + grid[r + 1][c]) % k
                        dp[r + 1][c][nr] = (dp[r + 1][c][nr] + cnt) % mod
                    if c + 1 < n:
                        nr = (rem + grid[r][c + 1]) % k
                        dp[r][c + 1][nr] = (dp[r][c + 1][nr] + cnt) % mod
        return dp[-1][-1][0]
```
- **Time:** O(mnk) — **Space:** O(mnk)

## Approach 2 — Better
**Idea:** Pull transitions from the top and left into each cell.
```python
class Solution:
    def numberOfPaths(self, grid: list[list[int]], k: int) -> int:
        mod = 10 ** 9 + 7
        m, n = len(grid), len(grid[0])
        dp = [[[0] * k for _ in range(n)] for _ in range(m)]
        for r in range(m):
            for c in range(n):
                val = grid[r][c] % k
                if r == 0 and c == 0:
                    dp[r][c][val] = 1
                    continue
                for rem in range(k):
                    prev = (rem - val) % k
                    ways = (dp[r - 1][c][prev] if r else 0) + (dp[r][c - 1][prev] if c else 0)
                    dp[r][c][rem] = ways % mod
        return dp[m - 1][n - 1][0]
```
- **Time:** O(mnk) — **Space:** O(mnk)

## Approach 3 — Optimal
**Idea:** Roll rows while keeping each column's remainder counts.
```python
class Solution:
    def numberOfPaths(self, grid: list[list[int]], k: int) -> int:
        mod = 10 ** 9 + 7
        m, n = len(grid), len(grid[0])
        dp = [[0] * k for _ in range(n)]
        for r in range(m):
            for c in range(n):
                val = grid[r][c] % k
                cur = [0] * k
                if r == 0 and c == 0:
                    cur[val] = 1
                else:
                    for old in range(k):
                        nr = (old + val) % k
                        if r:
                            cur[nr] = (cur[nr] + dp[c][old]) % mod
                        if c:
                            cur[nr] = (cur[nr] + dp[c - 1][old]) % mod
                dp[c] = cur
        return dp[-1][0]
```
- **Time:** O(mnk) — **Space:** O(nk)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mnk) | O(mnk) |
| Better | O(mnk) | O(mnk) |
| Optimal | O(mnk) | O(nk) |

## Edge Cases & Pitfalls
- Initialize the start cell's remainder.
- Only right and down moves are allowed.
- Count paths modulo `1_000_000_007`.

## Related
- Number of Increasing Paths in a Grid
- Unique Paths
