# 15. Maximal Square

- **Difficulty:** Medium
- **Pattern:** 2-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Meta

## Problem
Given an `m x n` binary matrix of characters `'0'` and `'1'`, return the area of the largest square containing only `1`s. Constraints: `1 <= m, n <= 300`.

## Examples
```text
Input: matrix = [["1","0","1","0","0"],["1","0","1","1","1"],["1","1","1","1","1"],["1","0","0","1","0"]]
Output: 4
Explanation: The largest all-1 square has side length 2.
```

## Understanding & Intuition
Let `dp[r][c]` be the side length of the largest all-1 square starting at `(r, c)`. If the cell is `1`, the side is one plus the minimum of right, down, and diagonal states. Track the maximum side and square it for area.

## Approach 1 — Naive / Brute Force
**Idea:** (recursion)
```python
from typing import List

class SolutionRecursive:
    def maximalSquare(self, matrix: List[List[str]]) -> int:
        m, n = len(matrix), len(matrix[0])

        def dfs(r: int, c: int) -> int:
            if r == m or c == n or matrix[r][c] == "0":
                return 0
            return 1 + min(dfs(r + 1, c), dfs(r, c + 1), dfs(r + 1, c + 1))

        best = 0
        for r in range(m):
            for c in range(n):
                best = max(best, dfs(r, c))
        return best * best
```
- **Time:** O(3^(mn)) — **Space:** O(m+n)

## Approach 2 — Better
**Idea:** (memoization)
```python
from typing import List

class SolutionMemoized:
    def maximalSquare(self, matrix: List[List[str]]) -> int:
        m, n = len(matrix), len(matrix[0])
        memo = {}

        def dfs(r: int, c: int) -> int:
            if r == m or c == n or matrix[r][c] == "0":
                return 0
            if (r, c) not in memo:
                memo[(r, c)] = 1 + min(dfs(r + 1, c), dfs(r, c + 1), dfs(r + 1, c + 1))
            return memo[(r, c)]

        best = 0
        for r in range(m):
            for c in range(n):
                best = max(best, dfs(r, c))
        return best * best
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** (tabulation, space-optimized where possible)
```python
from typing import List

class Solution:
    def maximalSquare(self, matrix: List[List[str]]) -> int:
        m, n = len(matrix), len(matrix[0])
        dp = [0] * (n + 1)
        best = 0
        for r in range(m - 1, -1, -1):
            diag = 0
            for c in range(n - 1, -1, -1):
                old = dp[c]
                if matrix[r][c] == "1":
                    dp[c] = 1 + min(dp[c], dp[c + 1], diag)
                    best = max(best, dp[c])
                else:
                    dp[c] = 0
                diag = old
        return best * best
```
- **Time:** O(mn) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(3^(mn)) | O(m+n) |
| Better | O(mn) | O(mn) |
| Optimal | O(mn) | O(n) |

## Edge Cases & Pitfalls
- Return area, not side length.
- Character cells are `"0"`/`"1"`, not integers on LeetCode.

## Related
- Largest Rectangle in Histogram
- Count Square Submatrices with All Ones
