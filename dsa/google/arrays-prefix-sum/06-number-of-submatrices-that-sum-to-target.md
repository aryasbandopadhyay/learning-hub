# 06. Number of Submatrices That Sum to Target

- **Difficulty:** Hard
- **Pattern:** Arrays & Prefix Sums
- **Asked at:** Google, Amazon, Meta

## Problem
You are given an `m x n` integer matrix `matrix` and an integer `target`.

Count all non-empty rectangular submatrices whose elements sum exactly to `target`. A submatrix must use contiguous rows and contiguous columns.

**Input**
- `matrix`: a 2-D list of integers with `m` rows and `n` columns.
- `target`: the required submatrix sum.

**Output**
- The number of rectangular submatrices with sum exactly `target`.

## Constraints
- `1 <= m, n <= 100`
- `-1000 <= matrix[r][c] <= 1000`
- `-10^8 <= target <= 10^8`

## Examples
```text
Input: matrix = [[0,1,0],[1,1,1],[0,1,0]], target = 0
Output: 4
Explanation: There are four single-cell submatrices containing `0`; no larger rectangle in this matrix sums to `0`.
```

## Understanding & Intuition
A 2D submatrix sum can be answered by prefix sums, but enumerating all rectangles is expensive. Fixing a pair of rows compresses the matrix into column sums, reducing the problem to counting subarrays with a target sum. A hash map of prefix frequencies counts those subarrays in linear time per row pair.

## Approach 1 — Naive / Brute Force
**Idea:** Build a 2D prefix sum and enumerate every rectangle.
```python
class Solution:
    def numSubmatrixSumTarget(self, matrix: list[list[int]], target: int) -> int:
        m, n = len(matrix), len(matrix[0])
        pref = [[0] * (n + 1) for _ in range(m + 1)]
        for i in range(m):
            for j in range(n):
                pref[i + 1][j + 1] = matrix[i][j] + pref[i][j + 1] + pref[i + 1][j] - pref[i][j]
        ans = 0
        for r1 in range(m):
            for c1 in range(n):
                for r2 in range(r1, m):
                    for c2 in range(c1, n):
                        total = pref[r2 + 1][c2 + 1] - pref[r1][c2 + 1] - pref[r2 + 1][c1] + pref[r1][c1]
                        if total == target:
                            ans += 1
        return ans
```
- **Time:** O(m^2 n^2) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Fix top and bottom rows, maintain column sums, then test all column ranges.
```python
class Solution:
    def numSubmatrixSumTarget(self, matrix: list[list[int]], target: int) -> int:
        m, n = len(matrix), len(matrix[0])
        ans = 0
        for top in range(m):
            col = [0] * n
            for bottom in range(top, m):
                for c in range(n):
                    col[c] += matrix[bottom][c]
                for left in range(n):
                    total = 0
                    for right in range(left, n):
                        total += col[right]
                        if total == target:
                            ans += 1
        return ans
```
- **Time:** O(m^2 n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** For each row pair, count target-sum subarrays of compressed column sums with a prefix-frequency map.
```python
class Solution:
    def numSubmatrixSumTarget(self, matrix: list[list[int]], target: int) -> int:
        m, n = len(matrix), len(matrix[0])
        ans = 0
        for top in range(m):
            col = [0] * n
            for bottom in range(top, m):
                for c in range(n):
                    col[c] += matrix[bottom][c]
                freq = {0: 1}
                total = 0
                for x in col:
                    total += x
                    ans += freq.get(total - target, 0)
                    freq[total] = freq.get(total, 0) + 1
        return ans
```
- **Time:** O(m^2 n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(m^2 n^2) | O(mn) |
| Better | O(m^2 n^2) | O(n) |
| Optimal | O(m^2 n) | O(n) |

## Edge Cases & Pitfalls
- Negative values require prefix-frequency counting, not a sliding window.
- Single-cell submatrices are valid.
- If columns are much fewer than rows, fixing row pairs is efficient.

## Related
- Range Sum Query 2D Immutable
- Subarray Sum Equals K
