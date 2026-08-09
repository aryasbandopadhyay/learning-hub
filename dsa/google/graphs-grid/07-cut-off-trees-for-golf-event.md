# 07. Cut Off Trees for Golf Event

- **Difficulty:** Hard
- **Pattern:** Graph / BFS / DFS on Grids
- **Asked at:** Google

## Problem
You are given a forest grid. `0` is an obstacle, `1` is walkable ground, and any value greater than `1` is a tree height.

Starting at `(0,0)`, cut all trees in strictly increasing height order. You may move 4-directionally through ground or tree cells, but not obstacles. Return the minimum total steps, or `-1` if some tree cannot be reached.

**Input**
- `forest`: an `m x n` matrix of non-negative integers.

**Output**
- The minimum steps to cut all trees by ascending height, or `-1` if impossible.

## Constraints
- `1 <= m, n <= 50`
- `0 <= forest[r][c] <= 10^9`
- Tree heights greater than `1` are unique.

## Examples
```text
Input: forest = [[1,2,3],[0,0,4],[7,6,5]]
Output: 6
Explanation: The trees are visited in order `2,3,4,5,6,7`, and the shortest route following that order uses `6` steps.
```

## Understanding & Intuition
The tree order is fixed by height. Between two consecutive targets, every move has unit cost, so BFS gives the shortest segment. If any segment is unreachable, the total answer is impossible.

## Approach 1 — Naive / Brute Force
**Idea:** Use a simple list queue BFS for every target.
```python
class Solution:
    def cutOffTree(self, forest: list[list[int]]) -> int:
        m,n=len(forest),len(forest[0]); trees=sorted((forest[r][c],r,c) for r in range(m) for c in range(n) if forest[r][c]>1)
        def bfs(sr,sc,tr,tc):
            q=[(sr,sc,0)]; seen={(sr,sc)}; head=0
            while head<len(q):
                r,c,d=q[head]; head+=1
                if (r,c)==(tr,tc): return d
                for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                    nr,nc=r+dr,c+dc
                    if 0<=nr<m and 0<=nc<n and forest[nr][nc]!=0 and (nr,nc) not in seen:
                        seen.add((nr,nc)); q.append((nr,nc,d+1))
            return -1
        ans=0; r=c=0
        for _,tr,tc in trees:
            d=bfs(r,c,tr,tc)
            if d<0: return -1
            ans+=d; r,c=tr,tc
        return ans
```
- **Time:** O(Tmn) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** Use deque BFS for each fixed source-target pair.
```python
class Solution:
    def cutOffTree(self, forest: list[list[int]]) -> int:
        from collections import deque
        m,n=len(forest),len(forest[0]); trees=sorted((forest[r][c],r,c) for r in range(m) for c in range(n) if forest[r][c]>1)
        def bfs(start,target):
            q=deque([(start[0],start[1],0)]); seen={start}
            while q:
                r,c,d=q.popleft()
                if (r,c)==target: return d
                for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                    nr,nc=r+dr,c+dc
                    if 0<=nr<m and 0<=nc<n and forest[nr][nc]!=0 and (nr,nc) not in seen:
                        seen.add((nr,nc)); q.append((nr,nc,d+1))
            return -1
        ans=0; cur=(0,0)
        for _,r,c in trees:
            d=bfs(cur,(r,c))
            if d<0: return -1
            ans+=d; cur=(r,c)
        return ans
```
- **Time:** O(Tmn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** Use A* with Manhattan distance to focus each shortest-path search.
```python
class Solution:
    def cutOffTree(self, forest: list[list[int]]) -> int:
        import heapq
        m,n=len(forest),len(forest[0]); trees=sorted((forest[r][c],r,c) for r in range(m) for c in range(n) if forest[r][c]>1)
        def astar(start,target):
            tr,tc=target; pq=[(abs(start[0]-tr)+abs(start[1]-tc),0,start[0],start[1])]; best={start:0}
            while pq:
                _,d,r,c=heapq.heappop(pq)
                if (r,c)==target: return d
                if d!=best[(r,c)]: continue
                for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                    nr,nc=r+dr,c+dc; nd=d+1
                    if 0<=nr<m and 0<=nc<n and forest[nr][nc]!=0 and nd<best.get((nr,nc),10**9):
                        best[(nr,nc)]=nd; heapq.heappush(pq,(nd+abs(nr-tr)+abs(nc-tc),nd,nr,nc))
            return -1
        ans=0; cur=(0,0)
        for _,r,c in trees:
            d=astar(cur,(r,c))
            if d<0: return -1
            ans+=d; cur=(r,c)
        return ans
```
- **Time:** O(Tmn log(mn)) — **Space:** O(mn)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(Tmn) | O(mn) |
| Better | O(Tmn) | O(mn) |
| Optimal | O(Tmn log(mn)) | O(mn) |

## Edge Cases & Pitfalls
- Trees are cut by height, not by nearest distance.
- A blocked start makes non-start trees unreachable.
- Each tree cell is walkable after it is reached.

## Related
- Shortest Path in Binary Matrix
- Minimum Obstacle Removal to Reach Corner
