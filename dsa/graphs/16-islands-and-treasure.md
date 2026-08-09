# 16. Islands and Treasure

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
You are given a grid where `-1` is an obstacle, `0` is a treasure, and `2147483647` is empty land.

For each land cell, write the shortest 4-directional distance to a treasure. Obstacles cannot be crossed, and unreachable land remains `2147483647`. Modify `grid` in place and return the updated grid for the judge.

**Input**
- `grid`: an `m x n` integer grid containing `-1`, `0`, or `2147483647`.

**Output**
- The final distance grid. **This judge compares exactly**, so every cell must match.

## Constraints
- `m == grid.length`
- `n == grid[r].length`
- `1 <= m, n <= 250`
- `grid[r][c]` is one of `-1`, `0`, or `2147483647`.

## Examples
```text
Input: grid = [[2147483647,-1,0],[2147483647,2147483647,2147483647]]
Output: [[4,-1,0],[3,2,1]]
Explanation: The treasure at the top-right is the only source. Distances spread around the obstacle one step at a time, producing the shown shortest-path values.
```

## Understanding & Intuition
This is identical to multi-source shortest path on a grid: treasures are sources.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List
from collections import deque

class Solution:
    def islandsAndTreasure(self, grid: List[List[int]]) -> None:
        # Multi-source BFS from treasures.
        m, n, q = len(grid), len(grid[0]), deque()
        for r in range(m):
            for c in range(n):
                if grid[r][c] == 0: q.append((r,c))
        while q:
            r, c = q.popleft()
            for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and grid[nr][nc] == 2147483647:
                    grid[nr][nc] = grid[r][c] + 1
                    q.append((nr,nc))
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List
from collections import deque

class Solution:
    def islandsAndTreasure(self, grid: List[List[int]]) -> None:
        # Multi-source BFS from treasures.
        m, n, q = len(grid), len(grid[0]), deque()
        for r in range(m):
            for c in range(n):
                if grid[r][c] == 0: q.append((r,c))
        while q:
            r, c = q.popleft()
            for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and grid[nr][nc] == 2147483647:
                    grid[nr][nc] = grid[r][c] + 1
                    q.append((nr,nc))
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List
from collections import deque

class Solution:
    def islandsAndTreasure(self, grid: List[List[int]]) -> None:
        # Multi-source BFS from treasures.
        m, n, q = len(grid), len(grid[0]), deque()
        for r in range(m):
            for c in range(n):
                if grid[r][c] == 0: q.append((r,c))
        while q:
            r, c = q.popleft()
            for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and grid[nr][nc] == 2147483647:
                    grid[nr][nc] = grid[r][c] + 1
                    q.append((nr,nc))
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
