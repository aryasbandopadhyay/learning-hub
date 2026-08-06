# 06. Sort the Matrix Diagonally

- **Difficulty:** Medium
- **Pattern:** matrix simulation & transformation
- **Asked at:** Amazon, Google, Microsoft

## Problem
Given an `m x n` matrix `mat`, sort every diagonal running from top-left to bottom-right in ascending order and return the transformed matrix.

Constraints: `1 <= m, n <= 100`, `1 <= mat[i][j] <= 100`.

## Examples
```text
Input: mat = [[3,3,1,1],[2,2,1,2],[1,1,1,2]]
Output: [[1,1,1,1],[1,2,2,2],[1,2,3,3]]
Explanation: Each top-left to bottom-right diagonal is sorted independently.
```

## Understanding & Intuition
Cells on the same diagonal share the same `r - c` value. Sorting one diagonal never affects another diagonal's membership. We can collect, sort, and write back each diagonal.

## Approach 1 — Naive / Brute Force
**Idea:** For every diagonal start, collect values, sort them with insertion sort, and write them back.
```python
class Solution:
    def diagonalSort(self, mat: list[list[int]]) -> list[list[int]]:
        ans = [row[:] for row in mat]
        m, n = len(ans), len(ans[0])
        starts = [(r, 0) for r in range(m)] + [(0, c) for c in range(1, n)]
        for sr, sc in starts:
            vals = []
            r, c = sr, sc
            while r < m and c < n:
                vals.append(ans[r][c]); r += 1; c += 1
            for i in range(1, len(vals)):
                x = vals[i]; j = i - 1
                while j >= 0 and vals[j] > x:
                    vals[j + 1] = vals[j]; j -= 1
                vals[j + 1] = x
            r, c, idx = sr, sc, 0
            while r < m and c < n:
                ans[r][c] = vals[idx]; idx += 1; r += 1; c += 1
        return ans
```
- **Time:** O(m*n*min(m,n)) — **Space:** O(m*n)

## Approach 2 — Better
**Idea:** Group values by diagonal key, sort each group descending, and pop the smallest while scanning.
```python
class Solution:
    def diagonalSort(self, mat: list[list[int]]) -> list[list[int]]:
        diagonals = {}
        for r in range(len(mat)):
            for c in range(len(mat[0])):
                diagonals.setdefault(r - c, []).append(mat[r][c])
        for vals in diagonals.values():
            vals.sort(reverse=True)
        ans = [row[:] for row in mat]
        for r in range(len(mat)):
            for c in range(len(mat[0])):
                ans[r][c] = diagonals[r - c].pop()
        return ans
```
- **Time:** O(m*n*log(min(m,n))) — **Space:** O(m*n)

## Approach 3 — Optimal
**Idea:** Values are bounded, so counting sort each diagonal in linear time.
```python
class Solution:
    def diagonalSort(self, mat: list[list[int]]) -> list[list[int]]:
        ans = [row[:] for row in mat]
        m, n = len(ans), len(ans[0])
        starts = [(r, 0) for r in range(m)] + [(0, c) for c in range(1, n)]
        for sr, sc in starts:
            count = [0] * 101
            r, c = sr, sc
            while r < m and c < n:
                count[ans[r][c]] += 1; r += 1; c += 1
            value = 1
            r, c = sr, sc
            while r < m and c < n:
                while count[value] == 0:
                    value += 1
                ans[r][c] = value
                count[value] -= 1
                r += 1; c += 1
        return ans
```
- **Time:** O(m*n + 100*(m+n)) — **Space:** O(m*n + 100)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(m*n*min(m,n)) | O(m*n) |
| Better | O(m*n*log(min(m,n))) | O(m*n) |
| Optimal | O(m*n + 100*(m+n)) | O(m*n + 100) |

## Edge Cases & Pitfalls
- Start each diagonal from the first row or first column exactly once.
- Preserve dimensions and return a new matrix.
- Counting sort relies on the stated value range.

## Related
- Diagonal Traverse II
- Sort Colors
