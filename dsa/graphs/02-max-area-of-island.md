# 02. Max Area of Island

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Bloomberg, Microsoft

## Problem
Given a binary grid, return the largest 4-directionally connected land area. Constraints: 1 <= m,n <= 50.

## Examples
```text
Input: grid = [[0,1,0],[1,1,0],[0,0,1]]
Output: 3
Explanation: The largest island has three cells.
```

## Understanding & Intuition
Area is component size. Efficient solutions ensure every cell is counted once.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List

class Solution:
    def maxAreaOfIsland(self, grid: List[List[int]]) -> int:
        # In-place DFS counts and erases one island.
        m, n = len(grid), len(grid[0])
        def dfs(r, c):
            if r < 0 or c < 0 or r == m or c == n or grid[r][c] == 0:
                return 0
            grid[r][c] = 0
            return 1 + dfs(r+1,c) + dfs(r-1,c) + dfs(r,c+1) + dfs(r,c-1)
        best = 0
        for r in range(m):
            for c in range(n):
                if grid[r][c]:
                    best = max(best, dfs(r, c))
        return best
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List

class Solution:
    def maxAreaOfIsland(self, grid: List[List[int]]) -> int:
        # In-place DFS counts and erases one island.
        m, n = len(grid), len(grid[0])
        def dfs(r, c):
            if r < 0 or c < 0 or r == m or c == n or grid[r][c] == 0:
                return 0
            grid[r][c] = 0
            return 1 + dfs(r+1,c) + dfs(r-1,c) + dfs(r,c+1) + dfs(r,c-1)
        best = 0
        for r in range(m):
            for c in range(n):
                if grid[r][c]:
                    best = max(best, dfs(r, c))
        return best
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List

class Solution:
    def maxAreaOfIsland(self, grid: List[List[int]]) -> int:
        # In-place DFS counts and erases one island.
        m, n = len(grid), len(grid[0])
        def dfs(r, c):
            if r < 0 or c < 0 or r == m or c == n or grid[r][c] == 0:
                return 0
            grid[r][c] = 0
            return 1 + dfs(r+1,c) + dfs(r-1,c) + dfs(r,c+1) + dfs(r,c-1)
        best = 0
        for r in range(m):
            for c in range(n):
                if grid[r][c]:
                    best = max(best, dfs(r, c))
        return best
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
