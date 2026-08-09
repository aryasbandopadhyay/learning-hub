# 03. Set Matrix Zeroes

- **Difficulty:** Medium
- **Pattern:** Math & Geometry
- **Asked at:** Amazon, Microsoft, Meta, Google

## Problem
Given an `m x n` integer matrix, if any cell is `0`, set **every cell in that row and every cell in that column** to `0`.

All zeroing decisions are based on the matrix's original contents. A zero created while processing should not cause additional rows or columns to be cleared. Update the given `matrix` in-place.

**Input**
- `matrix`: a 2-D list of integers with `m` rows and `n` columns.

**Output**
- Modify `matrix` in-place. The judge checks the final matrix exactly, row by row.

## Constraints
- `m == matrix.length`
- `n == matrix[i].length`
- `1 <= m, n <= 200`
- `-2^31 <= matrix[i][j] <= 2^31 - 1`

## Examples
```text
Input: matrix = [[1,1,1],[1,0,1],[1,1,1]]
Output: [[1,0,1],[0,0,0],[1,0,1]]
Explanation: The original zero is at `(1, 1)`, so row `1` and column `1` are cleared. Other cells that were not in that row or column keep their original value.
```

```text
Input: matrix = [[1,2],[3,4]]
Output: [[1,2],[3,4]]
Explanation: There are no original zeroes, so the matrix remains unchanged.
```

## Understanding & Intuition
Zeroing immediately would spread zeros incorrectly. First record which original rows and columns contain zero. The optimal version uses the first row and column as those markers.

## Approach 1 — Naive / Brute Force
**Idea:** Copy the matrix, inspect original zeros in the copy, and mutate the real matrix.
```python
from typing import List

class Solution:
    def setZeroes(self, matrix: List[List[int]]) -> None:
        rows, cols = len(matrix), len(matrix[0])
        original = [row[:] for row in matrix]
        for r in range(rows):
            for c in range(cols):
                if original[r][c] == 0:
                    for j in range(cols):
                        matrix[r][j] = 0
                    for i in range(rows):
                        matrix[i][c] = 0
```
- **Time:** O(mn(m+n)) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Store zero rows and zero columns in sets, then apply them.
```python
from typing import List

class Solution:
    def setZeroes(self, matrix: List[List[int]]) -> None:
        rows, cols = len(matrix), len(matrix[0])
        zero_rows, zero_cols = set(), set()
        for r in range(rows):
            for c in range(cols):
                if matrix[r][c] == 0:
                    zero_rows.add(r)
                    zero_cols.add(c)
        for r in range(rows):
            for c in range(cols):
                if r in zero_rows or c in zero_cols:
                    matrix[r][c] = 0
```
- **Time:** O(mn) — **Space:** O(m+n)

## Approach 3 — Optimal
**Idea:** Use first row and first column as marker arrays, with one flag for column zero.
```python
from typing import List

class Solution:
    def setZeroes(self, matrix: List[List[int]]) -> None:
        rows, cols = len(matrix), len(matrix[0])
        first_col_zero = any(matrix[r][0] == 0 for r in range(rows))
        first_row_zero = any(matrix[0][c] == 0 for c in range(cols))
        for r in range(1, rows):
            for c in range(1, cols):
                if matrix[r][c] == 0:
                    matrix[r][0] = matrix[0][c] = 0
        for r in range(1, rows):
            for c in range(1, cols):
                if matrix[r][0] == 0 or matrix[0][c] == 0:
                    matrix[r][c] = 0
        if first_row_zero:
            for c in range(cols):
                matrix[0][c] = 0
        if first_col_zero:
            for r in range(rows):
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
- Preserve whether the first row and first column originally had zeros.
- Avoid treating newly written zeros as original zeros.

## Related
- Game of Life
- Rotate Image
