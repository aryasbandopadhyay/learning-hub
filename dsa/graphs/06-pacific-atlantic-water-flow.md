# 06. Pacific Atlantic Water Flow

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Google, Amazon, Meta, Microsoft

## Problem
Return cells where water can flow to both oceans. Water moves to lower/equal adjacent heights. Constraints: 1 <= m,n <= 200.

## Examples
```text
Input: heights = [[1,2,2],[3,2,3],[2,4,5]]
Output: [[0,1],[0,2],[1,0],[1,1],[1,2],[2,0],[2,1],[2,2]]
Explanation: These cells can reach both borders.
```

## Understanding & Intuition
Reverse the flow: start at oceans and move uphill. The intersection of reachable sets is the answer.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List
from collections import deque

class Solution:
    def pacificAtlantic(self, heights: List[List[int]]) -> List[List[int]]:
        # Reverse-flow BFS from both ocean borders.
        m, n = len(heights), len(heights[0])
        def bfs(starts):
            seen, q = set(starts), deque(starts)
            while q:
                r, c = q.popleft()
                for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                    nr, nc = r + dr, c + dc
                    if 0 <= nr < m and 0 <= nc < n and (nr,nc) not in seen and heights[nr][nc] >= heights[r][c]:
                        seen.add((nr,nc)); q.append((nr,nc))
            return seen
        pac = bfs([(r,0) for r in range(m)] + [(0,c) for c in range(n)])
        atl = bfs([(r,n-1) for r in range(m)] + [(m-1,c) for c in range(n)])
        return [[r,c] for r,c in sorted(pac & atl)]
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List
from collections import deque

class Solution:
    def pacificAtlantic(self, heights: List[List[int]]) -> List[List[int]]:
        # Reverse-flow BFS from both ocean borders.
        m, n = len(heights), len(heights[0])
        def bfs(starts):
            seen, q = set(starts), deque(starts)
            while q:
                r, c = q.popleft()
                for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                    nr, nc = r + dr, c + dc
                    if 0 <= nr < m and 0 <= nc < n and (nr,nc) not in seen and heights[nr][nc] >= heights[r][c]:
                        seen.add((nr,nc)); q.append((nr,nc))
            return seen
        pac = bfs([(r,0) for r in range(m)] + [(0,c) for c in range(n)])
        atl = bfs([(r,n-1) for r in range(m)] + [(m-1,c) for c in range(n)])
        return [[r,c] for r,c in sorted(pac & atl)]
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List
from collections import deque

class Solution:
    def pacificAtlantic(self, heights: List[List[int]]) -> List[List[int]]:
        # Reverse-flow BFS from both ocean borders.
        m, n = len(heights), len(heights[0])
        def bfs(starts):
            seen, q = set(starts), deque(starts)
            while q:
                r, c = q.popleft()
                for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                    nr, nc = r + dr, c + dc
                    if 0 <= nr < m and 0 <= nc < n and (nr,nc) not in seen and heights[nr][nc] >= heights[r][c]:
                        seen.add((nr,nc)); q.append((nr,nc))
            return seen
        pac = bfs([(r,0) for r in range(m)] + [(0,c) for c in range(n)])
        atl = bfs([(r,n-1) for r in range(m)] + [(m-1,c) for c in range(n)])
        return [[r,c] for r,c in sorted(pac & atl)]
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
