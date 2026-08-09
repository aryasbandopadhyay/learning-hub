# 04. Min Cost to Connect All Points

- **Difficulty:** Medium
- **Pattern:** Advanced Graphs
- **Asked at:** Amazon, Microsoft, Google, Apple

## Problem
You are given points in the plane. The cost to connect two points is their Manhattan distance `|xi - xj| + |yi - yj|`.

Return the minimum total cost to connect all points so every point can reach every other point through chosen connections.

**Input**
- `points`: coordinate pairs `[x, y]`.

**Output**
- An integer: the total weight of a minimum spanning tree.

## Constraints
- `1 <= points.length <= 1000`
- `points[i].length == 2`
- `-10^6 <= x, y <= 10^6`
- All points are distinct.

## Examples
```text
Input: points = [[0,0],[2,2],[3,10],[5,2],[7,0]]
Output: 20
Explanation: A minimum spanning tree connects the five points with total Manhattan cost 20. Any lower cost would leave some point disconnected.
```

## Understanding & Intuition
All points form a complete weighted graph. A naive spanning-tree search is exponential. Minimum spanning tree algorithms solve it: Kruskal sorts all edges, while Prim can avoid storing every edge at once.

## Approach 1 — Naive / Brute Force
**Idea:** Try adding edges recursively while avoiding cycles and keep the cheapest tree.
```python
from typing import List

class Solution:
    def minCostConnectPoints(self, points: List[List[int]]) -> int:
        n = len(points)
        edges = []
        for i in range(n):
            for j in range(i + 1, n):
                cost = abs(points[i][0] - points[j][0]) + abs(points[i][1] - points[j][1])
                edges.append((cost, i, j))

        best = float("inf")

        def connected(parent: list[int], a: int, b: int) -> bool:
            def find(x: int) -> int:
                while parent[x] != x:
                    x = parent[x]
                return x
            return find(a) == find(b)

        def search(idx: int, chosen: int, total: int, parent: list[int]) -> None:
            nonlocal best
            if total >= best:
                return
            if chosen == n - 1:
                best = total
                return
            if idx == len(edges):
                return
            cost, u, v = edges[idx]
            if not connected(parent, u, v):
                new_parent = parent[:]
                ru = u
                while new_parent[ru] != ru:
                    ru = new_parent[ru]
                rv = v
                while new_parent[rv] != rv:
                    rv = new_parent[rv]
                new_parent[ru] = rv
                search(idx + 1, chosen + 1, total + cost, new_parent)
            search(idx + 1, chosen, total, parent)

        search(0, 0, 0, list(range(n)))
        return 0 if n <= 1 else best
```
- **Time:** O(2^E * V) — **Space:** O(E + V)

## Approach 2 — Better
**Idea:** Use Kruskal's algorithm with Union-Find over all pairwise edges.
```python
from typing import List

class Solution:
    def minCostConnectPoints(self, points: List[List[int]]) -> int:
        n = len(points)
        parent = list(range(n))
        rank = [0] * n

        def find(x: int) -> int:
            if parent[x] != x:
                parent[x] = find(parent[x])
            return parent[x]

        def union(a: int, b: int) -> bool:
            ra, rb = find(a), find(b)
            if ra == rb:
                return False
            if rank[ra] < rank[rb]:
                ra, rb = rb, ra
            parent[rb] = ra
            if rank[ra] == rank[rb]:
                rank[ra] += 1
            return True

        edges = []
        for i in range(n):
            for j in range(i + 1, n):
                cost = abs(points[i][0] - points[j][0]) + abs(points[i][1] - points[j][1])
                edges.append((cost, i, j))
        edges.sort()

        total = used = 0
        for cost, u, v in edges:
            if union(u, v):
                total += cost
                used += 1
                if used == n - 1:
                    break
        return total
```
- **Time:** O(V^2 log V) — **Space:** O(V^2)

## Approach 3 — Optimal
**Idea:** Prim's algorithm grows the MST and computes distances to unvisited points on demand.
```python
from typing import List

class Solution:
    def minCostConnectPoints(self, points: List[List[int]]) -> int:
        n = len(points)
        in_mst = [False] * n
        min_dist = [float("inf")] * n
        min_dist[0] = 0
        total = 0

        for _ in range(n):
            u = -1
            for i in range(n):
                if not in_mst[i] and (u == -1 or min_dist[i] < min_dist[u]):
                    u = i
            in_mst[u] = True
            total += min_dist[u]

            for v in range(n):
                if not in_mst[v]:
                    dist = abs(points[u][0] - points[v][0]) + abs(points[u][1] - points[v][1])
                    if dist < min_dist[v]:
                        min_dist[v] = dist
        return total
```
- **Time:** O(V^2) — **Space:** O(V)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^E * V) | O(E + V) |
| Better | O(V^2 log V) | O(V^2) |
| Optimal | O(V^2) | O(V) |

## Edge Cases & Pitfalls
- A single point costs `0`.
- The graph is complete, so no connectivity check is needed.
- Kruskal stores O(V^2) edges, which is large for `n = 1000`.

## Related
- Prim's Algorithm
- Kruskal's Algorithm

