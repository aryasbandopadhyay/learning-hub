# 01. Set Matrix Zeroes

- **Difficulty:** Medium
- **Pattern:** Matrix
- **Asked at:** Amazon, Microsoft, Google, Meta

## Problem
Given an `m x n` matrix, if any original cell is `0`, set its entire row and column to `0`.

The changes are based on the original matrix, not zeros created during the process. Modify `matrix` in place and return the final matrix for the judge.

**Input**
- `matrix`: a 2-D list of integers.

**Output**
- The final matrix. **This judge compares exactly**, so every cell must match.

## Constraints
- `m == matrix.length`
- `n == matrix[r].length`
- `1 <= m, n <= 200`
- `-2^31 <= matrix[r][c] <= 2^31 - 1`.

## Examples
```text
Input: matrix = [[1,1,1],[1,0,1],[1,1,1]]
Output: [[1,0,1],[0,0,0],[1,0,1]]
Explanation: The original zero is at row 1, column 1. That row and column become zero, while other cells remain unchanged.
```

## Understanding & Intuition
Zeroing immediately can accidentally create new zeroes that should not trigger more changes. We must remember which rows and columns originally contained zeroes. The optimal trick stores that information inside the first row and first column.

## Approach 1 — Naive / Brute Force
**Idea:** Copy the matrix first, then use the copy to decide which original zeroes should clear rows and columns.
```python
from typing import List

class Solution:
    def setZeroes(self, matrix: List[List[int]]) -> None:
        # Keep original values so new zeroes do not cascade.
        m, n = len(matrix), len(matrix[0])
        original = [row[:] for row in matrix]
        for r in range(m):
            for c in range(n):
                if original[r][c] == 0:
                    for j in range(n):
                        matrix[r][j] = 0
                    for i in range(m):
                        matrix[i][c] = 0
```
- **Time:** O(mn(m+n)) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Store only the row and column indices that contain original zeroes.
```python
from typing import List

class Solution:
    def setZeroes(self, matrix: List[List[int]]) -> None:
        m, n = len(matrix), len(matrix[0])
        rows, cols = set(), set()
        for r in range(m):
            for c in range(n):
                if matrix[r][c] == 0:
                    rows.add(r)
                    cols.add(c)

        # Clear cells whose row or column was marked.
        for r in range(m):
            for c in range(n):
                if r in rows or c in cols:
                    matrix[r][c] = 0
```
- **Time:** O(mn) — **Space:** O(m+n)

## Approach 3 — Optimal
**Idea:** Use the first row and first column as marker arrays, plus one flag for whether the first column itself must become zero.
```python
from typing import List

class Solution:
    def setZeroes(self, matrix: List[List[int]]) -> None:
        m, n = len(matrix), len(matrix[0])
        first_col_zero = False

        # Mark rows in column 0 and columns in row 0.
        for r in range(m):
            if matrix[r][0] == 0:
                first_col_zero = True
            for c in range(1, n):
                if matrix[r][c] == 0:
                    matrix[r][0] = 0
                    matrix[0][c] = 0

        # Apply marks to the inner matrix.
        for r in range(1, m):
            for c in range(1, n):
                if matrix[r][0] == 0 or matrix[0][c] == 0:
                    matrix[r][c] = 0

        if matrix[0][0] == 0:
            for c in range(n):
                matrix[0][c] = 0
        if first_col_zero:
            for r in range(m):
                matrix[r][0] = 0
```
- **Time:** O(mn) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn(m+n)) | O(mn) |
| Better | O(mn) | O(m+n) |
| Optimal | O(mn) | O(1) |

## Edge Cases & Pitfalls
- First row and first column need separate handling.
- Do not zero while scanning unless original zero positions are preserved.

## Related
- Game of Life
- Rotate Image
