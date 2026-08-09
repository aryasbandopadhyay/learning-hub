# 08. Escape the Spreading Fire

- **Difficulty:** Hard
- **Pattern:** Graph / BFS / DFS on Grids
- **Asked at:** Google

## Problem
You are given a grid where `0` is grass, `1` is fire, and `2` is a wall. You start at the top-left cell and need to reach the bottom-right safehouse.

You may wait at the start, then each minute you move 4-directionally or stay in place while fire spreads to adjacent grass cells. Walls block both movement and fire. Return the maximum initial waiting time that still allows escape, `10^9` if waiting forever is possible, or `-1` if escape is impossible even immediately.

**Input**
- `grid`: an `m x n` matrix with values `0`, `1`, and `2`.

**Output**
- The largest safe wait time, `10^9` for unbounded waiting, or `-1` if no escape route exists.

## Constraints
- `2 <= m, n <= 300`
- `grid[r][c]` is `0`, `1`, or `2`
- `grid[0][0] == 0`
- `grid[m-1][n-1] == 0`

## Examples
```text
Input: grid = [[0,2,0,0,0,0,0],[0,0,0,2,2,1,0],[0,2,0,0,1,2,0],[0,0,2,2,2,0,2],[0,0,0,0,0,0,0]]
Output: 3
Explanation: Waiting `3` minutes still leaves a safe route to the bottom-right cell. Waiting longer lets the fire cut off the route.
```

## Understanding & Intuition
Fire arrival times are independent of your route, so compute them once with multi-source BFS. For a chosen wait time, another BFS checks whether you can enter each cell strictly before fire, except the safehouse where arriving at the same time is allowed. Feasibility is monotonic in the wait time.

## Approach 1 — Naive / Brute Force
**Idea:** Try every wait from `0` upward using BFS feasibility.
```python
class Solution:
    def maximumMinutes(self, grid: list[list[int]]) -> int:
        from collections import deque
        m,n=len(grid),len(grid[0]); INF=10**9
        fire=[[INF]*n for _ in range(m)]; q=deque()
        for r in range(m):
            for c in range(n):
                if grid[r][c]==1: fire[r][c]=0; q.append((r,c))
        while q:
            r,c=q.popleft()
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if 0<=nr<m and 0<=nc<n and grid[nr][nc]!=2 and fire[nr][nc]==INF:
                    fire[nr][nc]=fire[r][c]+1; q.append((nr,nc))
        def can(wait):
            if wait>=fire[0][0]: return False
            q=deque([(0,0,wait)]); seen={(0,0)}
            while q:
                r,c,t=q.popleft()
                if (r,c)==(m-1,n-1): return t<=fire[r][c]
                for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                    nr,nc=r+dr,c+dc; nt=t+1
                    if 0<=nr<m and 0<=nc<n and grid[nr][nc]!=2 and (nr,nc) not in seen:
                        if ((nr,nc)==(m-1,n-1) and nt<=fire[nr][nc]) or ((nr,nc)!=(m-1,n-1) and nt<fire[nr][nc]):
                            seen.add((nr,nc)); q.append((nr,nc,nt))
            return False
        best=-1
        for w in range(m*n+1):
            if can(w): best=w
            else: break
        return 1000000000 if best==m*n else best
```
- **Time:** O((mn)^2) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Binary search the maximum feasible wait after precomputing fire times.
```python
class Solution:
    def maximumMinutes(self, grid: list[list[int]]) -> int:
        from collections import deque
        m,n=len(grid),len(grid[0]); INF=10**9; fire=[[INF]*n for _ in range(m)]; q=deque()
        for r in range(m):
            for c in range(n):
                if grid[r][c]==1: fire[r][c]=0; q.append((r,c))
        while q:
            r,c=q.popleft()
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if 0<=nr<m and 0<=nc<n and grid[nr][nc]!=2 and fire[nr][nc]==INF:
                    fire[nr][nc]=fire[r][c]+1; q.append((nr,nc))
        def can(wait):
            if wait>=fire[0][0]: return False
            q=deque([(0,0,wait)]); seen=[[False]*n for _ in range(m)]; seen[0][0]=True
            while q:
                r,c,t=q.popleft()
                if (r,c)==(m-1,n-1): return t<=fire[r][c]
                for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                    nr,nc=r+dr,c+dc; nt=t+1
                    if 0<=nr<m and 0<=nc<n and grid[nr][nc]!=2 and not seen[nr][nc]:
                        ok=nt<=fire[nr][nc] if (nr,nc)==(m-1,n-1) else nt<fire[nr][nc]
                        if ok: seen[nr][nc]=True; q.append((nr,nc,nt))
            return False
        lo,hi=-1,m*n
        while lo<hi:
            mid=(lo+hi+1)//2
            if can(mid): lo=mid
            else: hi=mid-1
        return 1000000000 if lo==m*n else lo
```
- **Time:** O(mn log(mn)) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** Binary search up to the official forever sentinel and check BFS by layers.
```python
class Solution:
    def maximumMinutes(self, grid: list[list[int]]) -> int:
        from collections import deque
        m,n=len(grid),len(grid[0]); INF=10**9; fire=[[INF]*n for _ in range(m)]; q=deque(); dirs=((1,0),(-1,0),(0,1),(0,-1))
        for r,row in enumerate(grid):
            for c,val in enumerate(row):
                if val==1: fire[r][c]=0; q.append((r,c))
        while q:
            r,c=q.popleft()
            for dr,dc in dirs:
                nr,nc=r+dr,c+dc
                if 0<=nr<m and 0<=nc<n and grid[nr][nc]!=2 and fire[nr][nc]==INF:
                    fire[nr][nc]=fire[r][c]+1; q.append((nr,nc))
        def ok(wait):
            if wait>=fire[0][0]: return False
            q=deque([(0,0)]); seen=[[False]*n for _ in range(m)]; seen[0][0]=True; t=wait
            while q:
                for _ in range(len(q)):
                    r,c=q.popleft()
                    if (r,c)==(m-1,n-1): return t<=fire[r][c]
                    for dr,dc in dirs:
                        nr,nc=r+dr,c+dc; nt=t+1
                        if 0<=nr<m and 0<=nc<n and grid[nr][nc]!=2 and not seen[nr][nc]:
                            safe=nt<=fire[nr][nc] if (nr,nc)==(m-1,n-1) else nt<fire[nr][nc]
                            if safe: seen[nr][nc]=True; q.append((nr,nc))
                t+=1
            return False
        lo,hi=-1,1000000000
        while lo<hi:
            mid=(lo+hi+1)//2
            if ok(mid): lo=mid
            else: hi=mid-1
        return lo
```
- **Time:** O(mn log 10^9) — **Space:** O(mn)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((mn)^2) | O(mn) |
| Better | O(mn log(mn)) | O(mn) |
| Optimal | O(mn log 10^9) | O(mn) |

## Edge Cases & Pitfalls
- The safehouse allows a tie with fire; intermediate cells do not.
- Walls block both fire and the player.
- If a wait of `1000000000` works, return exactly that sentinel.

## Related
- Nearest Exit from Entrance in Maze
- Minimum Obstacle Removal to Reach Corner
