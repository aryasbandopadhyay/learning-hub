# 15. Flood Fill

- **Difficulty:** Easy
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Apple, Microsoft

## Problem
Recolor the component containing (sr, sc) with the same original color. Constraints: 1 <= m,n <= 50.

## Examples
```text
Input: image = [[1,1,1],[1,1,0],[1,0,1]], sr = 1, sc = 1, color = 2
Output: [[2,2,2],[2,2,0],[2,0,1]]
Explanation: Connected 1s from the start are recolored.
```

## Understanding & Intuition
Flood fill is graph traversal from one pixel. Only neighbors with the original color belong to the component.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List

class Solution:
    def floodFill(self, image: List[List[int]], sr: int, sc: int, color: int) -> List[List[int]]:
        # In-place DFS with same-color guard.
        old = image[sr][sc]
        if old == color: return image
        m, n = len(image), len(image[0])
        def dfs(r, c):
            if r < 0 or c < 0 or r == m or c == n or image[r][c] != old:
                return
            image[r][c] = color
            dfs(r+1,c); dfs(r-1,c); dfs(r,c+1); dfs(r,c-1)
        dfs(sr, sc)
        return image
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List

class Solution:
    def floodFill(self, image: List[List[int]], sr: int, sc: int, color: int) -> List[List[int]]:
        # In-place DFS with same-color guard.
        old = image[sr][sc]
        if old == color: return image
        m, n = len(image), len(image[0])
        def dfs(r, c):
            if r < 0 or c < 0 or r == m or c == n or image[r][c] != old:
                return
            image[r][c] = color
            dfs(r+1,c); dfs(r-1,c); dfs(r,c+1); dfs(r,c-1)
        dfs(sr, sc)
        return image
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List

class Solution:
    def floodFill(self, image: List[List[int]], sr: int, sc: int, color: int) -> List[List[int]]:
        # In-place DFS with same-color guard.
        old = image[sr][sc]
        if old == color: return image
        m, n = len(image), len(image[0])
        def dfs(r, c):
            if r < 0 or c < 0 or r == m or c == n or image[r][c] != old:
                return
            image[r][c] = color
            dfs(r+1,c); dfs(r-1,c); dfs(r,c+1); dfs(r,c-1)
        dfs(sr, sc)
        return image
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
