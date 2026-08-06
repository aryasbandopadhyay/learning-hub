# 05. Valid Sudoku

- **Difficulty:** Medium
- **Pattern:** Matrix
- **Asked at:** Amazon, Microsoft, Google, Meta

## Problem
Given a partially filled `9 x 9` Sudoku board, determine if it is valid. Only filled cells `'1'` to `'9'` are checked; empty cells are `'.'`. Constraints: the board is always `9 x 9`.

## Examples
```text
Input: board = [["5","3",".",".","7",".",".",".","."],["6",".",".","1","9","5",".",".","."],[".","9","8",".",".",".",".","6","."],["8",".",".",".","6",".",".",".","3"],["4",".",".","8",".","3",".",".","1"],["7",".",".",".","2",".",".",".","6"],[".","6",".",".",".",".","2","8","."],[".",".",".","4","1","9",".",".","5"],[".",".",".",".","8",".",".","7","9"]]
Output: true
Explanation: No row, column, or 3x3 box contains a duplicate filled digit.
```

## Understanding & Intuition
Sudoku validity is local: rows, columns, and boxes must each contain unique digits. The board does not need to be solvable. Mapping each cell to its box index lets us scan once.

## Approach 1 — Naive / Brute Force
**Idea:** For every filled cell, scan its row, column, and box for another equal digit.
```python
from typing import List

class Solution:
    def isValidSudoku(self, board: List[List[str]]) -> bool:
        for r in range(9):
            for c in range(9):
                digit = board[r][c]
                if digit == ".":
                    continue
                for j in range(9):
                    if j != c and board[r][j] == digit:
                        return False
                for i in range(9):
                    if i != r and board[i][c] == digit:
                        return False
                br, bc = (r // 3) * 3, (c // 3) * 3
                for i in range(br, br + 3):
                    for j in range(bc, bc + 3):
                        if (i, j) != (r, c) and board[i][j] == digit:
                            return False
        return True
```
- **Time:** O(1) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Validate each row, column, and box by collecting seen digits in a set.
```python
from typing import List

class Solution:
    def isValidSudoku(self, board: List[List[str]]) -> bool:
        def valid(values: list[str]) -> bool:
            digits = [v for v in values if v != "."]
            return len(digits) == len(set(digits))

        for i in range(9):
            if not valid(board[i]):
                return False
            if not valid([board[r][i] for r in range(9)]):
                return False

        for br in range(0, 9, 3):
            for bc in range(0, 9, 3):
                box = [board[r][c] for r in range(br, br + 3) for c in range(bc, bc + 3)]
                if not valid(box):
                    return False
        return True
```
- **Time:** O(1) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Scan once and store row, column, and box keys in one set.
```python
from typing import List

class Solution:
    def isValidSudoku(self, board: List[List[str]]) -> bool:
        seen = set()
        for r in range(9):
            for c in range(9):
                digit = board[r][c]
                if digit == ".":
                    continue
                box = (r // 3, c // 3)
                keys = ((digit, "row", r), (digit, "col", c), (digit, "box", box))
                if any(key in seen for key in keys):
                    return False
                seen.update(keys)
        return True
```
- **Time:** O(1) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(1) | O(1) |
| Better | O(1) | O(1) |
| Optimal | O(1) | O(1) |

## Edge Cases & Pitfalls
- Do not require empty cells to make a solvable puzzle.
- Box index is based on integer division by 3.

## Related
- Sudoku Solver
- Word Search
