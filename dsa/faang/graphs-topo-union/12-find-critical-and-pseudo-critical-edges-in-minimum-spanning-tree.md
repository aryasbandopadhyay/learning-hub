# 12. Find Critical and Pseudo-Critical Edges in Minimum Spanning Tree

- **Difficulty:** Hard
- **Pattern:** graphs: topological sort & union-find
- **Asked at:** Google, Amazon, Meta

## Problem
Implement `findCriticalAndPseudoCriticalEdges` for **Find Critical and Pseudo-Critical Edges in Minimum Spanning Tree**. Given a connected weighted undirected graph with `n` nodes and `edges`, where each edge is `[u, v, weight]`, return `[critical, pseudo]`. Critical edges appear in every minimum spanning tree; pseudo-critical edges can appear in at least one. Return indices increasingly in each list.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

This judge compares exactly: return `[critical, pseudoCritical]`, with edge indices ascending inside each list.

**Input**
- `n`: integer; problem size or count as defined above.
- `edges`: list; edge list or outgoing-edge list as defined above.

**Output**
- A list. This judge compares exactly: return `[critical, pseudoCritical]`, with edge indices ascending inside each list.

## Constraints
- `2 <= n <= 100`, `1 <= len(edges) <= 200`, the graph is connected

## Examples
```text
Input: n = 5, edges = [[0,1,1],[1,2,1],[2,3,2],[0,3,2],[0,4,3],[3,4,3],[1,4,6]]
Output: [[0,1],[2,3,4,5]]
Explanation: Edges 0 and 1 are mandatory; edges 2, 3, 4, and 5 can be in some MST. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Kruskal's algorithm gives the MST weight. If excluding an edge increases the MST weight, it is critical. If forcing an edge can still achieve the base MST weight, it is pseudo-critical.

## Approach 1 — Naive / Brute Force
**Idea:** For tiny edge counts, enumerate MST-sized subsets; otherwise fall back to the same classification helper to stay correct.
```python
class Solution:
    def findCriticalAndPseudoCriticalEdges(self, n: int, edges: list[list[int]]) -> list[list[int]]:
        from itertools import combinations
        m = len(edges)
        def classify():
            order = sorted(range(m), key=lambda i: edges[i][2])
            def run(blocked, picked):
                parent = list(range(n)); size = [1] * n
                def find(x):
                    while parent[x] != x:
                        parent[x] = parent[parent[x]]; x = parent[x]
                    return x
                def union(a, b):
                    ra, rb = find(a), find(b)
                    if ra == rb: return False
                    if size[ra] < size[rb]: ra, rb = rb, ra
                    parent[rb] = ra; size[ra] += size[rb]
                    return True
                total = used = 0
                if picked != -1:
                    u, v, w = edges[picked]
                    union(u, v); total += w; used += 1
                for idx in order:
                    if idx != blocked:
                        u, v, w = edges[idx]
                        if union(u, v): total += w; used += 1
                return total if used == n - 1 else 10**18
            base = run(-1, -1); critical = []; pseudo = []
            for i in range(m):
                if run(i, -1) > base: critical.append(i)
                elif run(-1, i) == base: pseudo.append(i)
            return [critical, pseudo]
        if m > 18:
            return classify()
        best = None; msts = []
        for combo in combinations(range(m), n - 1):
            parent = list(range(n))
            def find(x):
                while parent[x] != x:
                    parent[x] = parent[parent[x]]; x = parent[x]
                return x
            weight = 0; ok = True
            for idx in combo:
                u, v, w = edges[idx]; ru, rv = find(u), find(v)
                if ru == rv:
                    ok = False; break
                parent[rv] = ru; weight += w
            if ok and len({find(i) for i in range(n)}) == 1:
                if best is None or weight < best:
                    best = weight; msts = [set(combo)]
                elif weight == best:
                    msts.append(set(combo))
        critical = [i for i in range(m) if msts and all(i in s for s in msts)]
        pseudo = [i for i in range(m) if msts and any(i in s for s in msts) and i not in critical]
        return [critical, pseudo]
```
- **Time:** Exponential for tiny `e`, fallback O(e^2 α(n)) — **Space:** O(e + n)

## Approach 2 — Better
**Idea:** Rerun Kruskal excluding and forcing each edge.
```python
class Solution:
    def findCriticalAndPseudoCriticalEdges(self, n: int, edges: list[list[int]]) -> list[list[int]]:
        indexed = [e + [i] for i, e in enumerate(edges)]
        indexed.sort(key=lambda x: x[2])
        def kruskal(skip=-1, force=-1):
            parent = list(range(n)); rank = [0] * n
            def find(x):
                while parent[x] != x:
                    parent[x] = parent[parent[x]]; x = parent[x]
                return x
            def union(a, b):
                ra, rb = find(a), find(b)
                if ra == rb: return False
                if rank[ra] < rank[rb]: ra, rb = rb, ra
                parent[rb] = ra
                if rank[ra] == rank[rb]: rank[ra] += 1
                return True
            total = used = 0
            if force != -1:
                u, v, w = edges[force]
                union(u, v); total += w; used += 1
            for u, v, w, idx in indexed:
                if idx != skip and union(u, v):
                    total += w; used += 1
            return total if used == n - 1 else 10**18
        base = kruskal(); critical = []; pseudo = []
        for i in range(len(edges)):
            if kruskal(skip=i) > base:
                critical.append(i)
            elif kruskal(force=i) == base:
                pseudo.append(i)
        return [critical, pseudo]
```
- **Time:** O(e^2 α(n)) — **Space:** O(n + e)

## Approach 3 — Optimal
**Idea:** Sort edge indices once and use compact union-by-size Kruskal for every include/exclude test.
```python
class Solution:
    def findCriticalAndPseudoCriticalEdges(self, n: int, edges: list[list[int]]) -> list[list[int]]:
        order = sorted(range(len(edges)), key=lambda i: edges[i][2])
        def run(blocked: int, picked: int) -> int:
            parent = list(range(n)); size = [1] * n
            def find(x):
                while parent[x] != x:
                    parent[x] = parent[parent[x]]; x = parent[x]
                return x
            def union(a, b):
                ra, rb = find(a), find(b)
                if ra == rb:
                    return False
                if size[ra] < size[rb]:
                    ra, rb = rb, ra
                parent[rb] = ra; size[ra] += size[rb]
                return True
            total = used = 0
            if picked != -1:
                u, v, w = edges[picked]
                union(u, v); total += w; used += 1
            for idx in order:
                if idx == blocked:
                    continue
                u, v, w = edges[idx]
                if union(u, v):
                    total += w; used += 1
            return total if used == n - 1 else 10**18
        base = run(-1, -1)
        critical = []; pseudo = []
        for i in range(len(edges)):
            if run(i, -1) > base:
                critical.append(i)
            elif run(-1, i) == base:
                pseudo.append(i)
        return [critical, pseudo]
```
- **Time:** O(e^2 α(n)) — **Space:** O(n + e)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | Exponential fallback O(e^2 α(n)) | O(e + n) |
| Better | O(e^2 α(n)) | O(n + e) |
| Optimal | O(e^2 α(n)) | O(n + e) |

## Edge Cases & Pitfalls
- Always preserve original edge indices after sorting by weight.
- Critical edges are not also listed as pseudo-critical.

## Related
- Kruskal's Algorithm
- Minimum Spanning Tree
