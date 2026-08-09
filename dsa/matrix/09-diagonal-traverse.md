# 09. Diagonal Traverse

- **Difficulty:** Medium
- **Pattern:** Matrix
- **Asked at:** Amazon, Microsoft, Google, Apple

## Problem
Given an `m x n` matrix, return its elements in diagonal traversal order.

Cells with the same `r + c` are on the same diagonal. Visit diagonals from smallest to largest `r + c`, reversing every even-numbered diagonal so movement alternates up-right and down-left.

**Input**
- `mat`: a 2-D list of integers.

**Output**
- A list of values. **This judge compares exactly**, so values must follow the standard alternating diagonal order.

## Constraints
- `m == mat.length`
- `n == mat[r].length`
- `1 <= m, n <= 10^4`
- `1 <= m * n <= 10^4`
- `-10^5 <= mat[r][c] <= 10^5`.

## Examples
```text
Input: mat = [[1,2,3],[4,5,6],[7,8,9]]
Output: [1,2,4,7,5,3,6,8,9]
Explanation: The diagonals are visited in increasing `r+c`, reversing alternating diagonals, which yields `[1,2,4,7,5,3,6,8,9]`.
```

## Understanding & Intuition
All cells on one diagonal share the same sum `r + c`. The required order alternates direction per diagonal. We can either group diagonals or simulate the bouncing movement.

## Approach 1 — Naive / Brute Force
**Idea:** For every possible diagonal sum, scan the whole matrix and collect matching cells.
```python
from typing import List

class Solution:
    def findDiagonalOrder(self, mat: List[List[int]]) -> List[int]:
        m, n = len(mat), len(mat[0])
        ans = []
        for s in range(m + n - 1):
            diag = []
            for r in range(m):
                for c in range(n):
                    if r + c == s:
                        diag.append(mat[r][c])
            if s % 2 == 0:
                diag.reverse()
            ans.extend(diag)
        return ans
```
- **Time:** O((m+n)mn) — **Space:** O(min(m,n))

## Approach 2 — Better
**Idea:** Build each diagonal directly by choosing a valid starting row and walking down-left.
```python
from typing import List

class Solution:
    def findDiagonalOrder(self, mat: List[List[int]]) -> List[int]:
        m, n = len(mat), len(mat[0])
        ans = []
        for s in range(m + n - 1):
            r = 0 if s < n else s - n + 1
            c = s if s < n else n - 1
            diag = []
            while r < m and c >= 0:
                diag.append(mat[r][c])
                r += 1
                c -= 1
            ans.extend(reversed(diag) if s % 2 == 0 else diag)
        return ans
```
- **Time:** O(mn) — **Space:** O(min(m,n))

## Approach 3 — Optimal
**Idea:** Simulate movement and change direction when hitting a boundary, avoiding per-diagonal storage.
```python
from typing import List

class Solution:
    def findDiagonalOrder(self, mat: List[List[int]]) -> List[int]:
        m, n = len(mat), len(mat[0])
        r = c = 0
        direction = 1  # 1 means up-right, -1 means down-left.
        ans = []

        for _ in range(m * n):
            ans.append(mat[r][c])
            if direction == 1:
                if c == n - 1:
                    r += 1
                    direction = -1
                elif r == 0:
                    c += 1
                    direction = -1
                else:
                    r -= 1
                    c += 1
            else:
                if r == m - 1:
                    c += 1
                    direction = 1
                elif c == 0:
                    r += 1
                    direction = 1
                else:
                    r += 1
                    c -= 1
        return ans
```
- **Time:** O(mn) — **Space:** O(1) auxiliary

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((m+n)mn) | O(min(m,n)) |
| Better | O(mn) | O(min(m,n)) |
| Optimal | O(mn) | O(1) |

## Edge Cases & Pitfalls
- Single row or single column must not step out of bounds.
- Direction changes differ depending on which boundary is hit first.

## Related
- Spiral Matrix
- Transpose Matrix
