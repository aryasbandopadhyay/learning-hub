# 15. Flood Fill

- **Difficulty:** Easy
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Apple, Microsoft

## Problem
You are given an image as an `m x n` grid of color values. Starting at `(sr, sc)`, recolor that pixel and every 4-directionally connected pixel with the same original color to `color`.

Return the modified image.

**Input**
- `image`: a 2-D list of integer colors.
- `sr`: starting row.
- `sc`: starting column.
- `color`: replacement color.

**Output**
- The recolored image. **This judge compares exactly**, so every cell must match.

## Constraints
- `m == image.length`
- `n == image[r].length`
- `1 <= m, n <= 50`
- `0 <= image[r][c], color < 2^16`
- `0 <= sr < m`, `0 <= sc < n`.

## Examples
```text
Input: image = [[1,1,1],[1,1,0],[1,0,1]], sr = 1, sc = 1, color = 2
Output: [[2,2,2],[2,2,0],[2,0,1]]
Explanation: The starting pixel has color 1. All connected 1-valued pixels are changed to 2, while disconnected or differently colored pixels remain unchanged.
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
