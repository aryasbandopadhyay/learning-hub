# 01. Shortest Path in Binary Matrix

- **Difficulty:** Medium
- **Pattern:** Graph / BFS / DFS on Grids
- **Asked at:** Google

## Problem
Given an `n x n` binary grid, return the length of the shortest clear path from `(0,0)` to `(n-1,n-1)`. Clear cells are `0`, blocked cells are `1`, and movement is allowed in 8 directions. Return `-1` if no clear path exists. Constraints: `1 <= n <= 100`.

## Examples
```text
Input: grid = [[0,1],[1,0]]
Output: 2
Explanation: The path uses the diagonal move from start to finish.
```

## Understanding & Intuition
Each open cell is a graph node with up to eight neighbors. Since every move costs one, the shortest valid route is found by expanding cells by distance. The start and target must both be open.

## Approach 1 — Naive / Brute Force
**Idea:** Use Dijkstra even though all edges have weight one.
```python
class Solution:
    def shortestPathBinaryMatrix(self, grid: list[list[int]]) -> int:
        import heapq
        n=len(grid)
        if grid[0][0] or grid[n-1][n-1]: return -1
        dist=[[10**9]*n for _ in range(n)]; dist[0][0]=1
        pq=[(1,0,0)]; dirs=[(a,b) for a in (-1,0,1) for b in (-1,0,1) if a or b]
        while pq:
            d,r,c=heapq.heappop(pq)
            if d!=dist[r][c]: continue
            if (r,c)==(n-1,n-1): return d
            for dr,dc in dirs:
                nr,nc=r+dr,c+dc
                if 0<=nr<n and 0<=nc<n and grid[nr][nc]==0 and d+1<dist[nr][nc]:
                    dist[nr][nc]=d+1; heapq.heappush(pq,(d+1,nr,nc))
        return -1
```
- **Time:** O(n^2 log n) — **Space:** O(n^2)

## Approach 2 — Better
**Idea:** Use BFS with a separate distance matrix.
```python
class Solution:
    def shortestPathBinaryMatrix(self, grid: list[list[int]]) -> int:
        from collections import deque
        n=len(grid)
        if grid[0][0] or grid[n-1][n-1]: return -1
        dist=[[0]*n for _ in range(n)]; dist[0][0]=1
        q=deque([(0,0)]); dirs=[(a,b) for a in (-1,0,1) for b in (-1,0,1) if a or b]
        while q:
            r,c=q.popleft()
            if (r,c)==(n-1,n-1): return dist[r][c]
            for dr,dc in dirs:
                nr,nc=r+dr,c+dc
                if 0<=nr<n and 0<=nc<n and grid[nr][nc]==0 and dist[nr][nc]==0:
                    dist[nr][nc]=dist[r][c]+1; q.append((nr,nc))
        return -1
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** BFS while storing distances in a copied grid.
```python
class Solution:
    def shortestPathBinaryMatrix(self, grid: list[list[int]]) -> int:
        from collections import deque
        n=len(grid)
        if grid[0][0] or grid[n-1][n-1]: return -1
        g=[row[:] for row in grid]; g[0][0]=1
        q=deque([(0,0)]); dirs=[(a,b) for a in (-1,0,1) for b in (-1,0,1) if a or b]
        while q:
            r,c=q.popleft()
            if (r,c)==(n-1,n-1): return g[r][c]
            for dr,dc in dirs:
                nr,nc=r+dr,c+dc
                if 0<=nr<n and 0<=nc<n and g[nr][nc]==0:
                    g[nr][nc]=g[r][c]+1; q.append((nr,nc))
        return -1
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2 log n) | O(n^2) |
| Better | O(n^2) | O(n^2) |
| Optimal | O(n^2) | O(n^2) |

## Edge Cases & Pitfalls
- Return `-1` if start or finish is blocked.
- A one-cell open grid returns `1`.
- Diagonal moves are allowed.

## Related
- Nearest Exit from Entrance in Maze
- Minimum Obstacle Removal to Reach Corner
