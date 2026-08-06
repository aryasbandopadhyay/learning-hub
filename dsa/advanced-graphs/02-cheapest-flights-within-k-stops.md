# 02. Cheapest Flights Within K Stops

- **Difficulty:** Medium
- **Pattern:** Advanced Graphs
- **Asked at:** Amazon, Google, Meta, Bloomberg

## Problem
Given `n` cities, flights `[from, to, price]`, source `src`, destination `dst`, and at most `k` stops, return the cheapest price from `src` to `dst`, or `-1` if no such route exists. Constraints: `1 <= n <= 100`, prices are positive, and at most `k + 1` edges may be used.

## Examples
```text
Input: n = 4, flights = [[0,1,100],[1,2,100],[2,0,100],[1,3,600],[2,3,200]], src = 0, dst = 3, k = 1
Output: 700
Explanation: The route 0 -> 1 -> 3 costs 700 and uses one stop.
```

## Understanding & Intuition
The stop limit makes ordinary shortest path insufficient unless state includes remaining edges. DFS can search all valid routes. Bellman-Ford with exactly `k + 1` rounds is a clean dynamic-programming solution, and a heap over `(cost, city, edges_used)` often explores fewer states.

## Approach 1 — Naive / Brute Force
**Idea:** DFS through all routes using at most `k + 1` edges and track the minimum cost.
```python
from collections import defaultdict
from typing import List

class Solution:
    def findCheapestPrice(self, n: int, flights: List[List[int]], src: int, dst: int, k: int) -> int:
        graph = defaultdict(list)
        for u, v, price in flights:
            graph[u].append((v, price))

        best = float("inf")

        def dfs(city: int, edges_left: int, cost: int, seen: set[int]) -> None:
            nonlocal best
            if cost >= best:
                return
            if city == dst:
                best = cost
                return
            if edges_left == 0:
                return
            for nxt, price in graph[city]:
                if nxt not in seen:
                    seen.add(nxt)
                    dfs(nxt, edges_left - 1, cost + price, seen)
                    seen.remove(nxt)

        dfs(src, k + 1, 0, {src})
        return -1 if best == float("inf") else best
```
- **Time:** O(V^(K+1)) — **Space:** O(V + E)

## Approach 2 — Better
**Idea:** Run `k + 1` Bellman-Ford relaxations using a copy so each round adds one more edge.
```python
from typing import List

class Solution:
    def findCheapestPrice(self, n: int, flights: List[List[int]], src: int, dst: int, k: int) -> int:
        dist = [float("inf")] * n
        dist[src] = 0

        for _ in range(k + 1):
            nxt_dist = dist[:]  # prevents using more than one new edge this round
            for u, v, price in flights:
                if dist[u] + price < nxt_dist[v]:
                    nxt_dist[v] = dist[u] + price
            dist = nxt_dist

        return -1 if dist[dst] == float("inf") else dist[dst]
```
- **Time:** O(KE) — **Space:** O(V)

## Approach 3 — Optimal
**Idea:** Use Dijkstra-style best-first search with state `(city, edges_used)` and stop when `dst` is popped.
```python
import heapq
from collections import defaultdict
from typing import List

class Solution:
    def findCheapestPrice(self, n: int, flights: List[List[int]], src: int, dst: int, k: int) -> int:
        graph = defaultdict(list)
        for u, v, price in flights:
            graph[u].append((v, price))

        max_edges = k + 1
        best = [[float("inf")] * (max_edges + 1) for _ in range(n)]
        best[src][0] = 0
        heap = [(0, src, 0)]

        while heap:
            cost, city, used = heapq.heappop(heap)
            if city == dst:
                return cost
            if used == max_edges or cost != best[city][used]:
                continue
            for nxt, price in graph[city]:
                new_cost = cost + price
                if new_cost < best[nxt][used + 1]:
                    best[nxt][used + 1] = new_cost
                    heapq.heappush(heap, (new_cost, nxt, used + 1))

        return -1
```
- **Time:** O(KE log(KV)) — **Space:** O(KV + E)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(V^(K+1)) | O(V + E) |
| Better | O(KE) | O(V) |
| Optimal | O(KE log(KV)) | O(KV + E) |

## Edge Cases & Pitfalls
- `k` stops means at most `k + 1` edges.
- Bellman-Ford must copy the previous distance array each round.
- A cheaper path with too many stops is invalid.

## Related
- Network Delay Time
- Bellman-Ford shortest paths

