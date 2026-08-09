# 12. Minimum Days to Disconnect Island

- **Difficulty:** Hard
- **Pattern:** Graph / BFS / DFS on Grids
- **Asked at:** Google

## Problem
You are given an `m x n` binary grid where `1` is land and `0` is water. An island is a 4-directionally connected group of land cells.

Each day, change one land cell into water. Return the minimum days needed until the grid is disconnected, meaning it has zero islands or more than one island.

**Input**
- `grid`: an `m x n` matrix containing only `0` and `1`.

**Output**
- The minimum number of land cells to remove to disconnect the island.

## Constraints
- `1 <= m, n <= 30`
- `grid[r][c]` is `0` or `1`

## Examples
```text
Input: grid = [[1,1],[1,1]]
Output: 2
Explanation: A full `2 x 2` island remains connected after any one removal, but two removals can disconnect it, so the answer is `2`.
```

## Understanding & Intuition
The answer is always `0`, `1`, or `2`. Brute force counts islands after removing each possible land cell. A faster view treats land cells as a graph: if the single island has an articulation point, one removal is enough; otherwise two are enough.

## Approach 1 — Naive / Brute Force
**Idea:** Count islands initially, then after removing each land cell.
```python
class Solution:
    def minDays(self, grid: list[list[int]]) -> int:
        m,n=len(grid),len(grid[0])
        def count(g):
            seen=set(); comps=0
            for r in range(m):
                for c in range(n):
                    if g[r][c] and (r,c) not in seen:
                        comps+=1; stack=[(r,c)]; seen.add((r,c))
                        while stack:
                            x,y=stack.pop()
                            for dx,dy in ((1,0),(-1,0),(0,1),(0,-1)):
                                nx,ny=x+dx,y+dy
                                if 0<=nx<m and 0<=ny<n and g[nx][ny] and (nx,ny) not in seen:
                                    seen.add((nx,ny)); stack.append((nx,ny))
            return comps
        if count(grid)!=1: return 0
        for r in range(m):
            for c in range(n):
                if grid[r][c]:
                    grid[r][c]=0
                    if count(grid)!=1: grid[r][c]=1; return 1
                    grid[r][c]=1
        return 2
```
- **Time:** O((mn)^2) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Use Tarjan low-link values to find an articulation land cell.
```python
class Solution:
    def minDays(self, grid: list[list[int]]) -> int:
        m,n=len(grid),len(grid[0]); land=[(r,c) for r in range(m) for c in range(n) if grid[r][c]]
        if len(land)<=1: return len(land)
        disc=[[-1]*n for _ in range(m)]; low=[[0]*n for _ in range(m)]; t=[0]; seen=[0]; art=[False]
        def dfs(r,c,pr,pc):
            disc[r][c]=low[r][c]=t[0]; t[0]+=1; seen[0]+=1; child=0
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if not (0<=nr<m and 0<=nc<n) or grid[nr][nc]==0 or (nr,nc)==(pr,pc): continue
                if disc[nr][nc]<0:
                    child+=1; dfs(nr,nc,r,c); low[r][c]=min(low[r][c],low[nr][nc])
                    if (pr!=-1 and low[nr][nc]>=disc[r][c]) or (pr==-1 and child>1): art[0]=True
                else: low[r][c]=min(low[r][c],disc[nr][nc])
        dfs(land[0][0],land[0][1],-1,-1)
        if seen[0]!=len(land): return 0
        return 1 if art[0] else 2
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** Flatten land cells into ids and run articulation detection on the compact graph.
```python
class Solution:
    def minDays(self, grid: list[list[int]]) -> int:
        m,n=len(grid),len(grid[0]); ids={};
        for r in range(m):
            for c in range(n):
                if grid[r][c]: ids[(r,c)]=len(ids)
        total=len(ids)
        if total==0: return 0
        def comps():
            seen=set(); ans=0
            for cell in ids:
                if cell not in seen:
                    ans+=1; stack=[cell]; seen.add(cell)
                    while stack:
                        r,c=stack.pop()
                        for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                            nb=(r+dr,c+dc)
                            if nb in ids and nb not in seen: seen.add(nb); stack.append(nb)
            return ans
        if comps()!=1: return 0
        if total<=2: return 1
        disc=[-1]*total; low=[0]*total; tick=[0]; art=[False]
        def neigh(cell):
            r,c=cell
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nb=(r+dr,c+dc)
                if nb in ids: yield nb
        def dfs(cell,parent):
            u=ids[cell]; disc[u]=low[u]=tick[0]; tick[0]+=1; child=0
            for nb in neigh(cell):
                v=ids[nb]
                if v==parent: continue
                if disc[v]<0:
                    child+=1; dfs(nb,u); low[u]=min(low[u],low[v])
                    if (parent!=-1 and low[v]>=disc[u]) or (parent==-1 and child>1): art[0]=True
                else: low[u]=min(low[u],disc[v])
        dfs(next(iter(ids)),-1)
        return 1 if art[0] else 2
```
- **Time:** O(mn) — **Space:** O(mn)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((mn)^2) | O(mn) |
| Better | O(mn) | O(mn) |
| Optimal | O(mn) | O(mn) |

## Edge Cases & Pitfalls
- Zero islands is already disconnected.
- One or two land cells need one day.
- Root articulation logic is different from non-root logic.

## Related
- Number of Enclaves
- Making a Large Island
