# 03. Rotate Image

- **Difficulty:** Medium
- **Pattern:** Matrix
- **Asked at:** Amazon, Microsoft, Google, Meta

## Problem
You are given an `n x n` matrix representing an image.

Rotate it 90 degrees clockwise in place. The value at `(r, c)` moves to `(c, n - 1 - r)`. Return the final matrix for the judge.

**Input**
- `matrix`: a square 2-D list of integers.

**Output**
- The rotated matrix. **This judge compares exactly**, so every cell must match.

## Constraints
- `n == matrix.length == matrix[r].length`
- `1 <= n <= 20`
- `-1000 <= matrix[r][c] <= 1000`.

## Examples
```text
Input: matrix = [[1,2,3],[4,5,6],[7,8,9]]
Output: [[7,4,1],[8,5,2],[9,6,3]]
Explanation: The first column `[1,4,7]` becomes the first row reversed as `[7,4,1]`. Applying that movement to every cell gives the output.
```

## Understanding & Intuition
A clockwise rotation remaps coordinates. Because the output must be in the same matrix, either copy the original values or perform safe swaps. The transpose-then-reverse observation gives the cleanest in-place method.

## Approach 1 — Naive / Brute Force
**Idea:** Build a rotated copy, then copy it back into the original matrix.
```python
from typing import List

class Solution:
    def rotate(self, matrix: List[List[int]]) -> None:
        n = len(matrix)
        rotated = [[0] * n for _ in range(n)]
        for r in range(n):
            for c in range(n):
                rotated[c][n - 1 - r] = matrix[r][c]

        # Mutate the input as required by LeetCode.
        for r in range(n):
            matrix[r][:] = rotated[r]
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 2 — Better
**Idea:** Rotate one layer at a time using four-way swaps.
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
                matrix[first][i] = matrix[last - offset][first]
                matrix[last - offset][first] = matrix[last][last - offset]
                matrix[last][last - offset] = matrix[i][last]
                matrix[i][last] = top
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Transpose across the main diagonal, then reverse each row.
```python
from typing import List

class Solution:
    def rotate(self, matrix: List[List[int]]) -> None:
        n = len(matrix)
        for r in range(n):
            for c in range(r + 1, n):
                matrix[r][c], matrix[c][r] = matrix[c][r], matrix[r][c]

        # Reversing rows after transpose gives clockwise rotation.
        for row in matrix:
            row.reverse()
```
- **Time:** O(n^2) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n^2) |
| Better | O(n^2) | O(1) |
| Optimal | O(n^2) | O(1) |

## Edge Cases & Pitfalls
- The matrix is square; rectangular transpose logic is not enough.
- Odd `n` leaves the center cell unchanged.

## Related
- Transpose Matrix
- Set Matrix Zeroes
