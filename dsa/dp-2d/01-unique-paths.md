# 01. Unique Paths

- **Difficulty:** Medium
- **Pattern:** 2-D Dynamic Programming
- **Asked at:** Google, Amazon, Microsoft, Meta

## Problem
A robot starts in the top-left corner of an `m x n` grid and wants to reach the bottom-right corner.
On each move it may go only down or right.

Return the number of distinct paths from start to finish.

**Input**
- `m`: the number of grid rows.
- `n`: the number of grid columns.

**Output**
- An integer: the number of valid down/right paths.

## Constraints
- 1 <= m, n <= 100
- The answer is at most `2 * 10^9`.

## Examples
```text
Input: m = 3, n = 7
Output: 28
Explanation: From the top-left to the bottom-right of a `3 x 7` grid, every path consists of 2 down moves and 6 right moves, which can be arranged in `28` ways.
```

## Understanding & Intuition
Let `dp[r][c]` be the number of paths from cell `(r, c)` to the goal. From any non-goal cell, the answer is paths from the cell below plus paths from the cell to the right. Boundaries contribute zero except the goal, which contributes one.

## Approach 1 — Naive / Brute Force
**Idea:** (recursion)
```python
class SolutionRecursive:
    def uniquePaths(self, m: int, n: int) -> int:
        def dfs(r: int, c: int) -> int:
            # Outside the grid cannot form a path.
            if r == m or c == n:
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
class SolutionMemoized:
    def uniquePaths(self, m: int, n: int) -> int:
        memo = {}

        def dfs(r: int, c: int) -> int:
            if r == m or c == n:
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
class Solution:
    def uniquePaths(self, m: int, n: int) -> int:
        # dp[c] is paths from the current row at column c.
        dp = [1] * n
        for _ in range(m - 2, -1, -1):
            for c in range(n - 2, -1, -1):
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
- Single row or single column has exactly one path.
- Do not count paths after moving outside the grid.

## Related
- Unique Paths II
- Minimum Path Sum
