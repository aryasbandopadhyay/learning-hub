# 08. Minimum Falling Path Sum II

- **Difficulty:** Hard
- **Pattern:** Dynamic Programming on Grids/Matrices
- **Asked at:** Google

## Problem
Given an `n x n` grid, choose one element from each row so that adjacent rows do not choose the same column. Return the minimum possible sum.

Constraints: `1 <= n <= 200`, `-99 <= grid[i][j] <= 99`.

## Examples
```text
Input: grid = [[1,2,3],[4,5,6],[7,8,9]]
Output: 13
Explanation: Choose 1, then 5, then 7.
```

## Understanding & Intuition
Each cell needs the minimum previous-row value from a different column. Scanning every previous column is simple but slow. Tracking the smallest and second smallest previous values makes each transition constant time.

## Approach 1 — Naive / Brute Force
**Idea:** Directly scan all previous columns for each cell.
```python
class Solution:
    def minFallingPathSum(self, grid: list[list[int]]) -> int:
        n = len(grid)
        if n == 1:
            return grid[0][0]
        dp = [row[:] for row in grid]
        for r in range(1, n):
            for c in range(n):
                dp[r][c] += min(dp[r - 1][pc] for pc in range(n) if pc != c)
        return min(dp[-1])
```
- **Time:** O(n^3) — **Space:** O(n^2)

## Approach 2 — Better
**Idea:** Keep the smallest and second-smallest values of the previous row.
```python
class Solution:
    def minFallingPathSum(self, grid: list[list[int]]) -> int:
        n = len(grid)
        prev = grid[0][:]
        for r in range(1, n):
            first = second = 10 ** 18
            idx = -1
            for c, v in enumerate(prev):
                if v < first:
                    second, first, idx = first, v, c
                elif v < second:
                    second = v
            prev = [grid[r][c] + (second if c == idx else first) for c in range(n)]
        return min(prev)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Carry only the best value, its column, and the second best value.
```python
class Solution:
    def minFallingPathSum(self, grid: list[list[int]]) -> int:
        best1 = best2 = 0
        best_col = -1
        for row in grid:
            nb1 = nb2 = 10 ** 18
            nb_col = -1
            for c, x in enumerate(row):
                v = x + (best2 if c == best_col else best1)
                if v < nb1:
                    nb2, nb1, nb_col = nb1, v, c
                elif v < nb2:
                    nb2 = v
            best1, best2, best_col = nb1, nb2, nb_col
        return best1
```
- **Time:** O(n^2) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(n^2) |
| Better | O(n^2) | O(n) |
| Optimal | O(n^2) | O(1) |

## Edge Cases & Pitfalls
- For `n = 1`, return the only value.
- Exclude only the immediately previous column.
- Negative values are valid.

## Related
- Minimum Falling Path Sum
- Paint House II
