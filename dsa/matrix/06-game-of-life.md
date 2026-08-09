# 06. Game of Life

- **Difficulty:** Medium
- **Pattern:** Matrix
- **Asked at:** Amazon, Microsoft, Google, Meta

## Problem
You are given a board for Conway's Game of Life, where `1` is live and `0` is dead. Each cell has up to eight neighbours.

Update the board simultaneously: live cells with fewer than two or more than three live neighbours die, live cells with two or three live neighbours live, and dead cells with exactly three live neighbours become live. Modify in place and return the final board for the judge.

**Input**
- `board`: an `m x n` grid of `0`s and `1`s.

**Output**
- The next board state. **This judge compares exactly**, so every cell must match the simultaneous update.

## Constraints
- `m == board.length`
- `n == board[r].length`
- `1 <= m, n <= 25`
- `board[r][c]` is `0` or `1`.

## Examples
```text
Input: board = [[0,1,0],[0,0,1],[1,1,1],[0,0,0]]
Output: [[0,0,0],[1,0,1],[0,1,1],[0,1,0]]
Explanation: Each cell is updated from the original neighbour counts, not from partial updates. Applying the rules simultaneously produces the shown generation.
```

## Understanding & Intuition
All cells must update simultaneously, so original states must remain readable while writing next states. Extra memory is straightforward. The in-place solution encodes transitions using temporary values.

## Approach 1 — Naive / Brute Force
**Idea:** Copy the board and compute each next cell from the copy.
```python
from typing import List

class Solution:
    def gameOfLife(self, board: List[List[int]]) -> None:
        m, n = len(board), len(board[0])
        old = [row[:] for row in board]

        def live_neighbors(r: int, c: int) -> int:
            total = 0
            for dr in (-1, 0, 1):
                for dc in (-1, 0, 1):
                    if dr == 0 and dc == 0:
                        continue
                    nr, nc = r + dr, c + dc
                    if 0 <= nr < m and 0 <= nc < n:
                        total += old[nr][nc]
            return total

        for r in range(m):
            for c in range(n):
                live = live_neighbors(r, c)
                board[r][c] = 1 if live == 3 or (old[r][c] == 1 and live == 2) else 0
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Store only the coordinates that will be live in the next generation.
```python
from typing import List

class Solution:
    def gameOfLife(self, board: List[List[int]]) -> None:
        m, n = len(board), len(board[0])
        next_live = set()
        for r in range(m):
            for c in range(n):
                live = 0
                for dr in (-1, 0, 1):
                    for dc in (-1, 0, 1):
                        if dr == 0 and dc == 0:
                            continue
                        nr, nc = r + dr, c + dc
                        if 0 <= nr < m and 0 <= nc < n:
                            live += board[nr][nc]
                if live == 3 or (board[r][c] == 1 and live == 2):
                    next_live.add((r, c))

        for r in range(m):
            for c in range(n):
                board[r][c] = 1 if (r, c) in next_live else 0
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** Encode transitions in-place: `-1` means live-to-dead and `2` means dead-to-live, while `abs(value) == 1` tells original live.
```python
from typing import List

class Solution:
    def gameOfLife(self, board: List[List[int]]) -> None:
        m, n = len(board), len(board[0])
        for r in range(m):
            for c in range(n):
                live = 0
                for dr in (-1, 0, 1):
                    for dc in (-1, 0, 1):
                        if dr == 0 and dc == 0:
                            continue
                        nr, nc = r + dr, c + dc
                        if 0 <= nr < m and 0 <= nc < n and abs(board[nr][nc]) == 1:
                            live += 1
                if board[r][c] == 1 and (live < 2 or live > 3):
                    board[r][c] = -1
                elif board[r][c] == 0 and live == 3:
                    board[r][c] = 2

        for r in range(m):
            for c in range(n):
                board[r][c] = 1 if board[r][c] > 0 else 0
```
- **Time:** O(mn) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn) | O(mn) |
| Better | O(mn) | O(mn) |
| Optimal | O(mn) | O(1) |

## Edge Cases & Pitfalls
- Neighbors include diagonals.
- Temporary transition markers must still reveal original live/dead state.

## Related
- Set Matrix Zeroes
- Minesweeper
