# 09. Minimum Obstacle Removal to Reach Corner

- **Difficulty:** Hard
- **Pattern:** Graph / BFS / DFS on Grids
- **Asked at:** Google

## Problem
Given a binary grid where `1` is an obstacle and `0` is free, return the minimum obstacles to remove to travel from `(0,0)` to `(m-1,n-1)` using 4-directional moves. Constraints: total cells up to `100000`.

## Examples
```text
Input: grid = [[0,1,1],[1,1,0],[1,1,0]]
Output: 2
Explanation: Removing two obstacles creates a route to the lower-right corner.
```

## Understanding & Intuition
Moving into a free cell costs zero and moving into an obstacle costs one. This is a shortest path on a grid with edge weights in `{0,1}`. Dijkstra works, and 0-1 BFS removes the logarithmic heap factor.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly relax all grid edges like Bellman-Ford.
```python
class Solution:
    def minimumObstacles(self, grid: list[list[int]]) -> int:
        m,n=len(grid),len(grid[0]); inf=10**9; dist=[[inf]*n for _ in range(m)]; dist[0][0]=0
        for _ in range(m*n-1):
            changed=False
            for r in range(m):
                for c in range(n):
                    if dist[r][c]==inf: continue
                    for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                        nr,nc=r+dr,c+dc
                        if 0<=nr<m and 0<=nc<n and dist[r][c]+grid[nr][nc]<dist[nr][nc]:
                            dist[nr][nc]=dist[r][c]+grid[nr][nc]; changed=True
            if not changed: break
        return dist[m-1][n-1]
```
- **Time:** O((mn)^2) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Use Dijkstra with a min-heap.
```python
class Solution:
    def minimumObstacles(self, grid: list[list[int]]) -> int:
        import heapq
        m,n=len(grid),len(grid[0]); dist=[[10**9]*n for _ in range(m)]; dist[0][0]=0; pq=[(0,0,0)]
        while pq:
            d,r,c=heapq.heappop(pq)
            if d!=dist[r][c]: continue
            if (r,c)==(m-1,n-1): return d
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if 0<=nr<m and 0<=nc<n:
                    nd=d+grid[nr][nc]
                    if nd<dist[nr][nc]: dist[nr][nc]=nd; heapq.heappush(pq,(nd,nr,nc))
        return -1
```
- **Time:** O(mn log(mn)) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** Use 0-1 BFS, putting zero-cost moves at the front of the deque.
```python
class Solution:
    def minimumObstacles(self, grid: list[list[int]]) -> int:
        from collections import deque
        m,n=len(grid),len(grid[0]); dist=[[10**9]*n for _ in range(m)]; dist[0][0]=0; dq=deque([(0,0)])
        while dq:
            r,c=dq.popleft()
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if 0<=nr<m and 0<=nc<n:
                    w=grid[nr][nc]; nd=dist[r][c]+w
                    if nd<dist[nr][nc]:
                        dist[nr][nc]=nd
                        if w: dq.append((nr,nc))
                        else: dq.appendleft((nr,nc))
        return dist[m-1][n-1]
```
- **Time:** O(mn) — **Space:** O(mn)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((mn)^2) | O(mn) |
| Better | O(mn log(mn)) | O(mn) |
| Optimal | O(mn) | O(mn) |

## Edge Cases & Pitfalls
- Do not pay for the starting cell.
- Plain BFS is wrong because costs differ.
- A zero-cost edge should be processed before cost-one edges.

## Related
- Cut Off Trees for Golf Event
- Shortest Path in Binary Matrix
