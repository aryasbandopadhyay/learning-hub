# 03. As Far from Land as Possible

- **Difficulty:** Medium
- **Pattern:** Graph / BFS / DFS on Grids
- **Asked at:** Google

## Problem
Given an `n x n` grid where `1` is land and `0` is water, return the maximum distance from any water cell to its nearest land cell using 4-directional moves. Return `-1` if all cells are land or all are water. Constraints: `1 <= n <= 100`.

## Examples
```text
Input: grid = [[1,0,1],[0,0,0],[1,0,1]]
Output: 2
Explanation: The center water cell is distance two from the nearest land.
```

## Understanding & Intuition
The answer is the largest nearest-land distance among water cells. Running BFS from each water cell is correct but wasteful. Starting from all land cells at once computes every nearest-land distance in one expansion.

## Approach 1 — Naive / Brute Force
**Idea:** BFS from each water cell until land is found.
```python
class Solution:
    def maxDistance(self, grid: list[list[int]]) -> int:
        from collections import deque
        n=len(grid); land=sum(map(sum,grid))
        if land==0 or land==n*n: return -1
        ans=0
        for sr in range(n):
            for sc in range(n):
                if grid[sr][sc]: continue
                q=deque([(sr,sc,0)]); seen={(sr,sc)}
                while q:
                    r,c,d=q.popleft()
                    if grid[r][c]: ans=max(ans,d); break
                    for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                        nr,nc=r+dr,c+dc
                        if 0<=nr<n and 0<=nc<n and (nr,nc) not in seen:
                            seen.add((nr,nc)); q.append((nr,nc,d+1))
        return ans
```
- **Time:** O(n^4) — **Space:** O(n^2)

## Approach 2 — Better
**Idea:** Run multi-source BFS from all land cells.
```python
class Solution:
    def maxDistance(self, grid: list[list[int]]) -> int:
        from collections import deque
        n=len(grid); dist=[[-1]*n for _ in range(n)]; q=deque()
        for r in range(n):
            for c in range(n):
                if grid[r][c]: dist[r][c]=0; q.append((r,c))
        if not q or len(q)==n*n: return -1
        ans=0
        while q:
            r,c=q.popleft(); ans=max(ans,dist[r][c])
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if 0<=nr<n and 0<=nc<n and dist[nr][nc]<0:
                    dist[nr][nc]=dist[r][c]+1; q.append((nr,nc))
        return ans
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Use two sweeps to compute nearest-land distance.
```python
class Solution:
    def maxDistance(self, grid: list[list[int]]) -> int:
        n=len(grid); land=sum(map(sum,grid))
        if land==0 or land==n*n: return -1
        inf=2*n+5; dp=[[0 if grid[r][c] else inf for c in range(n)] for r in range(n)]
        for r in range(n):
            for c in range(n):
                if r: dp[r][c]=min(dp[r][c],dp[r-1][c]+1)
                if c: dp[r][c]=min(dp[r][c],dp[r][c-1]+1)
        ans=0
        for r in range(n-1,-1,-1):
            for c in range(n-1,-1,-1):
                if r+1<n: dp[r][c]=min(dp[r][c],dp[r+1][c]+1)
                if c+1<n: dp[r][c]=min(dp[r][c],dp[r][c+1]+1)
                if grid[r][c]==0: ans=max(ans,dp[r][c])
        return ans
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^4) | O(n^2) |
| Better | O(n^2) | O(n^2) |
| Optimal | O(n^2) | O(n^2) |

## Edge Cases & Pitfalls
- All land or all water returns `-1`.
- Use 4-directional distance.
- Enqueue every land cell before BFS expansion.

## Related
- 01 Matrix
- Map of Highest Peak
