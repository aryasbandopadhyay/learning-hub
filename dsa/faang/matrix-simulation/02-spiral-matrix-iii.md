# 02. Spiral Matrix III

- **Difficulty:** Medium
- **Pattern:** matrix simulation & transformation
- **Asked at:** Google, Amazon, Microsoft

## Problem
Given grid dimensions `rows` and `cols` and a starting cell `(rStart, cStart)`, return the coordinates of all cells in the grid in the order visited by an infinite clockwise spiral walk. Coordinates outside the grid may be walked through, but only in-grid cells are returned.

**Input**
- `rows`: a `int`; the number of grid rows.
- `cols`: a `int`; the number of grid columns.
- `rStart`: a `int`; the starting row.
- `cStart`: a `int`; the starting column.

**Output**
- A `list[list[int]]`. Return the coordinates of all cells in the grid in the order visited by an infinite clockwise spiral walk. returned. This judge compares the sequence exactly: return valid grid coordinates in the exact order the spiral visits them.

## Constraints
- `1 <= rows, cols <= 100`, `0 <= rStart < rows`, `0 <= cStart < cols`.

## Examples
```text
Input: rows = 1, cols = 4, rStart = 0, cStart = 0
Output: [[0,0],[0,1],[0,2],[0,3]]
Explanation: The spiral immediately visits the only row from left to right. The output is written in the required deterministic order.
```

## Understanding & Intuition
The walk uses directions right, down, left, up, and its leg length increases after every two legs. Because the spiral may leave the grid, we keep walking until every valid coordinate has been recorded. The output order is unique.

## Approach 1 — Naive / Brute Force
**Idea:** Step one cell at a time, checking bounds after each move.
```python
class Solution:
    def spiralMatrixIII(self, rows: int, cols: int, rStart: int, cStart: int) -> list[list[int]]:
        total = rows * cols
        ans = [[rStart, cStart]]
        r, c = rStart, cStart
        dirs = [(0, 1), (1, 0), (0, -1), (-1, 0)]
        step_len = 1; d = 0
        while len(ans) < total:
            for _ in range(2):
                dr, dc = dirs[d]
                for _ in range(step_len):
                    r += dr; c += dc
                    if 0 <= r < rows and 0 <= c < cols:
                        ans.append([r, c])
                        if len(ans) == total:
                            return ans
                d = (d + 1) % 4
            step_len += 1
        return ans
```
- **Time:** O((rows+cols)^2) — **Space:** O(rows*cols)

## Approach 2 — Better
**Idea:** Process complete legs and append only valid cells encountered on each leg.
```python
class Solution:
    def spiralMatrixIII(self, rows: int, cols: int, rStart: int, cStart: int) -> list[list[int]]:
        ans = []
        r, c = rStart, cStart
        dirs = [(0, 1), (1, 0), (0, -1), (-1, 0)]
        length = 1
        while len(ans) < rows * cols:
            for d, (dr, dc) in enumerate(dirs):
                for _ in range(length):
                    if 0 <= r < rows and 0 <= c < cols:
                        ans.append([r, c])
                    r += dr; c += dc
                if d % 2 == 1:
                    length += 1
                if len(ans) == rows * cols:
                    return ans
        return ans
```
- **Time:** O((rows+cols)^2) — **Space:** O(rows*cols)

## Approach 3 — Optimal
**Idea:** Generate the same spiral by radius layers, stopping as soon as all cells are collected.
```python
class Solution:
    def spiralMatrixIII(self, rows: int, cols: int, rStart: int, cStart: int) -> list[list[int]]:
        ans = [[rStart, cStart]]
        r, c = rStart, cStart
        total = rows * cols
        step = 1
        while len(ans) < total:
            for dr, dc, length in ((0, 1, step), (1, 0, step), (0, -1, step + 1), (-1, 0, step + 1)):
                for _ in range(length):
                    r += dr; c += dc
                    if 0 <= r < rows and 0 <= c < cols:
                        ans.append([r, c])
                        if len(ans) == total:
                            return ans
            step += 2
        return ans
```
- **Time:** O((rows+cols)^2) — **Space:** O(rows*cols)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((rows+cols)^2) | O(rows*cols) |
| Better | O((rows+cols)^2) | O(rows*cols) |
| Optimal | O((rows+cols)^2) | O(rows*cols) |

## Edge Cases & Pitfalls
- Start cell must be returned first.
- Do not stop just because the current coordinate leaves the grid.
- Single-row and single-column grids still follow the same spiral rules.

## Related
- Spiral Matrix II
- Walking Robot Simulation
