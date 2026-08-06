# 09. Most Stones Removed with Same Row or Column

- **Difficulty:** Medium
- **Pattern:** graphs: topological sort & union-find
- **Asked at:** Google, Amazon, Meta

## Problem
Given stone coordinates `stones`, you may remove a stone if another remaining stone shares its row or column. Return the maximum number of stones that can be removed.

Constraints: `1 <= len(stones) <= 1000`, coordinates are non-negative integers.

## Examples
```text
Input: stones = [[0,0],[0,1],[1,0],[1,2],[2,1],[2,2]]
Output: 5
Explanation: All stones are connected by shared rows or columns, so only one must remain.
```

## Understanding & Intuition
In each connected component of stones, all but one stone can be removed. Thus the answer is `number of stones - number of components`. Rows and columns define the component connections.

## Approach 1 — Naive / Brute Force
**Idea:** Compare every pair of stones, build an explicit graph, and count components.
```python
class Solution:
    def removeStones(self, stones: list[list[int]]) -> int:
        n = len(stones)
        adj = [[] for _ in range(n)]
        for i in range(n):
            for j in range(i + 1, n):
                if stones[i][0] == stones[j][0] or stones[i][1] == stones[j][1]:
                    adj[i].append(j); adj[j].append(i)
        seen = [False] * n; comps = 0
        for i in range(n):
            if not seen[i]:
                comps += 1; seen[i] = True; stack = [i]
                while stack:
                    u = stack.pop()
                    for v in adj[u]:
                        if not seen[v]:
                            seen[v] = True; stack.append(v)
        return n - comps
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 2 — Better
**Idea:** Traverse through row and column buckets without materializing all pair edges.
```python
class Solution:
    def removeStones(self, stones: list[list[int]]) -> int:
        from collections import defaultdict
        rows = defaultdict(list); cols = defaultdict(list)
        for i, (r, c) in enumerate(stones):
            rows[r].append(i); cols[c].append(i)
        seen = [False] * len(stones); comps = 0
        for i in range(len(stones)):
            if seen[i]:
                continue
            comps += 1; seen[i] = True; stack = [i]
            while stack:
                u = stack.pop(); r, c = stones[u]
                for v in rows[r] + cols[c]:
                    if not seen[v]:
                        seen[v] = True; stack.append(v)
        return len(stones) - comps
```
- **Time:** O(n^2) worst-case — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Union each stone's row node with its column node; used roots are components.
```python
class Solution:
    def removeStones(self, stones: list[list[int]]) -> int:
        parent = {}
        def find(x):
            if x not in parent:
                parent[x] = x
            while parent[x] != x:
                parent[x] = parent[parent[x]]; x = parent[x]
            return x
        def union(a, b):
            ra, rb = find(a), find(b)
            if ra != rb:
                parent[rb] = ra
        for r, c in stones:
            union(('r', r), ('c', c))
        roots = {find(('r', r)) for r, c in stones}
        return len(stones) - len(roots)
```
- **Time:** O(n α(n)) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n^2) |
| Better | O(n^2) | O(n) |
| Optimal | O(n α(n)) | O(n) |

## Edge Cases & Pitfalls
- Isolated stones cannot be removed.
- Rows and columns need separate union-find namespaces.

## Related
- Union-Find
- Connected Components
