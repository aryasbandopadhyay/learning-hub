# 05. Range Sum Query 2D Batch

- **Difficulty:** Medium
- **Pattern:** matrix simulation & transformation
- **Asked at:** Google, Meta, Amazon

## Problem
Given an integer matrix `matrix` and a list of queries `queries`, where each query is `[r1, c1, r2, c2]`, return a list containing the sum of every submatrix from top-left `(r1,c1)` to bottom-right `(r2,c2)` inclusive.

**Input**
- `matrix`: a `list[list[int]]`; the input matrix.
- `queries`: a `list[list[int]]`; the query strings.

**Output**
- A `list[int]`. Return a list containing the sum of every submatrix from top-left `(r1,c1)` to bottom-right `(r2,c2)` inclusive. This judge compares the sequence exactly: `answer[i]` must be the sum for `queries[i]`, preserving query order.

## Constraints
- `1 <= m, n <= 200`, `0 <= len(queries) <= 10^4`, query coordinates are valid and `r1 <= r2`, `c1 <= c2`.

## Examples
```text
Input: matrix = [[3,0,1,4,2],[5,6,3,2,1],[1,2,0,1,5],[4,1,0,1,7],[1,0,3,0,5]], queries = [[2,1,4,3],[1,1,2,2],[1,2,2,4]]
Output: [8,11,12]
Explanation: Each number is the sum of the requested rectangular region. The output is written in the required deterministic order.
```

## Understanding & Intuition
A batch of rectangle queries should not rescan the same matrix cells. Row prefixes reduce each query to the height of the rectangle. A 2D prefix sum reduces each query to four array lookups.

## Approach 1 — Naive / Brute Force
**Idea:** Sum every cell in every requested rectangle independently.
```python
class Solution:
    def rangeSum2D(self, matrix: list[list[int]], queries: list[list[int]]) -> list[int]:
        ans = []
        for r1, c1, r2, c2 in queries:
            total = 0
            for r in range(r1, r2 + 1):
                for c in range(c1, c2 + 1):
                    total += matrix[r][c]
            ans.append(total)
        return ans
```
- **Time:** O(q*m*n) — **Space:** O(q)

## Approach 2 — Better
**Idea:** Precompute row prefixes and sum one row segment per row in the query.
```python
class Solution:
    def rangeSum2D(self, matrix: list[list[int]], queries: list[list[int]]) -> list[int]:
        m, n = len(matrix), len(matrix[0])
        row_pref = [[0] * (n + 1) for _ in range(m)]
        for r in range(m):
            for c in range(n):
                row_pref[r][c + 1] = row_pref[r][c] + matrix[r][c]
        ans = []
        for r1, c1, r2, c2 in queries:
            total = 0
            for r in range(r1, r2 + 1):
                total += row_pref[r][c2 + 1] - row_pref[r][c1]
            ans.append(total)
        return ans
```
- **Time:** O(m*n + q*m) — **Space:** O(m*n + q)

## Approach 3 — Optimal
**Idea:** Precompute a summed-area table and answer each rectangle in constant time.
```python
class Solution:
    def rangeSum2D(self, matrix: list[list[int]], queries: list[list[int]]) -> list[int]:
        m, n = len(matrix), len(matrix[0])
        pref = [[0] * (n + 1) for _ in range(m + 1)]
        for r in range(m):
            for c in range(n):
                pref[r + 1][c + 1] = matrix[r][c] + pref[r][c + 1] + pref[r + 1][c] - pref[r][c]
        ans = []
        for r1, c1, r2, c2 in queries:
            ans.append(pref[r2 + 1][c2 + 1] - pref[r1][c2 + 1] - pref[r2 + 1][c1] + pref[r1][c1])
        return ans
```
- **Time:** O(m*n + q) — **Space:** O(m*n + q)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(q*m*n) | O(q) |
| Better | O(m*n + q*m) | O(m*n + q) |
| Optimal | O(m*n + q) | O(m*n + q) |

## Edge Cases & Pitfalls
- Empty `queries` should return `[]`.
- Include both rectangle endpoints.
- The method is a pure function, not a design-style mutable API.

## Related
- Matrix Block Sum
- Range Sum Query Immutable
