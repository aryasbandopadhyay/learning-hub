# 01. Rotate Image

- **Difficulty:** Medium
- **Pattern:** Math & Geometry
- **Asked at:** Amazon, Google, Microsoft, Apple

## Problem
Given an `n x n` matrix, rotate it 90 degrees clockwise in-place. Constraints: `1 <= n <= 20`, values fit in a signed integer.

## Examples
```text
Input: matrix = [[1,2,3],[4,5,6],[7,8,9]]
Output: [[7,4,1],[8,5,2],[9,6,3]]
Explanation: Rows become columns from bottom to top.
```

## Understanding & Intuition
Clockwise rotation maps cell `(r, c)` to `(c, n - 1 - r)`. A direct copy is simple but not in-place. The optimal trick is transpose, then reverse each row.

## Approach 1 — Naive / Brute Force
**Idea:** Build a rotated copy, then copy it back into the input matrix.
```python
from typing import List

class Solution:
    def rotate(self, matrix: List[List[int]]) -> None:
        n = len(matrix)
        rotated = [[0] * n for _ in range(n)]
        for r in range(n):
            for c in range(n):
                # Rotation sends (r, c) to (c, n - 1 - r).
                rotated[c][n - 1 - r] = matrix[r][c]
        for r in range(n):
            matrix[r][:] = rotated[r]
```
- **Time:** O(n²) — **Space:** O(n²)

## Approach 2 — Better
**Idea:** Rotate one layer at a time, cycling four corresponding cells.
```python
from typing import List

class Solution:
    def rotate(self, matrix: List[List[int]]) -> None:
        n = len(matrix)
        for layer in range(n // 2):
            first, last = layer, n - 1 - layer
            for i in range(first, last):
                offset = i - first
                top = matrix[first][i]
                # Move left -> top, bottom -> left, right -> bottom, top -> right.
                matrix[first][i] = matrix[last - offset][first]
                matrix[last - offset][first] = matrix[last][last - offset]
                matrix[last][last - offset] = matrix[i][last]
                matrix[i][last] = top
```
- **Time:** O(n²) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Transpose across the main diagonal, then reverse every row.
```python
from typing import List

class Solution:
    def rotate(self, matrix: List[List[int]]) -> None:
        n = len(matrix)
        for r in range(n):
            for c in range(r + 1, n):
                # Transpose swaps rows and columns.
                matrix[r][c], matrix[c][r] = matrix[c][r], matrix[r][c]
        for row in matrix:
            row.reverse()
```
- **Time:** O(n²) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n²) | O(n²) |
| Better | O(n²) | O(1) |
| Optimal | O(n²) | O(1) |

## Edge Cases & Pitfalls
- Single-cell matrices remain unchanged.
- Do not return a new matrix; LeetCode expects in-place mutation.

## Related
- Transpose Matrix
- Spiral Matrix
