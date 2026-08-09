# 14. Sudoku Solver

- **Difficulty:** Hard
- **Pattern:** Backtracking
- **Asked at:** Amazon, Google, Microsoft, Meta

## Problem
Solve a partially filled `9 x 9` Sudoku board by filling every empty cell.

Empty cells are marked with `.`. A completed board is valid when every row, every column, and each of the nine `3 x 3` sub-boxes contains the digits `1` through `9` exactly once. The given puzzle is guaranteed to have exactly one solution, and the board must be modified in-place.

**Input**
- `board`: a `9 x 9` list of strings, where each entry is `.` or a digit from `1` to `9`.

**Output**
- Modify `board` in-place to the unique solved board. The judge checks the final board exactly in row-major order.

## Constraints
- `board.length == 9`
- `board[i].length == 9`
- `board[i][j]` is `.` or one of `1` through `9`.
- The initial filled cells do not violate Sudoku rules.
- The puzzle has exactly one valid solution.

## Examples
```text
Input: board = [["5","3",".",".","7",".",".",".","."],["6",".",".","1","9","5",".",".","."],[".","9","8",".",".",".",".","6","."],["8",".",".",".","6",".",".",".","3"],["4",".",".","8",".","3",".",".","1"],["7",".",".",".","2",".",".",".","6"],[".","6",".",".",".",".","2","8","."],[".",".",".","4","1","9",".",".","5"],[".",".",".",".","8",".",".","7","9"]]
Output: [["5","3","4","6","7","8","9","1","2"],["6","7","2","1","9","5","3","4","8"],["1","9","8","3","4","2","5","6","7"],["8","5","9","7","6","1","4","2","3"],["4","2","6","8","5","3","7","9","1"],["7","1","3","9","2","4","8","5","6"],["9","6","1","5","3","7","2","8","4"],["2","8","7","4","1","9","6","3","5"],["3","4","5","2","8","6","1","7","9"]]
Explanation: Filling the blanks with the shown digits makes every row, column, and `3 x 3` box contain each digit from `1` through `9` exactly once, so this is the unique solved board.
```

## Understanding & Intuition
Sudoku is a constraint satisfaction problem. Backtracking tries a digit in an empty cell, checks constraints, and moves on. Picking the emptiest-constrained cell first greatly reduces branching.

## Approach 1 — Naive / Brute Force
**Idea:** Scan for the first empty cell and try digits, validating rows, columns, and boxes by scanning every time.
```python
from typing import List

class Solution:
    def solveSudoku(self, board: List[List[str]]) -> None:
        def is_valid(r: int, c: int, ch: str) -> bool:
            for i in range(9):
                if board[r][i] == ch or board[i][c] == ch:
                    return False
            br, bc = (r // 3) * 3, (c // 3) * 3
            for i in range(br, br + 3):
                for j in range(bc, bc + 3):
                    if board[i][j] == ch:
                        return False
            return True

        def solve() -> bool:
            for r in range(9):
                for c in range(9):
                    if board[r][c] == ".":
                        for ch in "123456789":
                            if is_valid(r, c, ch):
                                board[r][c] = ch
                                if solve():
                                    return True
                                board[r][c] = "."
                        return False
            return True

        solve()
```
- **Time:** O(9^E * 81) — **Space:** O(E), where `E` is empty cells

## Approach 2 — Better
**Idea:** Maintain sets for rows, columns, and boxes so checking a candidate is constant time.
```python
from typing import List

class Solution:
    def solveSudoku(self, board: List[List[str]]) -> None:
        rows = [set() for _ in range(9)]
        cols = [set() for _ in range(9)]
        boxes = [set() for _ in range(9)]
        empties = []
        for r in range(9):
            for c in range(9):
                if board[r][c] == ".":
                    empties.append((r, c))
                else:
                    ch = board[r][c]
                    rows[r].add(ch); cols[c].add(ch); boxes[(r // 3) * 3 + c // 3].add(ch)

        def solve(pos: int) -> bool:
            if pos == len(empties):
                return True
            r, c = empties[pos]
            b = (r // 3) * 3 + c // 3
            for ch in "123456789":
                if ch in rows[r] or ch in cols[c] or ch in boxes[b]:
                    continue
                board[r][c] = ch
                rows[r].add(ch); cols[c].add(ch); boxes[b].add(ch)
                if solve(pos + 1):
                    return True
                rows[r].remove(ch); cols[c].remove(ch); boxes[b].remove(ch)
                board[r][c] = "."
            return False

        solve(0)
```
- **Time:** O(9^E) — **Space:** O(E + 27)

## Approach 3 — Optimal
**Idea:** Use bitmasks and choose the unfilled cell with the fewest candidates at every step.
```python
from typing import List

class Solution:
    def solveSudoku(self, board: List[List[str]]) -> None:
        rows = [0] * 9
        cols = [0] * 9
        boxes = [0] * 9
        empties = []
        full = (1 << 9) - 1
        for r in range(9):
            for c in range(9):
                if board[r][c] == ".":
                    empties.append((r, c))
                else:
                    bit = 1 << (ord(board[r][c]) - ord("1"))
                    rows[r] |= bit; cols[c] |= bit; boxes[(r // 3) * 3 + c // 3] |= bit

        def solve(pos: int) -> bool:
            if pos == len(empties):
                return True
            best = pos
            best_mask = 0
            best_count = 10
            for i in range(pos, len(empties)):
                r, c = empties[i]
                b = (r // 3) * 3 + c // 3
                mask = full & ~(rows[r] | cols[c] | boxes[b])
                count = mask.bit_count()
                if count < best_count:
                    best, best_mask, best_count = i, mask, count
                    if count == 1:
                        break
            if best_count == 0:
                return False
            empties[pos], empties[best] = empties[best], empties[pos]
            r, c = empties[pos]
            b = (r // 3) * 3 + c // 3
            mask = best_mask
            while mask:
                bit = mask & -mask
                mask -= bit
                ch = str(bit.bit_length())
                board[r][c] = ch
                rows[r] |= bit; cols[c] |= bit; boxes[b] |= bit
                if solve(pos + 1):
                    return True
                rows[r] ^= bit; cols[c] ^= bit; boxes[b] ^= bit
                board[r][c] = "."
            empties[pos], empties[best] = empties[best], empties[pos]
            return False

        solve(0)
```
- **Time:** O(9^E) worst case — **Space:** O(E + 27)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(9^E * 81) | O(E) |
| Better | O(9^E) | O(E + 27) |
| Optimal | O(9^E) | O(E + 27) |

## Edge Cases & Pitfalls
- The method returns `None`; it mutates `board`.
- Box index is `(row // 3) * 3 + col // 3`.
- Restore masks and board cells during backtracking.

## Related
- N-Queens
- Word Search
- Beautiful Arrangement
