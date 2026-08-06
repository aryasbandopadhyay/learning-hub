# 05. Minimum Height Trees

- **Difficulty:** Medium
- **Pattern:** graphs: topological sort & union-find
- **Asked at:** Google, Meta, Amazon

## Problem
Given an undirected tree with `n` nodes labeled `0..n-1` and edge list `edges`, return all roots that produce a minimum-height tree. Return the roots sorted increasingly.

Constraints: `1 <= n <= 20000`, `len(edges) = n - 1` for `n > 1`.

## Examples
```text
Input: n = 4, edges = [[1,0],[1,2],[1,3]]
Output: [1]
Explanation: Rooting at 1 gives height 1.
```

## Understanding & Intuition
A tree has one or two centers. Removing all current leaves peels one height layer from every possible root. The remaining one or two nodes are exactly the minimum-height roots.

## Approach 1 — Naive / Brute Force
**Idea:** BFS from every node to measure its height, then return the minimum-height roots.
```python
class Solution:
    def findMinHeightTrees(self, n: int, edges: list[list[int]]) -> list[int]:
        if n == 1:
            return [0]
        from collections import deque
        adj = [[] for _ in range(n)]
        for a, b in edges:
            adj[a].append(b); adj[b].append(a)
        heights = []
        for root in range(n):
            seen = [False] * n; seen[root] = True
            q = deque([root]); h = -1
            while q:
                h += 1
                for _ in range(len(q)):
                    u = q.popleft()
                    for v in adj[u]:
                        if not seen[v]:
                            seen[v] = True; q.append(v)
            heights.append(h)
        best = min(heights)
        return [i for i, h in enumerate(heights) if h == best]
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** The middle node(s) of the tree diameter are the centers.
```python
class Solution:
    def findMinHeightTrees(self, n: int, edges: list[list[int]]) -> list[int]:
        if n == 1:
            return [0]
        from collections import deque
        adj = [[] for _ in range(n)]
        for a, b in edges:
            adj[a].append(b); adj[b].append(a)
        def bfs(src):
            parent = [-1] * n; dist = [-1] * n
            dist[src] = 0; q = deque([src]); far = src
            while q:
                u = q.popleft(); far = u
                for v in adj[u]:
                    if dist[v] == -1:
                        dist[v] = dist[u] + 1; parent[v] = u; q.append(v)
            return far, parent
        a, _ = bfs(0)
        b, parent = bfs(a)
        path = []
        while b != -1:
            path.append(b); b = parent[b]
        m = len(path)
        return [path[m // 2]] if m % 2 else sorted([path[m // 2 - 1], path[m // 2]])
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Topologically trim leaves until at most two nodes remain.
```python
class Solution:
    def findMinHeightTrees(self, n: int, edges: list[list[int]]) -> list[int]:
        if n == 1:
            return [0]
        from collections import deque
        adj = [set() for _ in range(n)]
        for a, b in edges:
            adj[a].add(b); adj[b].add(a)
        leaves = deque([i for i in range(n) if len(adj[i]) == 1])
        remain = n
        while remain > 2:
            size = len(leaves); remain -= size
            for _ in range(size):
                leaf = leaves.popleft()
                nei = adj[leaf].pop()
                adj[nei].remove(leaf)
                if len(adj[nei]) == 1:
                    leaves.append(nei)
        return sorted(leaves)
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- `n = 1` has no edges and answer `[0]`.
- A path with even node count has two centers.

## Related
- Tree Diameter
- Topological Leaf Trimming
