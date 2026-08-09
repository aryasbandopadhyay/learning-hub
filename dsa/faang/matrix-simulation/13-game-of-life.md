# 13. Game Of Life

- **Difficulty:** Medium
- **Pattern:** Matrix Simulation
- **Asked at:** Amazon, Google, Microsoft

## Problem
Given an `m x n` board of `0`s and `1`s representing Conway's Game of Life, update the board to its next state using the standard rules: live cells with fewer than two or more than three live neighbors die, live cells with two or three live neighbors survive, and dead cells with exactly three live neighbors become live. Modify `board` in place and return it.

**Input**
- `board`: a `list[list[int]]`; the Game of Life board.

**Output**
- A `list[list[int]]`. Return it. This judge compares the sequence exactly: return the board after one simultaneous update, preserving its row and column layout.

## Constraints
- `1 <= m, n <= 25`, `board[i][j]` is `0` or `1`.

## Examples
```text
Input: board = [[0,1,0],[0,0,1],[1,1,1],[0,0,0]]
Output: [[0,0,0],[1,0,1],[0,1,1],[0,1,0]]
Explanation: Applying all live/dead updates simultaneously produces the returned board. The output is written in the required deterministic order.
```

## Understanding & Intuition
All cells must update simultaneously, so newly changed cells cannot affect neighbors in the same step. A copy, a set of next live cells, or encoded transition states can preserve the original state while computing the next one. The optimal version stores transition markers directly in the board.

## Approach 1 — Naive / Brute Force
**Idea:** copy the entire board, count neighbors from the copy, then write the next state into the original board.
```python
class Solution:
    def gameOfLife(self, board):
        if not board or not board[0]:
            return board
        rows = len(board)
        cols = len(board[0])
        old = [row[:] for row in board]
        directions = [(-1, -1), (-1, 0), (-1, 1), (0, -1), (0, 1), (1, -1), (1, 0), (1, 1)]
        for r in range(rows):
            for c in range(cols):
                live = 0
                for dr, dc in directions:
                    nr, nc = r + dr, c + dc
                    if 0 <= nr < rows and 0 <= nc < cols:
                        live += old[nr][nc]
                if old[r][c] == 1:
                    board[r][c] = 1 if live == 2 or live == 3 else 0
                else:
                    board[r][c] = 1 if live == 3 else 0
        return board
```
- **Time:** O(m * n) — **Space:** O(m * n)

## Approach 2 — Better
**Idea:** store original live cells in a set, count only their neighbors, then rewrite the board from the next live set.
```python
class Solution:
    def gameOfLife(self, board):
        if not board or not board[0]:
            return board
        rows = len(board)
        cols = len(board[0])
        live = set()
        for r in range(rows):
            for c in range(cols):
                if board[r][c] == 1:
                    live.add((r, c))
        counts = {}
        directions = [(-1, -1), (-1, 0), (-1, 1), (0, -1), (0, 1), (1, -1), (1, 0), (1, 1)]
        for r, c in live:
            for dr, dc in directions:
                nr, nc = r + dr, c + dc
                if 0 <= nr < rows and 0 <= nc < cols:
                    counts[(nr, nc)] = counts.get((nr, nc), 0) + 1
        next_live = set()
        for r in range(rows):
            for c in range(cols):
                neighbors = counts.get((r, c), 0)
                if neighbors == 3 or ((r, c) in live and neighbors == 2):
                    next_live.add((r, c))
        for r in range(rows):
            for c in range(cols):
                board[r][c] = 1 if (r, c) in next_live else 0
        return board
```
- **Time:** O(m * n) — **Space:** O(m * n)

## Approach 3 — Optimal
**Idea:** encode transitions in place: `-1` means live to dead and `2` means dead to live, then normalize.
```python
class Solution:
    def gameOfLife(self, board):
        if not board or not board[0]:
            return board
        rows = len(board)
        cols = len(board[0])
        directions = [(-1, -1), (-1, 0), (-1, 1), (0, -1), (0, 1), (1, -1), (1, 0), (1, 1)]
        for r in range(rows):
            for c in range(cols):
                live = 0
                for dr, dc in directions:
                    nr, nc = r + dr, c + dc
                    if 0 <= nr < rows and 0 <= nc < cols and abs(board[nr][nc]) == 1:
                        live += 1
                if board[r][c] == 1 and (live < 2 or live > 3):
                    board[r][c] = -1
                elif board[r][c] == 0 and live == 3:
                    board[r][c] = 2
        for r in range(rows):
            for c in range(cols):
                board[r][c] = 1 if board[r][c] > 0 else 0
        return board
```
- **Time:** O(m * n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(m * n) | O(m * n) |
| Better | O(m * n) | O(m * n) |
| Optimal | O(m * n) | O(1) |

## Edge Cases & Pitfalls
- Updates must be simultaneous, not sequential.
- In the in-place encoding, `-1` still counts as originally live and `2` counts as originally dead.
- Return the mutated `board` so the judge can compare JSON-friendly output.

## Related
- Set Matrix Zeroes
- Minesweeper
