# 08. Sparse Matrix Multiplication

- **Difficulty:** Medium
- **Pattern:** matrix simulation & transformation
- **Asked at:** Meta, Google, LinkedIn

## Problem
Given matrices `mat1` of size `m x k` and `mat2` of size `k x n`, return their matrix product as an `m x n` matrix. Many entries may be zero.

**Input**
- `mat1`: a `list[list[int]]`; the `mat1` value described above.
- `mat2`: a `list[list[int]]`; the `mat2` value described above.

**Output**
- A `list[list[int]]`. Return their matrix product as an `m x n` matrix. This judge compares the sequence exactly: return the product matrix with rows and columns in standard matrix order.

## Constraints
- `1 <= m,k,n <= 100`, values fit in signed 32-bit integers.

## Examples
```text
Input: mat1 = [[1,0,0],[-1,0,3]], mat2 = [[7,0,0],[0,0,0],[0,0,1]]
Output: [[7,0,0],[-7,0,3]]
Explanation: Zero entries do not affect the dot products. The output is written in the required deterministic order.
```

## Understanding & Intuition
The direct formula computes every dot product with every shared column. Sparse matrices allow us to skip terms where either multiplicand is zero. Compressing nonzero positions avoids most wasted work.

## Approach 1 — Naive / Brute Force
**Idea:** Compute each output cell by the full dot product formula.
```python
class Solution:
    def multiply(self, mat1: list[list[int]], mat2: list[list[int]]) -> list[list[int]]:
        m, common, n = len(mat1), len(mat1[0]), len(mat2[0])
        ans = [[0] * n for _ in range(m)]
        for i in range(m):
            for j in range(n):
                total = 0
                for t in range(common):
                    total += mat1[i][t] * mat2[t][j]
                ans[i][j] = total
        return ans
```
- **Time:** O(m*k*n) — **Space:** O(m*n)

## Approach 2 — Better
**Idea:** Skip a whole contribution row whenever `mat1[i][t]` is zero.
```python
class Solution:
    def multiply(self, mat1: list[list[int]], mat2: list[list[int]]) -> list[list[int]]:
        m, common, n = len(mat1), len(mat1[0]), len(mat2[0])
        ans = [[0] * n for _ in range(m)]
        for i in range(m):
            for t in range(common):
                if mat1[i][t] == 0:
                    continue
                for j in range(n):
                    if mat2[t][j] != 0:
                        ans[i][j] += mat1[i][t] * mat2[t][j]
        return ans
```
- **Time:** O(m*k*n) — **Space:** O(m*n)

## Approach 3 — Optimal
**Idea:** Store only nonzero entries of each row and multiply matching sparse rows.
```python
class Solution:
    def multiply(self, mat1: list[list[int]], mat2: list[list[int]]) -> list[list[int]]:
        m, common, n = len(mat1), len(mat1[0]), len(mat2[0])
        rows1 = [[(t, mat1[i][t]) for t in range(common) if mat1[i][t] != 0] for i in range(m)]
        rows2 = [[(j, mat2[t][j]) for j in range(n) if mat2[t][j] != 0] for t in range(common)]
        ans = [[0] * n for _ in range(m)]
        for i, row in enumerate(rows1):
            for t, a in row:
                for j, b in rows2[t]:
                    ans[i][j] += a * b
        return ans
```
- **Time:** O(nnz-products + m*k + k*n) — **Space:** O(m*n + nnz)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(m*k*n) | O(m*n) |
| Better | O(m*k*n) | O(m*n) |
| Optimal | O(nnz-products + m*k + k*n) | O(m*n + nnz) |

## Edge Cases & Pitfalls
- Preserve signs when multiplying negative values.
- Result dimensions are `len(mat1) x len(mat2[0])`.
- Skipping zero entries must not skip nonzero contributions in the same row.

## Related
- Dot Product of Two Sparse Vectors
- Matrix Multiplication
