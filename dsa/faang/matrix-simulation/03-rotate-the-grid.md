# 03. Rotate the Grid

- **Difficulty:** Medium
- **Pattern:** matrix simulation & transformation
- **Asked at:** Meta, Google, Amazon

## Problem
Given an `m x n` matrix `grid` where both dimensions are even or at least `2`, rotate each rectangular layer counterclockwise by `k` positions and return the resulting matrix.

Constraints: `2 <= m, n <= 50`, `m` and `n` are even, `0 <= k <= 10^9`.

## Examples
```text
Input: grid = [[40,10],[30,20]], k = 1
Output: [[10,20],[40,30]]
Explanation: The single layer moves counterclockwise by one position.
```

## Understanding & Intuition
Every ring of the matrix is independent. If a layer is read in clockwise perimeter order, a counterclockwise rotation by `k` is a left shift of that list. Writing shifted values back to the same coordinates completes the transform.

## Approach 1 — Naive / Brute Force
**Idea:** For each layer, perform one-position rotations `k % perimeter` times.
```python
class Solution:
    def rotateGrid(self, grid: list[list[int]], k: int) -> list[list[int]]:
        ans = [row[:] for row in grid]
        m, n = len(ans), len(ans[0])
        for layer in range(min(m, n) // 2):
            top, left, bottom, right = layer, layer, m - 1 - layer, n - 1 - layer
            coords = [(top, c) for c in range(left, right + 1)]
            coords += [(r, right) for r in range(top + 1, bottom)]
            coords += [(bottom, c) for c in range(right, left - 1, -1)]
            coords += [(r, left) for r in range(bottom - 1, top, -1)]
            for _ in range(k % len(coords)):
                first = ans[coords[0][0]][coords[0][1]]
                for i in range(len(coords) - 1):
                    r1, c1 = coords[i]; r2, c2 = coords[i + 1]
                    ans[r1][c1] = ans[r2][c2]
                r, c = coords[-1]; ans[r][c] = first
        return ans
```
- **Time:** O(m*n*min(k,m*n)) — **Space:** O(m*n)

## Approach 2 — Better
**Idea:** Extract each ring, left-rotate its values once by `k`, and write them back.
```python
class Solution:
    def rotateGrid(self, grid: list[list[int]], k: int) -> list[list[int]]:
        ans = [row[:] for row in grid]
        m, n = len(grid), len(grid[0])
        for layer in range(min(m, n) // 2):
            top, left, bottom, right = layer, layer, m - 1 - layer, n - 1 - layer
            coords = [(top, c) for c in range(left, right + 1)]
            coords += [(r, right) for r in range(top + 1, bottom)]
            coords += [(bottom, c) for c in range(right, left - 1, -1)]
            coords += [(r, left) for r in range(bottom - 1, top, -1)]
            vals = [grid[r][c] for r, c in coords]
            shift = k % len(vals)
            vals = vals[shift:] + vals[:shift]
            for (r, c), val in zip(coords, vals):
                ans[r][c] = val
        return ans
```
- **Time:** O(m*n) — **Space:** O(m*n)

## Approach 3 — Optimal
**Idea:** For each perimeter coordinate, copy directly from the coordinate `k` steps ahead in the same ring.
```python
class Solution:
    def rotateGrid(self, grid: list[list[int]], k: int) -> list[list[int]]:
        m, n = len(grid), len(grid[0])
        ans = [row[:] for row in grid]
        for layer in range(min(m, n) // 2):
            top, left, bottom, right = layer, layer, m - 1 - layer, n - 1 - layer
            coords = [(top, c) for c in range(left, right + 1)]
            coords += [(r, right) for r in range(top + 1, bottom)]
            coords += [(bottom, c) for c in range(right, left - 1, -1)]
            coords += [(r, left) for r in range(bottom - 1, top, -1)]
            shift = k % len(coords)
            for i, (r, c) in enumerate(coords):
                sr, sc = coords[(i + shift) % len(coords)]
                ans[r][c] = grid[sr][sc]
        return ans
```
- **Time:** O(m*n) — **Space:** O(m*n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(m*n*min(k,m*n)) | O(m*n) |
| Better | O(m*n) | O(m*n) |
| Optimal | O(m*n) | O(m*n) |

## Edge Cases & Pitfalls
- Reduce `k` modulo each layer perimeter.
- Read from the original grid when assigning rotated values.
- Do not duplicate corner coordinates while collecting a ring.

## Related
- Rotate Image
- Shift 2D Grid
