# 25. Snakes and Ladders

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
You are given an `n x n` Snakes and Ladders board labeled from square `1` to `n^2` in boustrophedon order: bottom row left-to-right, next row right-to-left, and so on.

Starting at square `1`, each move chooses a die roll from `1` to `6`. If the destination has a snake or ladder, you must move once to its target. Return the fewest moves to reach `n^2`, or `-1` if impossible.

**Input**
- `board`: an `n x n` grid; `-1` means no jump, otherwise the target square label.

**Output**
- An integer: the minimum number of die rolls, or `-1` if unreachable.

## Constraints
- `2 <= n <= 20`
- `board[r][c] == -1` or `1 <= board[r][c] <= n^2`
- Squares `1` and `n^2` do not contain a jump in the standard problem.

## Examples
```text
Input: board = [[-1,-1,-1],[-1,9,8],[-1,8,9]]
Output: 1
Explanation: From square 1, one die roll can land on a square that jumps to square 9, the final square. That takes exactly one move.
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
