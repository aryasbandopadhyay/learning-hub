# 01. Spiral Matrix II

- **Difficulty:** Medium
- **Pattern:** matrix simulation & transformation
- **Asked at:** Google, Amazon, Meta

## Problem
Given an integer `n`, return an `n x n` matrix filled with values from `1` to `n^2` in clockwise spiral order.

Constraints: `1 <= n <= 50`.

## Examples
```text
Input: n = 3
Output: [[1,2,3],[8,9,4],[7,6,5]]
Explanation: The numbers are written while walking clockwise around shrinking boundaries.
```

## Understanding & Intuition
The matrix is built by repeatedly walking right, down, left, and up. Once a border is filled, the next numbers belong to the inner submatrix. Tracking either visited cells or active boundaries prevents overwriting.

## Approach 1 — Naive / Brute Force
**Idea:** Simulate every step and turn whenever the next cell would leave the matrix or hit an already filled cell.
```python
class Solution:
    def generateMatrix(self, n: int) -> list[list[int]]:
        ans = [[0] * n for _ in range(n)]
        dirs = [(0, 1), (1, 0), (0, -1), (-1, 0)]
        r = c = d = 0
        for val in range(1, n * n + 1):
            ans[r][c] = val
            nr, nc = r + dirs[d][0], c + dirs[d][1]
            if nr < 0 or nr >= n or nc < 0 or nc >= n or ans[nr][nc] != 0:
                d = (d + 1) % 4
                nr, nc = r + dirs[d][0], c + dirs[d][1]
            r, c = nr, nc
        return ans
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 2 — Better
**Idea:** Fill one square ring at a time with four directional loops.
```python
class Solution:
    def generateMatrix(self, n: int) -> list[list[int]]:
        ans = [[0] * n for _ in range(n)]
        val = 1
        for layer in range((n + 1) // 2):
            top = left = layer
            bottom = right = n - 1 - layer
            for c in range(left, right + 1):
                ans[top][c] = val; val += 1
            for r in range(top + 1, bottom + 1):
                ans[r][right] = val; val += 1
            if top < bottom:
                for c in range(right - 1, left - 1, -1):
                    ans[bottom][c] = val; val += 1
            if left < right:
                for r in range(bottom - 1, top, -1):
                    ans[r][left] = val; val += 1
        return ans
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Maintain shrinking boundaries and fill each side while the boundaries remain valid.
```python
class Solution:
    def generateMatrix(self, n: int) -> list[list[int]]:
        ans = [[0] * n for _ in range(n)]
        top, bottom, left, right = 0, n - 1, 0, n - 1
        val = 1
        while top <= bottom and left <= right:
            for c in range(left, right + 1):
                ans[top][c] = val; val += 1
            top += 1
            for r in range(top, bottom + 1):
                ans[r][right] = val; val += 1
            right -= 1
            if top <= bottom:
                for c in range(right, left - 1, -1):
                    ans[bottom][c] = val; val += 1
                bottom -= 1
            if left <= right:
                for r in range(bottom, top - 1, -1):
                    ans[r][left] = val; val += 1
                left += 1
        return ans
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n^2) |
| Better | O(n^2) | O(n^2) |
| Optimal | O(n^2) | O(n^2) |

## Edge Cases & Pitfalls
- `n = 1` should return `[[1]]`.
- Turn before moving into an already filled cell.
- Avoid double-filling the center of odd-sized matrices.

## Related
- Spiral Matrix
- Spiral Matrix III
