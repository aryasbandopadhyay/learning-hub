# 10. N-Queens

- **Difficulty:** Hard
- **Pattern:** Backtracking
- **Asked at:** Google, Amazon, Meta, Microsoft

## Problem
Place `n` queens on an `n x n` chessboard so no two queens attack each other. Return all distinct board configurations, where `Q` is a queen and `.` is empty. `1 <= n <= 9`.

## Examples
```text
Input: n = 4
Output: [[".Q..","...Q","Q...","..Q."],["..Q.","Q...","...Q",".Q.."]]
Explanation: There are two safe configurations for four queens.
```

## Understanding & Intuition
One queen must be placed in each row. A placement is valid if its column and both diagonals are unused. Sets or bitmasks make those safety checks constant time.

## Approach 1 — Naive / Brute Force
**Idea:** Try every column in every row, then validate the entire board when all rows are filled.
```python
from typing import List

class Solution:
    def solveNQueens(self, n: int) -> List[List[str]]:
        result = []
        cols = []

        def valid() -> bool:
            for r1 in range(n):
                for r2 in range(r1 + 1, n):
                    c1, c2 = cols[r1], cols[r2]
                    if c1 == c2 or abs(r1 - r2) == abs(c1 - c2):
                        return False
            return True

        def dfs(row: int) -> None:
            if row == n:
                if valid():
                    result.append(["." * c + "Q" + "." * (n - c - 1) for c in cols])
                return
            for c in range(n):
                cols.append(c)
                dfs(row + 1)
                cols.pop()

        dfs(0)
        return result
```
- **Time:** O(n^n * n^2) — **Space:** O(n) auxiliary plus output

## Approach 2 — Better
**Idea:** Keep sets of occupied columns and diagonals to reject unsafe cells immediately.
```python
from typing import List

class Solution:
    def solveNQueens(self, n: int) -> List[List[str]]:
        result = []
        board = [["."] * n for _ in range(n)]
        cols, diag1, diag2 = set(), set(), set()

        def backtrack(row: int) -> None:
            if row == n:
                result.append(["".join(r) for r in board])
                return
            for c in range(n):
                if c in cols or row - c in diag1 or row + c in diag2:
                    continue
                cols.add(c); diag1.add(row - c); diag2.add(row + c)
                board[row][c] = "Q"
                backtrack(row + 1)
                board[row][c] = "."
                cols.remove(c); diag1.remove(row - c); diag2.remove(row + c)

        backtrack(0)
        return result
```
- **Time:** O(n!) — **Space:** O(n^2) board plus output

## Approach 3 — Optimal
**Idea:** Use bitmasks for occupied columns and diagonals, iterating only currently available positions.
```python
from typing import List

class Solution:
    def solveNQueens(self, n: int) -> List[List[str]]:
        result = []
        cols = [-1] * n
        full = (1 << n) - 1

        def make_board() -> List[str]:
            return ["." * c + "Q" + "." * (n - c - 1) for c in cols]

        def backtrack(row: int, col_mask: int, left_diag: int, right_diag: int) -> None:
            if row == n:
                result.append(make_board())
                return
            available = full & ~(col_mask | left_diag | right_diag)
            while available:
                bit = available & -available
                available -= bit
                c = bit.bit_length() - 1
                cols[row] = c
                backtrack(row + 1,
                          col_mask | bit,
                          (left_diag | bit) << 1,
                          (right_diag | bit) >> 1)

        backtrack(0, 0, 0, 0)
        return result
```
- **Time:** O(n!) — **Space:** O(n) auxiliary plus output

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^n * n^2) | O(n) plus output |
| Better | O(n!) | O(n^2) plus output |
| Optimal | O(n!) | O(n) plus output |

## Edge Cases & Pitfalls
- Diagonals are identified by `row - col` and `row + col`.
- Restore board and sets after recursion.
- For bitmasks, limit available positions with `full`.

## Related
- Sudoku Solver
- Word Search
- Beautiful Arrangement
