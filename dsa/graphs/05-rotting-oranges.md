# 05. Rotting Oranges

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Microsoft, Uber

## Problem
Each minute, rotten oranges rot adjacent fresh oranges. Return minutes until no fresh orange remains, or -1.

## Examples
```text
Input: grid = [[2,1,1],[1,1,0],[0,1,1]]
Output: 4
Explanation: Rot spreads one BFS layer per minute.
```

## Understanding & Intuition
All initially rotten oranges are simultaneous sources. BFS layers correspond to elapsed minutes.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List
from collections import deque

class Solution:
    def orangesRotting(self, grid: List[List[int]]) -> int:
        # Multi-source BFS with a fresh counter.
        m, n = len(grid), len(grid[0])
        q, fresh = deque(), 0
        for r in range(m):
            for c in range(n):
                if grid[r][c] == 2: q.append((r, c, 0))
                elif grid[r][c] == 1: fresh += 1
        mins = 0
        while q:
            r, c, mins = q.popleft()
            for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and grid[nr][nc] == 1:
                    grid[nr][nc] = 2; fresh -= 1
                    q.append((nr, nc, mins + 1))
        return mins if fresh == 0 else -1
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List
from collections import deque

class Solution:
    def orangesRotting(self, grid: List[List[int]]) -> int:
        # Multi-source BFS with a fresh counter.
        m, n = len(grid), len(grid[0])
        q, fresh = deque(), 0
        for r in range(m):
            for c in range(n):
                if grid[r][c] == 2: q.append((r, c, 0))
                elif grid[r][c] == 1: fresh += 1
        mins = 0
        while q:
            r, c, mins = q.popleft()
            for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and grid[nr][nc] == 1:
                    grid[nr][nc] = 2; fresh -= 1
                    q.append((nr, nc, mins + 1))
        return mins if fresh == 0 else -1
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List
from collections import deque

class Solution:
    def orangesRotting(self, grid: List[List[int]]) -> int:
        # Multi-source BFS with a fresh counter.
        m, n = len(grid), len(grid[0])
        q, fresh = deque(), 0
        for r in range(m):
            for c in range(n):
                if grid[r][c] == 2: q.append((r, c, 0))
                elif grid[r][c] == 1: fresh += 1
        mins = 0
        while q:
            r, c, mins = q.popleft()
            for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and grid[nr][nc] == 1:
                    grid[nr][nc] = 2; fresh -= 1
                    q.append((nr, nc, mins + 1))
        return mins if fresh == 0 else -1
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
