# 06. Count Square Submatrices with All Ones

- **Difficulty:** Medium
- **Pattern:** Dynamic Programming on Grids/Matrices
- **Asked at:** Google

## Problem
You are given a binary matrix.

Count every square submatrix whose cells are all `1`. Squares with different sizes or positions are counted separately.

**Input**
- `matrix`: an `m x n` matrix containing only `0` and `1`.

**Output**
- The total number of all-ones square submatrices.

## Constraints
- `1 <= m, n <= 300`
- `matrix[r][c]` is `0` or `1`

## Examples
```text
Input: matrix = [[0,1,1,1],[1,1,1,1],[0,1,1,1]]
Output: 15
Explanation: There are ten `1x1` squares, four `2x2` squares, and one `3x3` square, for a total of `15`.
```

## Understanding & Intuition
The largest all-one square ending at a cell is one plus the minimum of top, left, and top-left values. Each side length contributes one square of that size. Summing DP values counts all squares.

## Approach 1 — Naive / Brute Force
**Idea:** Use prefix sums to test every candidate square.
```python
class Solution:
    def countSquares(self, matrix: list[list[int]]) -> int:
        m, n = len(matrix), len(matrix[0])
        pref = [[0] * (n + 1) for _ in range(m + 1)]
        for r in range(m):
            for c in range(n):
                pref[r + 1][c + 1] = matrix[r][c] + pref[r][c + 1] + pref[r + 1][c] - pref[r][c]
        ans = 0
        for r in range(m):
            for c in range(n):
                for s in range(1, min(m - r, n - c) + 1):
                    total = pref[r + s][c + s] - pref[r][c + s] - pref[r + s][c] + pref[r][c]
                    if total == s * s:
                        ans += 1
        return ans
```
- **Time:** O(mn min(m,n)) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Store largest square side ending at each cell.
```python
class Solution:
    def countSquares(self, matrix: list[list[int]]) -> int:
        m, n = len(matrix), len(matrix[0])
        dp = [[0] * n for _ in range(m)]
        ans = 0
        for r in range(m):
            for c in range(n):
                if matrix[r][c]:
                    dp[r][c] = 1 if r == 0 or c == 0 else 1 + min(dp[r - 1][c], dp[r][c - 1], dp[r - 1][c - 1])
                    ans += dp[r][c]
        return ans
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** Use one DP row and a saved diagonal.
```python
class Solution:
    def countSquares(self, matrix: list[list[int]]) -> int:
        m, n = len(matrix), len(matrix[0])
        dp = [0] * (n + 1)
        ans = 0
        for r in range(1, m + 1):
            diag = 0
            for c in range(1, n + 1):
                old = dp[c]
                if matrix[r - 1][c - 1]:
                    dp[c] = 1 + min(dp[c], dp[c - 1], diag)
                    ans += dp[c]
                else:
                    dp[c] = 0
                diag = old
        return ans
```
- **Time:** O(mn) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn min(m,n)) | O(mn) |
| Better | O(mn) | O(mn) |
| Optimal | O(mn) | O(n) |

## Edge Cases & Pitfalls
- Zero cells contribute nothing.
- Sum side lengths, not just the maximum side.
- Rectangular matrices are allowed.

## Related
- Maximal Square
- Largest Plus Sign
