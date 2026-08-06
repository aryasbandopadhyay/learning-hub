# 09. Longest Increasing Path in a Matrix

- **Difficulty:** Hard
- **Pattern:** 2-D Dynamic Programming
- **Asked at:** Google, Amazon, Microsoft, Meta

## Problem
Given an `m x n` integer matrix, return the length of the longest path where adjacent cells move up, down, left, or right and values strictly increase. Constraints: `1 <= m, n <= 200`, `0 <= matrix[i][j] <= 2^31 - 1`.

## Examples
```text
Input: matrix = [[9,9,4],[6,6,8],[2,1,1]]
Output: 4
Explanation: One longest path is 1 -> 2 -> 6 -> 9.
```

## Understanding & Intuition
Let `dp[r][c]` be the longest increasing path starting at cell `(r, c)`. From a cell, you may move to any neighbor with a larger value, then add one. The graph is acyclic because values strictly increase.

## Approach 1 — Naive / Brute Force
**Idea:** (recursion)
```python
from typing import List

class SolutionRecursive:
    def longestIncreasingPath(self, matrix: List[List[int]]) -> int:
        m, n = len(matrix), len(matrix[0])
        dirs = [(1, 0), (-1, 0), (0, 1), (0, -1)]

        def dfs(r: int, c: int) -> int:
            best = 1
            for dr, dc in dirs:
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and matrix[nr][nc] > matrix[r][c]:
                    best = max(best, 1 + dfs(nr, nc))
            return best

        return max(dfs(r, c) for r in range(m) for c in range(n))
```
- **Time:** O(4^(mn)) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** (memoization)
```python
from typing import List

class SolutionMemoized:
    def longestIncreasingPath(self, matrix: List[List[int]]) -> int:
        m, n = len(matrix), len(matrix[0])
        memo = [[0] * n for _ in range(m)]
        dirs = [(1, 0), (-1, 0), (0, 1), (0, -1)]

        def dfs(r: int, c: int) -> int:
            if memo[r][c]:
                return memo[r][c]
            memo[r][c] = 1
            for dr, dc in dirs:
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and matrix[nr][nc] > matrix[r][c]:
                    memo[r][c] = max(memo[r][c], 1 + dfs(nr, nc))
            return memo[r][c]

        return max(dfs(r, c) for r in range(m) for c in range(n))
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** (tabulation, space-optimized where possible)
```python
from typing import List

class Solution:
    def longestIncreasingPath(self, matrix: List[List[int]]) -> int:
        m, n = len(matrix), len(matrix[0])
        cells = sorted((matrix[r][c], r, c) for r in range(m) for c in range(n))
        dp = [[1] * n for _ in range(m)]
        dirs = [(1, 0), (-1, 0), (0, 1), (0, -1)]
        for _, r, c in cells:
            for dr, dc in dirs:
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and matrix[nr][nc] > matrix[r][c]:
                    dp[nr][nc] = max(dp[nr][nc], dp[r][c] + 1)
        return max(max(row) for row in dp)
```
- **Time:** O(mn log(mn)) — **Space:** O(mn)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(4^(mn)) | O(mn) |
| Better | O(mn) | O(mn) |
| Optimal | O(mn log(mn)) | O(mn) |

## Edge Cases & Pitfalls
- Equal-valued neighbors are not increasing.
- Sorting gives a bottom-up topological order by value.

## Related
- Number of Islands
- Pacific Atlantic Water Flow
