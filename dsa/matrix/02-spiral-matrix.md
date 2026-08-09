# 02. Spiral Matrix

- **Difficulty:** Medium
- **Pattern:** Matrix
- **Asked at:** Amazon, Microsoft, Google, Apple

## Problem
Given an `m x n` matrix, return all elements in spiral order.

Start at the top-left, move right across the top row, down the right column, left across the bottom row, and up the left column. Repeat while shrinking inward until every cell is visited once.

**Input**
- `matrix`: a 2-D list of integers.

**Output**
- A list of values. **This judge compares exactly**, so values must be in spiral order.

## Constraints
- `m == matrix.length`
- `n == matrix[r].length`
- `1 <= m, n <= 10`
- `-100 <= matrix[r][c] <= 100`.

## Examples
```text
Input: matrix = [[1,2,3],[4,5,6],[7,8,9]]
Output: [1,2,3,6,9,8,7,4,5]
Explanation: The outer ring is read as `1,2,3,6,9,8,7,4`, then the remaining center value `5` is appended.
```

## Understanding & Intuition
A spiral repeatedly consumes the current outer rectangle. After each side is read, its boundary moves inward. Care is needed to avoid duplicating a row or column when only one remains.

## Approach 1 — Naive / Brute Force
**Idea:** Simulate walking cell by cell with a visited grid and turn right when blocked.
```python
from typing import List

class Solution:
    def spiralOrder(self, matrix: List[List[int]]) -> List[int]:
        m, n = len(matrix), len(matrix[0])
        seen = [[False] * n for _ in range(m)]
        dirs = [(0, 1), (1, 0), (0, -1), (-1, 0)]
        r = c = d = 0
        ans = []

        for _ in range(m * n):
            ans.append(matrix[r][c])
            seen[r][c] = True
            nr, nc = r + dirs[d][0], c + dirs[d][1]
            if not (0 <= nr < m and 0 <= nc < n and not seen[nr][nc]):
                d = (d + 1) % 4
                nr, nc = r + dirs[d][0], c + dirs[d][1]
            r, c = nr, nc
        return ans
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Peel layers using four boundaries and append sides only when the boundary is still valid.
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
- **Time:** O(mn) — **Space:** O(1) auxiliary

## Approach 3 — Optimal
**Idea:** Walk one element at a time while shrinking the boundary whenever a side is completed.
```python
from typing import List

class Solution:
    def spiralOrder(self, matrix: List[List[int]]) -> List[int]:
        m, n = len(matrix), len(matrix[0])
        top, bottom, left, right = 0, m - 1, 0, n - 1
        r = c = direction = 0
        dirs = [(0, 1), (1, 0), (0, -1), (-1, 0)]
        ans = []

        for _ in range(m * n):
            ans.append(matrix[r][c])
            if direction == 0 and c == right:
                direction, top = 1, top + 1
            elif direction == 1 and r == bottom:
                direction, right = 2, right - 1
            elif direction == 2 and c == left:
                direction, bottom = 3, bottom - 1
            elif direction == 3 and r == top:
                direction, left = 0, left + 1

            dr, dc = dirs[direction]
            r += dr
            c += dc
        return ans
```
- **Time:** O(mn) — **Space:** O(1) auxiliary

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn) | O(mn) |
| Better | O(mn) | O(1) |
| Optimal | O(mn) | O(1) |

## Edge Cases & Pitfalls
- Single row and single column matrices can duplicate elements if guards are missing.
- Return an empty list only if input constraints are relaxed to allow an empty matrix.

## Related
- Spiral Matrix II
- Diagonal Traverse
