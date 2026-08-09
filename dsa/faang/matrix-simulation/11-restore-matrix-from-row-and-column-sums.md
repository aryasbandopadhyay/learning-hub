# 11. Restore Matrix From Row and Column Sums

- **Difficulty:** Medium
- **Pattern:** matrix simulation & transformation
- **Asked at:** Google, Meta, Amazon

## Problem
Given nonnegative integer arrays `rowSum` and `colSum` with equal total sum, construct and return a nonnegative matrix whose row sums and column sums match them. To make the answer deterministic, return the canonical greedy matrix that fills cells from top-left to bottom-right with `min(remaining row, remaining column)`.

**Input**
- `rowSum`: a `list[int]`; required row sums.
- `colSum`: a `list[int]`; required column sums.

**Output**
- A `list[list[int]]`. Return a nonnegative matrix whose row sums and column sums match them. return the canonical greedy matrix that fills cells from top-left to bottom-right with `min(remaining row, remaining column)`. This judge compares the sequence exactly: return the canonical greedy matrix filled from top-left toward bottom-right.

## Constraints
- `1 <= len(rowSum), len(colSum) <= 500`, sums fit in a signed 32-bit integer.

## Examples
```text
Input: rowSum = [3,8], colSum = [4,7]
Output: [[3,0],[1,7]]
Explanation: Greedy top-left filling first places 3, then 1, then 7. The output is written in the required deterministic order.
```

## Understanding & Intuition
The total supply in rows equals the total demand in columns. Filling a cell with as much as possible can only finish its row, its column, or both. The canonical rule removes ambiguity among many valid matrices.

## Approach 1 — Naive / Brute Force
**Idea:** Move one unit at a time into the current top-left feasible cell.
```python
class Solution:
    def restoreMatrix(self, rowSum: list[int], colSum: list[int]) -> list[list[int]]:
        rows = rowSum[:]
        cols = colSum[:]
        m, n = len(rows), len(cols)
        ans = [[0] * n for _ in range(m)]
        i = j = 0
        while i < m and j < n:
            if rows[i] == 0:
                i += 1
            elif cols[j] == 0:
                j += 1
            else:
                ans[i][j] += 1
                rows[i] -= 1
                cols[j] -= 1
        return ans
```
- **Time:** O(m+n+S) — **Space:** O(m*n)

## Approach 2 — Better
**Idea:** Scan cells row-major and assign the maximum feasible amount to each cell.
```python
class Solution:
    def restoreMatrix(self, rowSum: list[int], colSum: list[int]) -> list[list[int]]:
        rows = rowSum[:]
        cols = colSum[:]
        m, n = len(rows), len(cols)
        ans = [[0] * n for _ in range(m)]
        for i in range(m):
            for j in range(n):
                x = min(rows[i], cols[j])
                ans[i][j] = x
                rows[i] -= x
                cols[j] -= x
        return ans
```
- **Time:** O(m*n) — **Space:** O(m*n)

## Approach 3 — Optimal
**Idea:** Use two pointers to skip exhausted rows and columns while assigning the same canonical greedy values.
```python
class Solution:
    def restoreMatrix(self, rowSum: list[int], colSum: list[int]) -> list[list[int]]:
        rows = rowSum[:]
        cols = colSum[:]
        m, n = len(rows), len(cols)
        ans = [[0] * n for _ in range(m)]
        i = j = 0
        while i < m and j < n:
            x = min(rows[i], cols[j])
            ans[i][j] = x
            rows[i] -= x
            cols[j] -= x
            if rows[i] == 0:
                i += 1
            if j < n and cols[j] == 0:
                j += 1
        return ans
```
- **Time:** O(m+n) — **Space:** O(m*n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(m+n+S) | O(m*n) |
| Better | O(m*n) | O(m*n) |
| Optimal | O(m+n) | O(m*n) |

## Edge Cases & Pitfalls
- Copy `rowSum` and `colSum` before mutating remaining capacities.
- Determinism matters because many valid matrices may exist.
- Zero rows or columns should be skipped naturally.

## Related
- Reconstruct a 2-Row Binary Matrix
- Maximum Matrix Sum
