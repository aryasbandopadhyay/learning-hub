# 01. Minimum Falling Path Sum

- **Difficulty:** Medium
- **Pattern:** Dynamic Programming on Grids/Matrices
- **Asked at:** Google

## Problem
You are given an `n x n` integer matrix.

A falling path starts at any cell in the first row. From `(r, c)`, the next step goes to row `r + 1` in column `c - 1`, `c`, or `c + 1`, if that column exists. Return the minimum sum of a falling path from the first row to the last row.

**Input**
- `matrix`: an `n x n` list of integer rows.

**Output**
- The minimum falling-path sum.

## Constraints
- `1 <= n <= 100`
- `-100 <= matrix[r][c] <= 100`

## Examples
```text
Input: matrix = [[2,1,3],[6,5,4],[7,8,9]]
Output: 13
Explanation: The path `1 -> 5 -> 7` has sum `13`, and every other allowed top-to-bottom path is at least as large.
```

## Understanding & Intuition
Each cell depends only on up to three cells in the previous row. A full table is easy to reason about, but only the previous row is needed. Boundary columns need special handling.

## Approach 1 — Naive / Brute Force
**Idea:** Store the best sum ending at every cell in a 2D DP table.
```python
class Solution:
    def minFallingPathSum(self, matrix: list[list[int]]) -> int:
        n = len(matrix)
        dp = [[0] * n for _ in range(n)]
        dp[0] = matrix[0][:]
        for r in range(1, n):
            for c in range(n):
                best = dp[r - 1][c]
                if c > 0:
                    best = min(best, dp[r - 1][c - 1])
                if c + 1 < n:
                    best = min(best, dp[r - 1][c + 1])
                dp[r][c] = matrix[r][c] + best
        return min(dp[-1])
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 2 — Better
**Idea:** Keep only the previous DP row.
```python
class Solution:
    def minFallingPathSum(self, matrix: list[list[int]]) -> int:
        n = len(matrix)
        prev = matrix[0][:]
        for r in range(1, n):
            cur = [0] * n
            for c in range(n):
                best = prev[c]
                if c:
                    best = min(best, prev[c - 1])
                if c + 1 < n:
                    best = min(best, prev[c + 1])
                cur[c] = matrix[r][c] + best
            prev = cur
        return min(prev)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Reuse one DP row while snapshotting old values for the transition.
```python
class Solution:
    def minFallingPathSum(self, matrix: list[list[int]]) -> int:
        dp = matrix[0][:]
        n = len(dp)
        for r in range(1, n):
            old = dp[:]
            for c in range(n):
                best = old[c]
                if c:
                    best = min(best, old[c - 1])
                if c + 1 < n:
                    best = min(best, old[c + 1])
                dp[c] = matrix[r][c] + best
        return min(dp)
```
- **Time:** O(n^2) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n^2) |
| Better | O(n^2) | O(n) |
| Optimal | O(n^2) | O(n) |

## Edge Cases & Pitfalls
- A single cell is the answer.
- Negative values are valid.
- Do not read diagonal columns outside the grid.

## Related
- Minimum Falling Path Sum II
- Triangle Minimum Path Sum
