# 07. Path with Maximum Probability

- **Difficulty:** Medium
- **Pattern:** Advanced Graphs
- **Asked at:** Google, Amazon, Microsoft, Uber

## Problem
You are given an undirected graph with `n` nodes. For edge `edges[i]`, `succProb[i]` is the success probability of traversing that edge.

Return the maximum success probability of any path from `start_node` to `end_node`, or `0` if no path exists.

**Input**
- `n`: number of nodes.
- `edges`: undirected edges `[a, b]`.
- `succProb`: probabilities aligned with `edges`.
- `start_node`: start.
- `end_node`: destination.

**Output**
- A floating-point number: the largest product of probabilities along any path.

## Constraints
- `2 <= n <= 10000`
- `0 <= edges.length <= 20000`
- `succProb.length == edges.length`
- `0 <= a, b, start_node, end_node < n`
- `0 <= succProb[i] <= 1`.

## Examples
```text
Input: n = 3, edges = [[0,1],[1,2],[0,2]], succProb = [0.5,0.5,0.2], start_node = 0, end_node = 2
Output: 0.25
Explanation: The direct path succeeds with probability 0.2. The path `0 -> 1 -> 2` succeeds with `0.5 * 0.5 = 0.25`, which is larger.
```

## Understanding & Intuition
Path probability multiplies edge probabilities. DFS can enumerate paths, and Bellman-Ford can repeatedly improve best probabilities. A max-heap Dijkstra variant is optimal because probabilities only decrease as paths extend.

## Approach 1 — Naive / Brute Force
**Idea:** DFS all simple paths and keep the highest product.
```python
from collections import defaultdict
from typing import List

class Solution:
    def maxProbability(self, n: int, edges: List[List[int]], succProb: List[float], start_node: int, end_node: int) -> float:
        graph = defaultdict(list)
        for (u, v), p in zip(edges, succProb):
            graph[u].append((v, p))
            graph[v].append((u, p))

        best = 0.0

        def dfs(node: int, prob: float, seen: set[int]) -> None:
            nonlocal best
            if prob <= best:
                return
            if node == end_node:
                best = prob
                return
            for nxt, p in graph[node]:
                if nxt not in seen:
                    seen.add(nxt)
                    dfs(nxt, prob * p, seen)
                    seen.remove(nxt)

        dfs(start_node, 1.0, {start_node})
        return best
```
- **Time:** O(V!) — **Space:** O(V + E)

## Approach 2 — Better
**Idea:** Bellman-Ford style relaxation maximizes probability products for up to `n - 1` edges.
```python
from typing import List

class Solution:
    def maxProbability(self, n: int, edges: List[List[int]], succProb: List[float], start_node: int, end_node: int) -> float:
        prob = [0.0] * n
        prob[start_node] = 1.0

        for _ in range(n - 1):
            changed = False
            for (u, v), p in zip(edges, succProb):
                if prob[u] * p > prob[v]:
                    prob[v] = prob[u] * p
                    changed = True
                if prob[v] * p > prob[u]:
                    prob[u] = prob[v] * p
                    changed = True
            if not changed:
                break
        return prob[end_node]
```
- **Time:** O(VE) — **Space:** O(V)

## Approach 3 — Optimal
**Idea:** Use a max-heap; the first time the destination is popped, its probability is maximal.
```python
import heapq
from collections import defaultdict
from typing import List

class Solution:
    def maxProbability(self, n: int, edges: List[List[int]], succProb: List[float], start_node: int, end_node: int) -> float:
        graph = defaultdict(list)
        for (u, v), p in zip(edges, succProb):
            graph[u].append((v, p))
            graph[v].append((u, p))

        best = [0.0] * n
        best[start_node] = 1.0
        heap = [(-1.0, start_node)]

        while heap:
            neg_prob, node = heapq.heappop(heap)
            cur = -neg_prob
            if node == end_node:
                return cur
            if cur < best[node]:
                continue
            for nxt, p in graph[node]:
                new_prob = cur * p
                if new_prob > best[nxt]:
                    best[nxt] = new_prob
                    heapq.heappush(heap, (-new_prob, nxt))
        return 0.0
```
- **Time:** O((V + E) log V) — **Space:** O(V + E)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(V!) | O(V + E) |
| Better | O(VE) | O(V) |
| Optimal | O((V + E) log V) | O(V + E) |

## Edge Cases & Pitfalls
- Return `0.0` if no path exists.
- Probabilities multiply, so extending a path cannot increase it.
- Use a max-heap by pushing negative probabilities.

## Related
- Network Delay Time
- Dijkstra variants

