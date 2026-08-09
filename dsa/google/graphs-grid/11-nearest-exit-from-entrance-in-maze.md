# 11. Nearest Exit from Entrance in Maze

- **Difficulty:** Medium
- **Pattern:** Graph / BFS / DFS on Grids
- **Asked at:** Google

## Problem
You are given a maze of open cells and walls. Open cells are `'.'`, walls are `'+'`, and `entrance = [row, col]` is your starting open cell.

In one step, move up, down, left, or right to another open cell. An exit is an open boundary cell other than the entrance itself. Return the fewest steps to any exit, or `-1` if none is reachable.

**Input**
- `maze`: an `m x n` grid of characters `'.'` and `'+'`.
- `entrance`: the starting coordinate `[row, col]`.

**Output**
- The minimum number of 4-directional steps to an exit, or `-1` if no exit is reachable.

## Constraints
- `1 <= m, n <= 100`
- `maze[r][c]` is `'.'` or `'+'`
- `0 <= entrance[0] < m`
- `0 <= entrance[1] < n`
- `maze[entrance[0]][entrance[1]] == '.'`

## Examples
```text
Input: maze = [['+','+','.','+'],['.','.','.','+'],['+','+','+','.']], entrance = [1,2]
Output: 1
Explanation: From the entrance, one move reaches an open boundary cell. The starting cell itself would not count as an exit.
```

## Understanding & Intuition
The maze is an unweighted grid graph. DFS can try every route with pruning, but BFS naturally returns the first and therefore nearest exit. The entrance itself is excluded even if it lies on the boundary.

## Approach 1 — Naive / Brute Force
**Idea:** DFS all simple paths while pruning paths no better than the best found.
```python
class Solution:
    def nearestExit(self, maze: list[list[str]], entrance: list[int]) -> int:
        m,n=len(maze),len(maze[0]); sr,sc=entrance; best=[10**9]; seen=set()
        def is_exit(r,c): return (r,c)!=(sr,sc) and (r in (0,m-1) or c in (0,n-1))
        def dfs(r,c,d):
            if d>=best[0]: return
            if is_exit(r,c): best[0]=d; return
            seen.add((r,c))
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if 0<=nr<m and 0<=nc<n and maze[nr][nc]=='.' and (nr,nc) not in seen: dfs(nr,nc,d+1)
            seen.remove((r,c))
        dfs(sr,sc,0)
        return -1 if best[0]==10**9 else best[0]
```
- **Time:** O(4^(mn)) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** BFS with an explicit visited set.
```python
class Solution:
    def nearestExit(self, maze: list[list[str]], entrance: list[int]) -> int:
        from collections import deque
        m,n=len(maze),len(maze[0]); sr,sc=entrance; q=deque([(sr,sc,0)]); seen={(sr,sc)}
        while q:
            r,c,d=q.popleft()
            if (r,c)!=(sr,sc) and (r in (0,m-1) or c in (0,n-1)): return d
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if 0<=nr<m and 0<=nc<n and maze[nr][nc]=='.' and (nr,nc) not in seen: seen.add((nr,nc)); q.append((nr,nc,d+1))
        return -1
```
- **Time:** O(mn) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** BFS while marking visited cells in a copied maze.
```python
class Solution:
    def nearestExit(self, maze: list[list[str]], entrance: list[int]) -> int:
        from collections import deque
        maze=[row[:] for row in maze]; m,n=len(maze),len(maze[0]); sr,sc=entrance; maze[sr][sc]='+'; q=deque([(sr,sc,0)])
        while q:
            r,c,d=q.popleft()
            for dr,dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr,nc=r+dr,c+dc
                if 0<=nr<m and 0<=nc<n and maze[nr][nc]=='.':
                    if nr in (0,m-1) or nc in (0,n-1): return d+1
                    maze[nr][nc]='+'; q.append((nr,nc,d+1))
        return -1
```
- **Time:** O(mn) — **Space:** O(mn)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(4^(mn)) | O(mn) |
| Better | O(mn) | O(mn) |
| Optimal | O(mn) | O(mn) |

## Edge Cases & Pitfalls
- The entrance is not an exit.
- Mark cells visited when enqueuing.
- Walls are never traversable.

## Related
- Escape the Spreading Fire
- Shortest Path in Binary Matrix
