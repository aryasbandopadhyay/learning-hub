# 13. Possible Bipartition

- **Difficulty:** Medium
- **Pattern:** graphs: topological sort & union-find
- **Asked at:** Google, Amazon, Meta

## Problem
There are `n` people labeled `1..n` and `dislikes`, where `[a, b]` means `a` and `b` cannot be in the same group. Return `True` if everyone can be split into two groups so every dislike pair crosses groups.

Constraints: `1 <= n <= 2000`, `0 <= len(dislikes) <= 10000`.

## Examples
```text
Input: n = 4, dislikes = [[1,2],[1,3],[2,4]]
Output: True
Explanation: One valid split is {1,4} and {2,3}.
```

## Understanding & Intuition
The dislike graph must be bipartite. Coloring adjacent nodes with opposite colors proves feasibility. Union-find can encode the same idea by grouping all neighbors of a node into the node's opposite side.

## Approach 1 — Naive / Brute Force
**Idea:** Backtrack through people, trying both groups while respecting assigned disliked neighbors.
```python
class Solution:
    def possibleBipartition(self, n: int, dislikes: list[list[int]]) -> bool:
        adj = [[] for _ in range(n + 1)]
        for a, b in dislikes:
            adj[a].append(b); adj[b].append(a)
        color = [0] * (n + 1)
        def backtrack(person):
            if person == n + 1:
                return True
            for c in (1, -1):
                if all(color[v] != c for v in adj[person]):
                    color[person] = c
                    if backtrack(person + 1):
                        return True
                    color[person] = 0
            return False
        return backtrack(1)
```
- **Time:** O(2^n + e) — **Space:** O(n + e)

## Approach 2 — Better
**Idea:** BFS-color every connected component and reject same-color endpoints.
```python
class Solution:
    def possibleBipartition(self, n: int, dislikes: list[list[int]]) -> bool:
        from collections import deque
        adj = [[] for _ in range(n + 1)]
        for a, b in dislikes:
            adj[a].append(b); adj[b].append(a)
        color = [0] * (n + 1)
        for i in range(1, n + 1):
            if color[i] != 0:
                continue
            color[i] = 1; q = deque([i])
            while q:
                u = q.popleft()
                for v in adj[u]:
                    if color[v] == color[u]:
                        return False
                    if color[v] == 0:
                        color[v] = -color[u]; q.append(v)
        return True
```
- **Time:** O(n + e) — **Space:** O(n + e)

## Approach 3 — Optimal
**Idea:** For each node, union all its disliked neighbors together and ensure none is in the node's own set.
```python
class Solution:
    def possibleBipartition(self, n: int, dislikes: list[list[int]]) -> bool:
        adj = [[] for _ in range(n + 1)]
        for a, b in dislikes:
            adj[a].append(b); adj[b].append(a)
        parent = list(range(n + 1)); rank = [0] * (n + 1)
        def find(x):
            while parent[x] != x:
                parent[x] = parent[parent[x]]; x = parent[x]
            return x
        def union(a, b):
            ra, rb = find(a), find(b)
            if ra == rb:
                return
            if rank[ra] < rank[rb]:
                ra, rb = rb, ra
            parent[rb] = ra
            if rank[ra] == rank[rb]:
                rank[ra] += 1
        for u in range(1, n + 1):
            if not adj[u]:
                continue
            first = adj[u][0]
            for v in adj[u]:
                if find(u) == find(v):
                    return False
                union(first, v)
        return True
```
- **Time:** O((n + e) α(n)) — **Space:** O(n + e)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n + e) | O(n + e) |
| Better | O(n + e) | O(n + e) |
| Optimal | O((n + e) α(n)) | O(n + e) |

## Edge Cases & Pitfalls
- People are labeled from `1`.
- Disconnected components must all be checked.

## Related
- Bipartite Graph
- Union-Find with Constraints
