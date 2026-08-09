# 06. Pacific Atlantic Water Flow

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Google, Amazon, Meta, Microsoft

## Problem
There is an `m x n` rectangular island that borders **both** the Pacific and Atlantic oceans:

- The **Pacific Ocean** touches the island's **top** and **left** edges.
- The **Atlantic Ocean** touches the island's **bottom** and **right** edges.

You are given an `m x n` integer matrix `heights` where `heights[r][c]` is the height above sea
level of the cell at row `r`, column `c`.

Rain water can flow from a cell to any of its **4 direct neighbours** (up, down, left, right) if the
neighbour's height is **less than or equal to** the current cell's height. Water can flow off the
island into an ocean from any cell that sits on that ocean's edge.

Return **all** grid coordinates `[r, c]` from which rain water can flow to **both** the Pacific and
the Atlantic oceans.

**Input**
- `heights`: a 2-D list of integers (`m` rows × `n` columns).

**Output**
- A list of `[row, col]` pairs. **This judge compares exactly**, so return the coordinates
  **sorted in ascending order** — first by `row`, then by `col`. (e.g. `[0,1]` before `[0,2]` before
  `[1,0]`).

## Constraints
- `m == heights.length`
- `n == heights[r].length`
- `1 <= m, n <= 200`
- `0 <= heights[r][c] <= 10^5`

## Examples
```text
Input: heights = [[1,2,2],[3,2,3],[2,4,5]]
Output: [[0,1],[0,2],[1,0],[1,1],[1,2],[2,0],[2,1],[2,2]]
Explanation: Take cell [2,2] (height 5, the bottom-right corner): it already sits on the Atlantic
edge, and water can walk up/left along non-increasing heights (5 -> 3 -> 1 ...) to reach the Pacific
(top/left) edge, so it flows to both oceans. Every listed cell can likewise reach both the top/left
(Pacific) and bottom/right (Atlantic) borders. Corner [0,0] is Pacific-only and corner [m-1... ]
cells that cannot climb to the top/left are excluded. The result is sorted by (row, col).
```

```text
Input: heights = [[1]]
Output: [[0,0]]
Explanation: A single cell borders every edge, so it reaches both oceans.
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
