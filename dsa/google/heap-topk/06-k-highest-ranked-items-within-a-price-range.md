# 06. K Highest Ranked Items Within a Price Range

- **Difficulty:** Medium
- **Pattern:** Heaps / Top-K / Greedy-with-Heap
- **Asked at:** Google

## Problem
Given a grid where `0` is a wall and positive values are item prices, return up to `k` reachable item cells from `start` whose prices are in `pricing = [low, high]`. Rank by distance from `start`, then price, then row, then column. Return coordinates as `list[list[int]]`.

Implement `Solution.highestRankedKItems` with the parameters below and return the requested value.

**Input**
- `grid`: a `list[list[int]]`; the matrix/grid described above.
- `pricing`: a `list[int]`; the inclusive price range `[low, high]`.
- `start`: a `list[int]`; the starting cell or time described above.
- `k`: a `int`; the required count, window limit, or operation budget described above.

**Output**
- A list of item coordinates `[row, col]`. **This judge compares exactly**, so order them by increasing distance from `start`, then price, then row, then column, and return at most `k` coordinates.

## Constraints
- `1 <= rows, cols <= 100`

## Examples
```text
Input: grid = [[1,2,0,1],[1,3,0,1],[0,2,5,1]], pricing = [2,5], start = [0,0], k = 3
Output: [[0,1],[1,1],[2,1]]
Explanation: Reachable in-range items are ordered by distance, price, row, and column. The result is shown in the required order.
```

## Understanding & Intuition
Because moves have unit cost, BFS discovers shortest distances. The ranking adds price and coordinates after distance, so we can either collect all candidates and sort or keep the best candidates in a heap. Walls are never traversable.

## Approach 1 — Naive / Brute Force
**Idea:** BFS to compute all distances, collect all valid items, sort by the full ranking key.
```python
class Solution:
    def highestRankedKItems(self, grid: list[list[int]], pricing: list[int], start: list[int], k: int) -> list[list[int]]:
        from collections import deque
        m, n = len(grid), len(grid[0])
        dist = [[-1] * n for _ in range(m)]
        q = deque([(start[0], start[1])])
        dist[start[0]][start[1]] = 0
        while q:
            r, c = q.popleft()
            for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and grid[nr][nc] != 0 and dist[nr][nc] == -1:
                    dist[nr][nc] = dist[r][c] + 1
                    q.append((nr, nc))
        items = []
        low, high = pricing
        for r in range(m):
            for c in range(n):
                if dist[r][c] != -1 and low <= grid[r][c] <= high:
                    items.append((dist[r][c], grid[r][c], r, c))
        items.sort()
        return [[r, c] for _, _, r, c in items[:k]]
```
- **Time:** O(mn log(mn)) — **Space:** O(mn)

## Approach 2 — Better
**Idea:** BFS level by level, sort only the items discovered at the current distance, and stop once `k` answers are filled.
```python
class Solution:
    def highestRankedKItems(self, grid: list[list[int]], pricing: list[int], start: list[int], k: int) -> list[list[int]]:
        from collections import deque
        m, n = len(grid), len(grid[0])
        low, high = pricing
        seen = [[False] * n for _ in range(m)]
        q = deque([(start[0], start[1])])
        seen[start[0]][start[1]] = True
        ans = []
        while q and len(ans) < k:
            level_items = []
            for _ in range(len(q)):
                r, c = q.popleft()
                if low <= grid[r][c] <= high:
                    level_items.append((grid[r][c], r, c))
                for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                    nr, nc = r + dr, c + dc
                    if 0 <= nr < m and 0 <= nc < n and grid[nr][nc] != 0 and not seen[nr][nc]:
                        seen[nr][nc] = True
                        q.append((nr, nc))
            level_items.sort()
            for _, r, c in level_items:
                if len(ans) < k:
                    ans.append([r, c])
        return ans
```
- **Time:** O(mn log(mn)) — **Space:** O(mn)

## Approach 3 — Optimal
**Idea:** Use a priority queue ordered by distance, price, row, and column so valid cells leave the heap in answer order.
```python
class Solution:
    def highestRankedKItems(self, grid: list[list[int]], pricing: list[int], start: list[int], k: int) -> list[list[int]]:
        import heapq
        m, n = len(grid), len(grid[0])
        low, high = pricing
        heap = [(0, grid[start[0]][start[1]], start[0], start[1])]
        seen = {(start[0], start[1])}
        ans = []
        while heap and len(ans) < k:
            d, price, r, c = heapq.heappop(heap)
            if low <= price <= high:
                ans.append([r, c])
            for dr, dc in ((1,0),(-1,0),(0,1),(0,-1)):
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and grid[nr][nc] != 0 and (nr, nc) not in seen:
                    seen.add((nr, nc))
                    heapq.heappush(heap, (d + 1, grid[nr][nc], nr, nc))
        return ans
```
- **Time:** O(mn log(mn)) — **Space:** O(mn)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn log(mn)) | O(mn) |
| Better | O(mn log(mn)) | O(mn) |
| Optimal | O(mn log(mn)) | O(mn) |

## Edge Cases & Pitfalls
- The starting cell may itself be a valid item.
- Do not traverse cells with value `0`.
- Ranking is distance before price; price alone is not enough.

## Related
- Shortest Path in a Grid
- Top K Frequent Elements
