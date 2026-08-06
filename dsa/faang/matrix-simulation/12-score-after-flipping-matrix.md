# 12. Score After Flipping Matrix

- **Difficulty:** Medium
- **Pattern:** matrix simulation & transformation
- **Asked at:** Google, Meta, Amazon

## Problem
Given a binary matrix `grid`, you may flip any row or column any number of times. A flip toggles every bit in that row or column. Treat each row as a binary number and return the maximum possible total score.

Constraints: `1 <= m, n <= 20`, `grid[i][j]` is `0` or `1`.

## Examples
```text
Input: grid = [[0,0,1,1],[1,0,1,0],[1,1,0,0]]
Output: 39
Explanation: Flip rows with leading 0, then flip columns that increase the number of 1s.
```

## Understanding & Intuition
The leftmost bit has the highest value, so every row should start with `1`. After making that choice, each remaining column contributes independently, and we keep the larger count between ones and zeros. This avoids enumerating all flip combinations.

## Approach 1 — Naive / Brute Force
**Idea:** Try every subset of row flips, then greedily choose beneficial column flips for that row state.
```python
class Solution:
    def matrixScore(self, grid: list[list[int]]) -> int:
        m, n = len(grid), len(grid[0])
        best = 0
        for mask in range(1 << m):
            total = 0
            for c in range(n):
                ones = 0
                for r in range(m):
                    bit = grid[r][c]
                    if (mask >> r) & 1:
                        bit ^= 1
                    ones += bit
                ones = max(ones, m - ones)
                total += ones * (1 << (n - 1 - c))
            best = max(best, total)
        return best
```
- **Time:** O(2^m*m*n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Materialize row flips to make the first column all ones, then flip any column with more zeros than ones.
```python
class Solution:
    def matrixScore(self, grid: list[list[int]]) -> int:
        mat = [row[:] for row in grid]
        m, n = len(mat), len(mat[0])
        for r in range(m):
            if mat[r][0] == 0:
                for c in range(n):
                    mat[r][c] ^= 1
        for c in range(1, n):
            ones = sum(mat[r][c] for r in range(m))
            if ones < m - ones:
                for r in range(m):
                    mat[r][c] ^= 1
        score = 0
        for row in mat:
            value = 0
            for bit in row:
                value = value * 2 + bit
            score += value
        return score
```
- **Time:** O(m*n) — **Space:** O(m*n)

## Approach 3 — Optimal
**Idea:** Do not mutate the matrix; virtually flip rows with leading zero and count the best contribution of each column.
```python
class Solution:
    def matrixScore(self, grid: list[list[int]]) -> int:
        m, n = len(grid), len(grid[0])
        score = m * (1 << (n - 1))
        for c in range(1, n):
            ones = 0
            for r in range(m):
                bit = grid[r][c]
                if grid[r][0] == 0:
                    bit ^= 1
                ones += bit
            score += max(ones, m - ones) * (1 << (n - 1 - c))
        return score
```
- **Time:** O(m*n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^m*m*n) | O(1) |
| Better | O(m*n) | O(m*n) |
| Optimal | O(m*n) | O(1) |

## Edge Cases & Pitfalls
- The most significant bit dominates all later bits in a row.
- Column flips are independent only after row-leading bits are fixed.
- Do not return the transformed matrix; this problem returns the maximum score.

## Related
- Maximum Matrix Sum
- Minimum Operations to Make a Uni-Value Grid
