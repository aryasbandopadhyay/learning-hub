# 04. Walls and Gates

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Meta, Amazon, Google, DoorDash

## Problem
You are given a grid of rooms, walls, and gates. A wall is `-1`, a gate is `0`, and an empty room is `2147483647` (infinity).

Fill each empty room with the distance to its nearest gate using up, down, left, and right moves. Walls block movement. Unreachable rooms stay `2147483647`. Modify `rooms` in place and return the updated grid for the judge.

**Input**
- `rooms`: an `m x n` integer grid containing `-1`, `0`, or `2147483647`.

**Output**
- The final grid. **This judge compares exactly**, so every cell must contain its required nearest-gate distance, wall, or gate value.

## Constraints
- `m == rooms.length`
- `n == rooms[r].length`
- `1 <= m, n <= 250`
- `rooms[r][c]` is one of `-1`, `0`, or `2147483647`.

## Examples
```text
Input: rooms = [[2147483647,-1,0],[2147483647,2147483647,2147483647]]
Output: [[4,-1,0],[3,2,1]]
Explanation: The gate is at the top-right. Shortest paths around the wall give distances 4 and 3 in the first column, then 2 and 1 moving toward the gate on the second row.
```

## Understanding & Intuition
Shortest paths in an unweighted grid call for BFS. Starting from all gates avoids repeated searches.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List
from collections import deque

class Solution:
    def wallsAndGates(self, rooms: List[List[int]]) -> None:
        # Multi-source BFS from all gates.
        if not rooms or not rooms[0]:
            return
        m, n, q = len(rooms), len(rooms[0]), deque()
        for r in range(m):
            for c in range(n):
                if rooms[r][c] == 0:
                    q.append((r, c))
        while q:
            r, c = q.popleft()
            for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and rooms[nr][nc] == 2147483647:
                    rooms[nr][nc] = rooms[r][c] + 1
                    q.append((nr, nc))
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List
from collections import deque

class Solution:
    def wallsAndGates(self, rooms: List[List[int]]) -> None:
        # Multi-source BFS from all gates.
        if not rooms or not rooms[0]:
            return
        m, n, q = len(rooms), len(rooms[0]), deque()
        for r in range(m):
            for c in range(n):
                if rooms[r][c] == 0:
                    q.append((r, c))
        while q:
            r, c = q.popleft()
            for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and rooms[nr][nc] == 2147483647:
                    rooms[nr][nc] = rooms[r][c] + 1
                    q.append((nr, nc))
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List
from collections import deque

class Solution:
    def wallsAndGates(self, rooms: List[List[int]]) -> None:
        # Multi-source BFS from all gates.
        if not rooms or not rooms[0]:
            return
        m, n, q = len(rooms), len(rooms[0]), deque()
        for r in range(m):
            for c in range(n):
                if rooms[r][c] == 0:
                    q.append((r, c))
        while q:
            r, c = q.popleft()
            for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and rooms[nr][nc] == 2147483647:
                    rooms[nr][nc] = rooms[r][c] + 1
                    q.append((nr, nc))
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
