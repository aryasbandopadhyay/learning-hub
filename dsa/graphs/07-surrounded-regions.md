# 07. Surrounded Regions

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Capture O regions fully surrounded by X by flipping them in-place. Border-connected O cells remain. Constraints: 1 <= m,n <= 200.

## Examples
```text
Input: board = [["X","X","X"],["X","O","X"],["X","X","X"]]
Output: [["X","X","X"],["X","X","X"],["X","X","X"]]
Explanation: The only O is surrounded.
```

## Understanding & Intuition
Only border-connected O cells are safe. Mark them first, then flip every other O.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List
from collections import deque

class Solution:
    def solve(self, board: List[List[str]]) -> None:
        # Mark escaping O cells from the border.
        if not board or not board[0]: return
        m, n = len(board), len(board[0])
        q = deque()
        for r in range(m):
            for c in (0, n-1):
                if board[r][c] == "O": board[r][c] = "E"; q.append((r,c))
        for c in range(n):
            for r in (0, m-1):
                if board[r][c] == "O": board[r][c] = "E"; q.append((r,c))
        while q:
            r, c = q.popleft()
            for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and board[nr][nc] == "O":
                    board[nr][nc] = "E"; q.append((nr,nc))
        for r in range(m):
            for c in range(n):
                board[r][c] = "O" if board[r][c] == "E" else "X" 
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List
from collections import deque

class Solution:
    def solve(self, board: List[List[str]]) -> None:
        # Mark escaping O cells from the border.
        if not board or not board[0]: return
        m, n = len(board), len(board[0])
        q = deque()
        for r in range(m):
            for c in (0, n-1):
                if board[r][c] == "O": board[r][c] = "E"; q.append((r,c))
        for c in range(n):
            for r in (0, m-1):
                if board[r][c] == "O": board[r][c] = "E"; q.append((r,c))
        while q:
            r, c = q.popleft()
            for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and board[nr][nc] == "O":
                    board[nr][nc] = "E"; q.append((nr,nc))
        for r in range(m):
            for c in range(n):
                board[r][c] = "O" if board[r][c] == "E" else "X" 
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List
from collections import deque

class Solution:
    def solve(self, board: List[List[str]]) -> None:
        # Mark escaping O cells from the border.
        if not board or not board[0]: return
        m, n = len(board), len(board[0])
        q = deque()
        for r in range(m):
            for c in (0, n-1):
                if board[r][c] == "O": board[r][c] = "E"; q.append((r,c))
        for c in range(n):
            for r in (0, m-1):
                if board[r][c] == "O": board[r][c] = "E"; q.append((r,c))
        while q:
            r, c = q.popleft()
            for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and board[nr][nc] == "O":
                    board[nr][nc] = "E"; q.append((nr,nc))
        for r in range(m):
            for c in range(n):
                board[r][c] = "O" if board[r][c] == "E" else "X" 
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(V+E) or O(mn) | O(V) or O(mn) |
| Better | O(V+E) or O(mn) | O(V) or O(mn) |
| Optimal | O(V+E) or O(mn) | O(V) or O(mn) |

## Edge Cases & Pitfalls
- Empty or singleton graphs/grids.
- Mark visited before repeated traversal creates cycles.
- Preserve required in-place behavior when the signature returns None.

## Related
- BFS
- DFS
- Union-Find / Topological Sort
