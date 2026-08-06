# 25. Snakes and Ladders

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given a snakes-and-ladders board, return the fewest dice moves to reach square n^2 or -1. Constraints: 2 <= n <= 20.

## Examples
```text
Input: board = [[-1,-1,-1],[-1,9,8],[-1,8,9]]
Output: 1
Explanation: One roll can land on a jump to square 9.
```

## Understanding & Intuition
Squares are nodes and dice rolls are unweighted edges. BFS gives the minimum number of moves.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List
from collections import deque

class Solution:
    def snakesAndLadders(self, board: List[List[int]]) -> int:
        # Flatten boustrophedon board, then BFS.
        n = len(board); cells = [-1]; left = True
        for r in range(n - 1, -1, -1):
            row = board[r] if left else board[r][::-1]
            cells.extend(row); left = not left
        q, seen = deque([(1, 0)]), {1}
        while q:
            sq, moves = q.popleft()
            if sq == n * n: return moves
            for nxt in range(sq + 1, min(sq + 6, n*n) + 1):
                dest = cells[nxt] if cells[nxt] != -1 else nxt
                if dest not in seen:
                    seen.add(dest); q.append((dest, moves + 1))
        return -1
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List
from collections import deque

class Solution:
    def snakesAndLadders(self, board: List[List[int]]) -> int:
        # Flatten boustrophedon board, then BFS.
        n = len(board); cells = [-1]; left = True
        for r in range(n - 1, -1, -1):
            row = board[r] if left else board[r][::-1]
            cells.extend(row); left = not left
        q, seen = deque([(1, 0)]), {1}
        while q:
            sq, moves = q.popleft()
            if sq == n * n: return moves
            for nxt in range(sq + 1, min(sq + 6, n*n) + 1):
                dest = cells[nxt] if cells[nxt] != -1 else nxt
                if dest not in seen:
                    seen.add(dest); q.append((dest, moves + 1))
        return -1
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List
from collections import deque

class Solution:
    def snakesAndLadders(self, board: List[List[int]]) -> int:
        # Flatten boustrophedon board, then BFS.
        n = len(board); cells = [-1]; left = True
        for r in range(n - 1, -1, -1):
            row = board[r] if left else board[r][::-1]
            cells.extend(row); left = not left
        q, seen = deque([(1, 0)]), {1}
        while q:
            sq, moves = q.popleft()
            if sq == n * n: return moves
            for nxt in range(sq + 1, min(sq + 6, n*n) + 1):
                dest = cells[nxt] if cells[nxt] != -1 else nxt
                if dest not in seen:
                    seen.add(dest); q.append((dest, moves + 1))
        return -1
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
