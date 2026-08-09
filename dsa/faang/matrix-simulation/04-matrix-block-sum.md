# 04. Matrix Block Sum

- **Difficulty:** Medium
- **Pattern:** matrix simulation & transformation
- **Asked at:** Amazon, Google, Meta

## Problem
Given a matrix `mat` and an integer `k`, return a matrix `answer` where `answer[i][j]` is the sum of all `mat[r][c]` such that `|r-i| <= k` and `|c-j| <= k`.

**Input**
- `mat`: a `list[list[int]]`; the input matrix.
- `k`: a `int`; the integer parameter described above.

**Output**
- A `list[list[int]]`. Return a matrix `answer` where `answer[i][j]` is the sum of all `mat[r][c]` such that `|r-i| <= k` and `|c-j| <= k`. This judge compares the sequence exactly: return `answer[i][j]` at the same row and column as `mat[i][j]`.

## Constraints
- `1 <= m, n <= 100`, `0 <= k <= 100`, values fit in a signed 32-bit integer.

## Examples
```text
Input: mat = [[1,2,3],[4,5,6],[7,8,9]], k = 1
Output: [[12,21,16],[27,45,33],[24,39,28]]
Explanation: The center cell includes the whole matrix; corners include their clipped 2x2 blocks. The output is written in the required deterministic order.
```

## Understanding & Intuition
Each output cell asks for a clipped rectangle sum around that cell. Computing every rectangle from scratch repeats work. Prefix sums let each rectangle be answered from a constant number of stored sums.

## Approach 1 — Naive / Brute Force
**Idea:** For every cell, scan all valid neighbors inside its block.
```python
class Solution:
    def matrixBlockSum(self, mat: list[list[int]], k: int) -> list[list[int]]:
        m, n = len(mat), len(mat[0])
        ans = [[0] * n for _ in range(m)]
        for i in range(m):
            for j in range(n):
                total = 0
                for r in range(max(0, i - k), min(m, i + k + 1)):
                    for c in range(max(0, j - k), min(n, j + k + 1)):
                        total += mat[r][c]
                ans[i][j] = total
        return ans
```
- **Time:** O(m*n*k^2) — **Space:** O(m*n)

## Approach 2 — Better
**Idea:** Use row prefix sums so each row segment in the block is O(1).
```python
class Solution:
    def matrixBlockSum(self, mat: list[list[int]], k: int) -> list[list[int]]:
        m, n = len(mat), len(mat[0])
        row_pref = [[0] * (n + 1) for _ in range(m)]
        for r in range(m):
            for c in range(n):
                row_pref[r][c + 1] = row_pref[r][c] + mat[r][c]
        ans = [[0] * n for _ in range(m)]
        for i in range(m):
            for j in range(n):
                left, right = max(0, j - k), min(n - 1, j + k)
                total = 0
                for r in range(max(0, i - k), min(m, i + k + 1)):
                    total += row_pref[r][right + 1] - row_pref[r][left]
                ans[i][j] = total
        return ans
```
- **Time:** O(m*n*k) — **Space:** O(m*n)

## Approach 3 — Optimal
**Idea:** Build a 2D prefix sum and answer each clipped block in O(1).
```python
class Solution:
    def matrixBlockSum(self, mat: list[list[int]], k: int) -> list[list[int]]:
        m, n = len(mat), len(mat[0])
        pref = [[0] * (n + 1) for _ in range(m + 1)]
        for r in range(m):
            for c in range(n):
                pref[r + 1][c + 1] = mat[r][c] + pref[r][c + 1] + pref[r + 1][c] - pref[r][c]
        ans = [[0] * n for _ in range(m)]
        for i in range(m):
            for j in range(n):
                r1, c1 = max(0, i - k), max(0, j - k)
                r2, c2 = min(m - 1, i + k), min(n - 1, j + k)
                ans[i][j] = pref[r2 + 1][c2 + 1] - pref[r1][c2 + 1] - pref[r2 + 1][c1] + pref[r1][c1]
        return ans
```
- **Time:** O(m*n) — **Space:** O(m*n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(m*n*k^2) | O(m*n) |
| Better | O(m*n*k) | O(m*n) |
| Optimal | O(m*n) | O(m*n) |

## Edge Cases & Pitfalls
- Clip block boundaries at matrix edges.
- `k = 0` should return a copy of the original values.
- Use an extra prefix row and column to simplify formulas.

## Related
- Range Sum Query 2D Batch
- Image Smoother
