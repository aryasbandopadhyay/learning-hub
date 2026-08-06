# 10. Shift 2D Grid

- **Difficulty:** Medium
- **Pattern:** matrix simulation & transformation
- **Asked at:** Amazon, Google, Microsoft

## Problem
Given an `m x n` grid and an integer `k`, shift the grid to the right `k` times. In one shift, each cell moves to the next column, the last column moves to the first column of the next row, and the bottom-right cell moves to the top-left. Return the shifted grid.

Constraints: `1 <= m, n <= 50`, `0 <= k <= 10^9`.

## Examples
```text
Input: grid = [[1,2,3],[4,5,6],[7,8,9]], k = 1
Output: [[9,1,2],[3,4,5],[6,7,8]]
Explanation: Every value moves one position forward in row-major order.
```

## Understanding & Intuition
A shift is just a rotation of the matrix's row-major flattening. The only challenge is mapping between a linear index and `(row, column)`. Reducing `k` modulo `m*n` handles large shifts.

## Approach 1 — Naive / Brute Force
**Idea:** Apply the one-step shift `k % (m*n)` times.
```python
class Solution:
    def shiftGrid(self, grid: list[list[int]], k: int) -> list[list[int]]:
        m, n = len(grid), len(grid[0])
        ans = [row[:] for row in grid]
        for _ in range(k % (m * n)):
            nxt = [[0] * n for _ in range(m)]
            for r in range(m):
                for c in range(n):
                    idx = (r * n + c + 1) % (m * n)
                    nxt[idx // n][idx % n] = ans[r][c]
            ans = nxt
        return ans
```
- **Time:** O(m*n*min(k,m*n)) — **Space:** O(m*n)

## Approach 2 — Better
**Idea:** Flatten the grid, rotate the list, then rebuild rows.
```python
class Solution:
    def shiftGrid(self, grid: list[list[int]], k: int) -> list[list[int]]:
        m, n = len(grid), len(grid[0])
        flat = []
        for row in grid:
            flat.extend(row)
        k %= len(flat)
        if k:
            flat = flat[-k:] + flat[:-k]
        return [flat[i * n:(i + 1) * n] for i in range(m)]
```
- **Time:** O(m*n) — **Space:** O(m*n)

## Approach 3 — Optimal
**Idea:** Place each value directly at its shifted linear index.
```python
class Solution:
    def shiftGrid(self, grid: list[list[int]], k: int) -> list[list[int]]:
        m, n = len(grid), len(grid[0])
        total = m * n
        k %= total
        ans = [[0] * n for _ in range(m)]
        for r in range(m):
            for c in range(n):
                idx = (r * n + c + k) % total
                ans[idx // n][idx % n] = grid[r][c]
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
- `k = 0` or `k` divisible by `m*n` returns the original layout.
- Use modulo before repeated shifting.
- Preserve row lengths when reconstructing the matrix.

## Related
- Rotate the Grid
- Reshape the Matrix
