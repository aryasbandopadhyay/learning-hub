# 10. Transpose Matrix

- **Difficulty:** Easy
- **Pattern:** Matrix
- **Asked at:** Amazon, Microsoft, Google

## Problem
Given an `m x n` matrix, return its transpose, where rows become columns. Constraints: `1 <= m,n <= 1000`, `m*n <= 10^5`.

## Examples
```text
Input: matrix = [[1,2,3],[4,5,6]]
Output: [[1,4],[2,5],[3,6]]
Explanation: Element at (r,c) moves to (c,r).
```

## Understanding & Intuition
Transposition swaps row and column coordinates. Rectangular matrices change shape from `m x n` to `n x m`. Unlike Rotate Image, a new matrix is expected and needed for non-square input.

## Approach 1 — Naive / Brute Force
**Idea:** Create the transposed matrix with explicit nested loops.
```python
from typing import List

class Solution:
    def transpose(self, matrix: List[List[int]]) -> List[List[int]]:
        m, n = len(matrix), len(matrix[0])
        ans = [[0] * m for _ in range(n)]
        for r in range(m):
            for c in range(n):
                ans[c][r] = matrix[r][c]
        return ans
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Build each output row by reading one original column.
```python
from typing import List

class Solution:
    def transpose(self, matrix: List[List[int]]) -> List[List[int]]:
        m, n = len(matrix), len(matrix[0])
        ans = []
        for c in range(n):
            row = []
            for r in range(m):
                row.append(matrix[r][c])
            ans.append(row)
        return ans
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** Use `zip(*matrix)` to stream columns as tuples, then convert each tuple to a list.
```python
from typing import List

class Solution:
    def transpose(self, matrix: List[List[int]]) -> List[List[int]]:
        return [list(col) for col in zip(*matrix)]
```
- **Time:** O(mn) — **Space:** O(mn)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn) | O(mn) |
| Better | O(mn) | O(mn) |
| Optimal | O(mn) | O(mn) |

## Edge Cases & Pitfalls
- Rectangular input changes dimensions.
- `zip(*matrix)` returns tuples unless converted to lists.

## Related
- Rotate Image
- Diagonal Traverse
