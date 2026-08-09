# 01. Network Delay Time

- **Difficulty:** Medium
- **Pattern:** Advanced Graphs
- **Asked at:** Amazon, Google, Microsoft, Meta

## Problem
A directed weighted edge `[u, v, w]` means a signal sent from `u` reaches `v` after `w` time units. Nodes are labeled `1` through `n`.

Starting from node `k`, return how long it takes for every node to receive the signal, or `-1` if some node is unreachable.

**Input**
- `times`: directed weighted edges `[u, v, w]`.
- `n`: the number of nodes.
- `k`: the starting node.

**Output**
- The maximum shortest-path distance from `k`, or `-1` if a node cannot be reached.

## Constraints
- `1 <= n <= 100`
- `0 <= times.length <= 6000`
- `times[i].length == 3`
- `1 <= u, v, k <= n`
- `1 <= w <= 100`.

## Examples
```text
Input: times = [[2,1,1],[2,3,1],[3,4,1]], n = 4, k = 2
Output: 2
Explanation: Nodes 1 and 3 receive the signal after 1 time unit. Node 4 is reached through 2 -> 3 -> 4 after 2 time units, the last arrival time.
```

## Understanding & Intuition
This is a single-source shortest path problem on a weighted directed graph. DFS can enumerate paths but repeats work. Bellman-Ford is simpler and handles general edge weights, while Dijkstra is optimal here because all weights are positive.

## Approach 1 — Naive / Brute Force
**Idea:** Try every simple path from `k` using DFS and keep the smallest arrival time per node.
```python
from collections import defaultdict
from typing import List

class Solution:
    def networkDelayTime(self, times: List[List[int]], n: int, k: int) -> int:
        graph = defaultdict(list)
        for u, v, w in times:
            graph[u].append((v, w))

        best = [float("inf")] * (n + 1)

        def dfs(node: int, cost: int, seen: set[int]) -> None:
            if cost >= best[node]:
                return
            best[node] = cost
            for nxt, weight in graph[node]:
                if nxt not in seen:  # avoid cycles in this path
                    seen.add(nxt)
                    dfs(nxt, cost + weight, seen)
                    seen.remove(nxt)

        dfs(k, 0, {k})
        ans = max(best[1:])
        return -1 if ans == float("inf") else ans
```
- **Time:** O(V!) — **Space:** O(V + E)

## Approach 2 — Better
**Idea:** Bellman-Ford relaxes every edge up to `n - 1` times to compute shortest paths.
```python
from typing import List

class Solution:
    def networkDelayTime(self, times: List[List[int]], n: int, k: int) -> int:
        dist = [float("inf")] * (n + 1)
        dist[k] = 0

        for _ in range(n - 1):
            changed = False
            for u, v, w in times:
                if dist[u] + w < dist[v]:
                    dist[v] = dist[u] + w
                    changed = True
            if not changed:
                break

        ans = max(dist[1:])
        return -1 if ans == float("inf") else ans
```
- **Time:** O(VE) — **Space:** O(V)

## Approach 3 — Optimal
**Idea:** Use Dijkstra with a min-heap; the first time a node is popped is its shortest arrival time.
```python
import heapq
from collections import defaultdict
from typing import List

class Solution:
    def networkDelayTime(self, times: List[List[int]], n: int, k: int) -> int:
        graph = defaultdict(list)
        for u, v, w in times:
            graph[u].append((v, w))

        dist = {}
        heap = [(0, k)]
        while heap:
            cost, node = heapq.heappop(heap)
            if node in dist:
                continue
            dist[node] = cost
            for nxt, weight in graph[node]:
                if nxt not in dist:
                    heapq.heappush(heap, (cost + weight, nxt))

        return max(dist.values()) if len(dist) == n else -1
```
- **Time:** O((V + E) log V) — **Space:** O(V + E)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(V!) | O(V + E) |
| Better | O(VE) | O(V) |
| Optimal | O((V + E) log V) | O(V + E) |

## Edge Cases & Pitfalls
- Return `-1` when any node is unreachable.
- Node labels are 1-based.
- Dijkstra is valid because all edge weights are positive.

## Related
- Cheapest Flights Within K Stops
- Path with Maximum Probability

