# 08. Find the City With the Smallest Number of Neighbors at a Threshold Distance

- **Difficulty:** Medium
- **Pattern:** Advanced Graphs
- **Asked at:** Amazon, Google, Microsoft, Adobe

## Problem
You are given an undirected weighted graph and a distance threshold.

For each city, count other cities reachable by a path of total distance at most `distanceThreshold`. Return the city with the smallest count; break ties by choosing the greatest city label.

**Input**
- `n`: number of cities.
- `edges`: undirected weighted edges `[from, to, weight]`.
- `distanceThreshold`: maximum allowed path distance.

**Output**
- The chosen city label, with ties resolved toward the larger label.

## Constraints
- `2 <= n <= 100`
- `1 <= edges.length <= n * (n - 1) / 2`
- `edges[i].length == 3`
- `0 <= from, to < n`
- `1 <= weight, distanceThreshold <= 10000`.

## Examples
```text
Input: n = 4, edges = [[0,1,3],[1,2,1],[1,3,4],[2,3,1]], distanceThreshold = 4
Output: 3
Explanation: Cities 0 and 3 each reach two neighbours within distance 4. The tie rule chooses the larger label, 3.
```

## Understanding & Intuition
We need all-pairs reachability under a distance threshold. DFS can enumerate paths from each city but is expensive. Running Dijkstra from each city works well on sparse graphs, while Floyd-Warshall is concise and optimal for the small `n <= 100` constraint.

## Approach 1 — Naive / Brute Force
**Idea:** From each city, DFS all simple paths whose current distance is within the threshold.
```python
from collections import defaultdict
from typing import List

class Solution:
    def findTheCity(self, n: int, edges: List[List[int]], distanceThreshold: int) -> int:
        graph = defaultdict(list)
        for u, v, w in edges:
            graph[u].append((v, w))
            graph[v].append((u, w))

        def count_from(start: int) -> int:
            reached = set()

            def dfs(node: int, dist: int, seen: set[int]) -> None:
                for nxt, weight in graph[node]:
                    new_dist = dist + weight
                    if nxt not in seen and new_dist <= distanceThreshold:
                        reached.add(nxt)
                        seen.add(nxt)
                        dfs(nxt, new_dist, seen)
                        seen.remove(nxt)

            dfs(start, 0, {start})
            return len(reached)

        best_city, best_count = -1, float("inf")
        for city in range(n):
            cnt = count_from(city)
            if cnt <= best_count:
                best_city, best_count = city, cnt
        return best_city
```
- **Time:** O(V * V!) — **Space:** O(V + E)

## Approach 2 — Better
**Idea:** Run Dijkstra from every city and count distances within the threshold.
```python
import heapq
from collections import defaultdict
from typing import List

class Solution:
    def findTheCity(self, n: int, edges: List[List[int]], distanceThreshold: int) -> int:
        graph = defaultdict(list)
        for u, v, w in edges:
            graph[u].append((v, w))
            graph[v].append((u, w))

        def dijkstra(start: int) -> int:
            dist = [float("inf")] * n
            dist[start] = 0
            heap = [(0, start)]
            while heap:
                cost, node = heapq.heappop(heap)
                if cost != dist[node]:
                    continue
                for nxt, weight in graph[node]:
                    new_cost = cost + weight
                    if new_cost < dist[nxt]:
                        dist[nxt] = new_cost
                        heapq.heappush(heap, (new_cost, nxt))
            return sum(0 < d <= distanceThreshold for d in dist)

        answer, best_count = -1, float("inf")
        for city in range(n):
            cnt = dijkstra(city)
            if cnt <= best_count:
                answer, best_count = city, cnt
        return answer
```
- **Time:** O(VE log V) — **Space:** O(V + E)

## Approach 3 — Optimal
**Idea:** Floyd-Warshall computes all-pairs shortest paths, then ties are handled by scanning city indices ascending with `<=`.
```python
from typing import List

class Solution:
    def findTheCity(self, n: int, edges: List[List[int]], distanceThreshold: int) -> int:
        dist = [[float("inf")] * n for _ in range(n)]
        for i in range(n):
            dist[i][i] = 0
        for u, v, w in edges:
            dist[u][v] = min(dist[u][v], w)
            dist[v][u] = min(dist[v][u], w)

        for mid in range(n):
            for src in range(n):
                for dst in range(n):
                    if dist[src][mid] + dist[mid][dst] < dist[src][dst]:
                        dist[src][dst] = dist[src][mid] + dist[mid][dst]

        answer, best_count = -1, float("inf")
        for city in range(n):
            cnt = sum(0 < dist[city][other] <= distanceThreshold for other in range(n))
            if cnt <= best_count:
                answer, best_count = city, cnt
        return answer
```
- **Time:** O(V^3) — **Space:** O(V^2)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(V * V!) | O(V + E) |
| Better | O(VE log V) | O(V + E) |
| Optimal | O(V^3) | O(V^2) |

## Edge Cases & Pitfalls
- Do not count the city itself as a neighbor.
- Ties choose the larger city index.
- Multiple edges between the same cities should keep the smaller weight.

## Related
- Floyd-Warshall
- Network Delay Time

