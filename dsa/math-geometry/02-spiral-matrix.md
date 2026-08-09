# 02. Spiral Matrix

- **Difficulty:** Medium
- **Pattern:** Math & Geometry
- **Asked at:** Amazon, Microsoft, Google, Meta

## Problem
Given an `m x n` matrix, return all of its elements in **clockwise spiral order**.

Start at the top-left corner. Traverse the current top row left to right, the current right column top to bottom, the current bottom row right to left, and the current left column bottom to top. After each outer layer is completed, move inward and repeat until every element has been visited exactly once.

**Input**
- `matrix`: a 2-D list of integers with `m` rows and `n` columns.

**Output**
- A list containing every matrix value in the exact clockwise spiral traversal order.

## Constraints
- `m == matrix.length`
- `n == matrix[i].length`
- `1 <= m, n <= 10`
- `-100 <= matrix[i][j] <= 100`

## Examples
```text
Input: matrix = [[1,2,3],[4,5,6],[7,8,9]]
Output: [1,2,3,6,9,8,7,4,5]
Explanation: Visit the outer layer as `1,2,3,6,9,8,7,4`, then move into the remaining center cell `5`. Concatenating those visits gives the required order.
```

```text
Input: matrix = [[1,2,3],[4,5,6]]
Output: [1,2,3,6,5,4]
Explanation: Traverse the top row `1,2,3`, then the right edge `6`, then the bottom row in reverse as `5,4`.
```

## Understanding & Intuition
A spiral is a repeated traversal of the current outer rectangle. After each side, the corresponding boundary moves inward. Stop when boundaries cross.

## Approach 1 — Naive / Brute Force
**Idea:** Simulate movement with a visited grid and turn whenever the next cell is invalid.
```python
from typing import List

class Solution:
    def spiralOrder(self, matrix: List[List[int]]) -> List[int]:
        rows, cols = len(matrix), len(matrix[0])
        seen = [[False] * cols for _ in range(rows)]
        dirs = [(0, 1), (1, 0), (0, -1), (-1, 0)]
        r = c = d = 0
        ans = []
        for _ in range(rows * cols):
            ans.append(matrix[r][c])
            seen[r][c] = True
            nr, nc = r + dirs[d][0], c + dirs[d][1]
            if not (0 <= nr < rows and 0 <= nc < cols and not seen[nr][nc]):
                d = (d + 1) % 4
                nr, nc = r + dirs[d][0], c + dirs[d][1]
            r, c = nr, nc
        return ans
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Peel the matrix by bounds and guard single remaining rows or columns.
```python
from typing import List

class Solution:
    def spiralOrder(self, matrix: List[List[int]]) -> List[int]:
        top, bottom = 0, len(matrix) - 1
        left, right = 0, len(matrix[0]) - 1
        ans = []
        while top <= bottom and left <= right:
            for c in range(left, right + 1):
                ans.append(matrix[top][c])
            for r in range(top + 1, bottom + 1):
                ans.append(matrix[r][right])
            if top < bottom:
                for c in range(right - 1, left - 1, -1):
                    ans.append(matrix[bottom][c])
            if left < right:
                for r in range(bottom - 1, top, -1):
                    ans.append(matrix[r][left])
            top, bottom, left, right = top + 1, bottom - 1, left + 1, right - 1
        return ans
```
- **Time:** O(mn) — **Space:** O(1) extra

## Approach 3 — Optimal
**Idea:** Use four boundaries and shrink immediately after each directional pass.
```python
from typing import List

class Solution:
    def spiralOrder(self, matrix: List[List[int]]) -> List[int]:
        top, bottom = 0, len(matrix) - 1
        left, right = 0, len(matrix[0]) - 1
        ans = []
        while left <= right and top <= bottom:
            for c in range(left, right + 1):
                ans.append(matrix[top][c])
            top += 1
            for r in range(top, bottom + 1):
                ans.append(matrix[r][right])
            right -= 1
            if top <= bottom:
                for c in range(right, left - 1, -1):
                    ans.append(matrix[bottom][c])
                bottom -= 1
            if left <= right:
                for r in range(bottom, top - 1, -1):
                    ans.append(matrix[r][left])
                left += 1
        return ans
```
- **Time:** O(mn) — **Space:** O(1) extra

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn) | O(mn) |
| Better | O(mn) | O(1) extra |
| Optimal | O(mn) | O(1) extra |

## Edge Cases & Pitfalls
- Single row or single column must not be duplicated.
- Empty matrix is not present under constraints.

## Related
- Rotate Image
- Spiral Matrix II
