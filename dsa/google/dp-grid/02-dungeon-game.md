# 02. Dungeon Game

- **Difficulty:** Hard
- **Pattern:** Dynamic Programming on Grids/Matrices
- **Asked at:** Google

## Problem
A knight starts at the top-left of `dungeon` and moves only right or down to the bottom-right. Each cell changes health, and health must always be at least `1`. Return the minimum initial health needed.

Constraints: `1 <= m,n <= 200`, `-1000 <= dungeon[i][j] <= 1000`.

## Examples
```text
Input: dungeon = [[-2,-3,3],[-5,-10,1],[10,30,-5]]
Output: 7
Explanation: Initial health 7 is just enough on an optimal route.
```

## Understanding & Intuition
Forward greediness fails because late health cannot prevent earlier death. Work backward and ask how much health is required before entering each cell. The best next move is the cheaper of right and down.

## Approach 1 — Naive / Brute Force
**Idea:** Binary-search starting health and check if any path can survive.
```python
class Solution:
    def calculateMinimumHP(self, dungeon: list[list[int]]) -> int:
        m, n = len(dungeon), len(dungeon[0])
        def can(start):
            best = [[-1] * n for _ in range(m)]
            hp = start + dungeon[0][0]
            if hp <= 0:
                return False
            best[0][0] = hp
            for r in range(m):
                for c in range(n):
                    if best[r][c] <= 0:
                        continue
                    for nr, nc in ((r + 1, c), (r, c + 1)):
                        if nr < m and nc < n:
                            nh = best[r][c] + dungeon[nr][nc]
                            if nh > 0:
                                best[nr][nc] = max(best[nr][nc], nh)
            return best[-1][-1] > 0
        lo, hi = 1, 1
        while not can(hi):
            hi *= 2
        while lo < hi:
            mid = (lo + hi) // 2
            if can(mid):
                hi = mid
            else:
                lo = mid + 1
        return lo
```
- **Time:** O(mn log H) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Use reverse DP with sentinels beyond the princess cell.
```python
class Solution:
    def calculateMinimumHP(self, dungeon: list[list[int]]) -> int:
        m, n = len(dungeon), len(dungeon[0])
        inf = 10 ** 18
        dp = [[inf] * (n + 1) for _ in range(m + 1)]
        dp[m][n - 1] = dp[m - 1][n] = 1
        for r in range(m - 1, -1, -1):
            for c in range(n - 1, -1, -1):
                dp[r][c] = max(1, min(dp[r + 1][c], dp[r][c + 1]) - dungeon[r][c])
        return dp[0][0]
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** Compress the reverse DP to one row.
```python
class Solution:
    def calculateMinimumHP(self, dungeon: list[list[int]]) -> int:
        m, n = len(dungeon), len(dungeon[0])
        inf = 10 ** 18
        dp = [inf] * (n + 1)
        dp[n - 1] = 1
        for r in range(m - 1, -1, -1):
            dp[n] = inf
            for c in range(n - 1, -1, -1):
                dp[c] = max(1, min(dp[c], dp[c + 1]) - dungeon[r][c])
        return dp[0]
```
- **Time:** O(mn) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn log H) | O(mn) |
| Better | O(mn) | O(mn) |
| Optimal | O(mn) | O(n) |

## Edge Cases & Pitfalls
- Health must stay at least `1`.
- A positive cell can reduce the required prior health.
- Compute from the destination backward.

## Related
- Minimum Path Sum
- Maximum Non Negative Product in a Matrix
