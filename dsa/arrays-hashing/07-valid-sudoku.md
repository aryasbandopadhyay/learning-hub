# 07. Valid Sudoku

- **Difficulty:** Medium
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Apple

## Problem
Given a partially filled `9 x 9` Sudoku board, determine whether the filled cells obey Sudoku rules. Empty cells `.` do not need to make the puzzle solvable.

**Input**
- `board`: a `9 x 9` list of strings, each either `"1"` through `"9"` or `"."`.

**Output**
- `True` if every row, column, and `3 x 3` sub-box has no repeated digit; otherwise `False`.

## Constraints
- `board.length == 9`
- `board[r].length == 9`
- `board[r][c]` is `"."` or a digit from `"1"` to `"9"`.

## Examples
```text
Input: board = [["5","3",".",".","7",".",".",".","."],["6",".",".","1","9","5",".",".","."],[".","9","8",".",".",".",".","6","."],["8",".",".",".","6",".",".",".","3"],["4",".",".","8",".","3",".",".","1"],["7",".",".",".","2",".",".",".","6"],[".","6",".",".",".",".","2","8","."],[".",".",".","4","1","9",".",".","5"],[".",".",".",".","8",".",".","7","9"]]
Output: True
Explanation: Every filled digit is unique within its row, column, and `3 x 3` box, so the partial board is valid.
```

## Understanding & Intuition
This is duplicate detection over rows, columns, and boxes. Sets store seen digits; a single tagged set is the compact refinement.

## Approach 1 — Naive / Brute Force
**Idea:** Check rows, columns, and boxes as separate units.
```python
class Solution:
    def isValidSudoku(self, board: list[list[str]]) -> bool:
        def ok(vals):
            seen = set()
            for v in vals:
                if v == '.':
                    continue
                if v in seen:
                    return False
                seen.add(v)
            return True
        for r in range(9):
            if not ok(board[r]):
                return False
        for c in range(9):
            if not ok([board[r][c] for r in range(9)]):
                return False
        for br in range(0, 9, 3):
            for bc in range(0, 9, 3):
                if not ok([board[r][c] for r in range(br, br+3) for c in range(bc, bc+3)]):
                    return False
        return True
```
- **Time:** O(1) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Maintain row, column, and box sets while scanning.
```python
class Solution:
    def isValidSudoku(self, board: list[list[str]]) -> bool:
        rows = [set() for _ in range(9)]
        cols = [set() for _ in range(9)]
        boxes = [set() for _ in range(9)]
        for r in range(9):
            for c in range(9):
                v = board[r][c]
                if v == '.':
                    continue
                b = (r // 3) * 3 + c // 3
                if v in rows[r] or v in cols[c] or v in boxes[b]:
                    return False
                rows[r].add(v); cols[c].add(v); boxes[b].add(v)
        return True
```
- **Time:** O(1) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use one set of tagged row/column/box constraints.
```python
class Solution:
    def isValidSudoku(self, board: list[list[str]]) -> bool:
        seen = set()
        for r in range(9):
            for c in range(9):
                v = board[r][c]
                if v == '.':
                    continue
                tags = ((v, 'r', r), (v, 'c', c), (v, 'b', r//3, c//3))
                for tag in tags:
                    if tag in seen:
                        return False
                    seen.add(tag)
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
- Ignore dots.
- Box indices use division by 3.
- A valid board need not be solvable.

## Related
- Sudoku Solver
- N-Queens
