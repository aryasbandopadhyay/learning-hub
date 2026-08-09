# 12. Largest Plus Sign

- **Difficulty:** Medium
- **Pattern:** Dynamic Programming on Grids/Matrices
- **Asked at:** Google

## Problem
You are given an `n x n` grid initially filled with `1`s and a list of mined cells that are changed to `0`.

A plus sign of order `x` has a center `1` cell and four arms of length `x - 1` extending up, down, left, and right through only `1` cells. Return the largest possible order; return `0` if no `1` cell exists.

**Input**
- `n`: the grid size.
- `mines`: a list of zero-valued coordinates `[row, col]`.

**Output**
- The order of the largest all-ones plus sign.

## Constraints
- `1 <= n <= 500`
- `0 <= mines.length <= 5000`
- `0 <= row, col < n`
- All mined coordinates are unique.

## Examples
```text
Input: n = 5, mines = [[4,2]]
Output: 2
Explanation: With only `[4,2]` mined, a plus sign of order `2` exists, but no center can support arms of length `2` in all directions.
```

## Understanding & Intuition
The order at a center is the minimum run of consecutive ones in four directions. Directional DP turns each center query into a constant-time minimum. A mine resets directional counts to zero.

## Approach 1 — Naive / Brute Force
**Idea:** Build four directional run-length tables.
```python
class Solution:
    def orderOfLargestPlusSign(self, n: int, mines: list[list[int]]) -> int:
        banned = {tuple(x) for x in mines}
        left = [[0] * n for _ in range(n)]
        right = [[0] * n for _ in range(n)]
        up = [[0] * n for _ in range(n)]
        down = [[0] * n for _ in range(n)]
        for r in range(n):
            for c in range(n):
                if (r, c) not in banned:
                    left[r][c] = (left[r][c - 1] if c else 0) + 1
                    up[r][c] = (up[r - 1][c] if r else 0) + 1
        for r in range(n - 1, -1, -1):
            for c in range(n - 1, -1, -1):
                if (r, c) not in banned:
                    right[r][c] = (right[r][c + 1] if c + 1 < n else 0) + 1
                    down[r][c] = (down[r + 1][c] if r + 1 < n else 0) + 1
        return max(min(left[r][c], right[r][c], up[r][c], down[r][c]) for r in range(n) for c in range(n))
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 2 — Better
**Idea:** Minimize one grid with separate horizontal and vertical scans.
```python
class Solution:
    def orderOfLargestPlusSign(self, n: int, mines: list[list[int]]) -> int:
        banned = {tuple(x) for x in mines}
        dp = [[n] * n for _ in range(n)]
        for r in range(n):
            cnt = 0
            for c in range(n):
                cnt = 0 if (r, c) in banned else cnt + 1
                dp[r][c] = min(dp[r][c], cnt)
            cnt = 0
            for c in range(n - 1, -1, -1):
                cnt = 0 if (r, c) in banned else cnt + 1
                dp[r][c] = min(dp[r][c], cnt)
        for c in range(n):
            cnt = 0
            for r in range(n):
                cnt = 0 if (r, c) in banned else cnt + 1
                dp[r][c] = min(dp[r][c], cnt)
            cnt = 0
            for r in range(n - 1, -1, -1):
                cnt = 0 if (r, c) in banned else cnt + 1
                dp[r][c] = min(dp[r][c], cnt)
        return max(max(row) for row in dp)
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Scan a row and a column from both ends in the same nested loop.
```python
class Solution:
    def orderOfLargestPlusSign(self, n: int, mines: list[list[int]]) -> int:
        banned = {tuple(x) for x in mines}
        dp = [[n] * n for _ in range(n)]
        for i in range(n):
            left = right = up = down = 0
            for j in range(n):
                left = 0 if (i, j) in banned else left + 1
                dp[i][j] = min(dp[i][j], left)
                right = 0 if (i, n - 1 - j) in banned else right + 1
                dp[i][n - 1 - j] = min(dp[i][n - 1 - j], right)
                up = 0 if (j, i) in banned else up + 1
                dp[j][i] = min(dp[j][i], up)
                down = 0 if (n - 1 - j, i) in banned else down + 1
                dp[n - 1 - j][i] = min(dp[n - 1 - j][i], down)
        return max(max(row) for row in dp)
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n^2) |
| Better | O(n^2) | O(n^2) |
| Optimal | O(n^2) | O(n^2) |

## Edge Cases & Pitfalls
- If all cells are mines, the answer is `0`.
- Order includes the center.
- Convert mines to a set.

## Related
- Count Square Submatrices with All Ones
- Maximal Square
