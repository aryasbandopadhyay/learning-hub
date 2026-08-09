# 02. Unique Paths II

- **Difficulty:** Medium
- **Pattern:** 2-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Bloomberg

## Problem
A robot starts at the top-left cell of a grid and wants to reach the bottom-right cell. It may move
only down or right. `obstacleGrid[r][c]` is `1` if the cell is blocked and `0` if it is open.

Return the number of valid paths that never step on an obstacle.

**Input**
- `obstacleGrid`: an `m x n` grid of `0`s and `1`s.

**Output**
- An integer: the number of valid paths from the top-left to the bottom-right cell.

## Constraints
- m == obstacleGrid.length
- n == obstacleGrid[r].length
- 1 <= m, n <= 100
- obstacleGrid[r][c] is either `0` or `1`.

## Examples
```text
Input: obstacleGrid = [[0,0,0],[0,1,0],[0,0,0]]
Output: 2
Explanation: The center cell is blocked, so only the route around the top/right edge and the route around the left/bottom edge remain valid.
```

## Understanding & Intuition
Let `dp[r][c]` be the number of paths from `(r, c)` to the goal. Obstacle cells have value zero. Otherwise, paths equal the sum from down and right neighbors.

## Approach 1 — Naive / Brute Force
**Idea:** (recursion)
```python
from typing import List

class SolutionRecursive:
    def uniquePathsWithObstacles(self, obstacleGrid: List[List[int]]) -> int:
        m, n = len(obstacleGrid), len(obstacleGrid[0])

        def dfs(r: int, c: int) -> int:
            if r == m or c == n or obstacleGrid[r][c] == 1:
                return 0
            if r == m - 1 and c == n - 1:
                return 1
            return dfs(r + 1, c) + dfs(r, c + 1)

        return dfs(0, 0)
```
- **Time:** O(2^(m+n)) — **Space:** O(m+n)

## Approach 2 — Better
**Idea:** (memoization)
```python
from typing import List

class SolutionMemoized:
    def uniquePathsWithObstacles(self, obstacleGrid: List[List[int]]) -> int:
        m, n = len(obstacleGrid), len(obstacleGrid[0])
        memo = {}

        def dfs(r: int, c: int) -> int:
            if r == m or c == n or obstacleGrid[r][c] == 1:
                return 0
            if r == m - 1 and c == n - 1:
                return 1
            if (r, c) not in memo:
                memo[(r, c)] = dfs(r + 1, c) + dfs(r, c + 1)
            return memo[(r, c)]

        return dfs(0, 0)
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** (tabulation, space-optimized where possible)
```python
from typing import List

class Solution:
    def uniquePathsWithObstacles(self, obstacleGrid: List[List[int]]) -> int:
        m, n = len(obstacleGrid), len(obstacleGrid[0])
        dp = [0] * n
        dp[n - 1] = 1 if obstacleGrid[m - 1][n - 1] == 0 else 0
        for r in range(m - 1, -1, -1):
            for c in range(n - 1, -1, -1):
                if obstacleGrid[r][c] == 1:
                    dp[c] = 0
                elif c + 1 < n:
                    dp[c] += dp[c + 1]
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
- If start or end is blocked, the answer is zero.
- Reset the current cell to zero when it is an obstacle.

## Related
- Unique Paths
- Minimum Path Sum
