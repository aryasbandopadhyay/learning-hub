# 10. Number of Operations to Make Network Connected

- **Difficulty:** Medium
- **Pattern:** graphs: topological sort & union-find
- **Asked at:** Amazon, Google, Microsoft

## Problem
Implement `makeConnected` for **Number of Operations to Make Network Connected**. There are `n` computers labeled `0..n-1` and undirected cable `connections`. You may move an extra cable to connect two components. Return the minimum operations needed to connect all computers, or `-1` if impossible.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `n`: integer; problem size or count as defined above.
- `connections`: list; network cables.

**Output**
- A single integer.

## Constraints
- `1 <= n <= 100000`, `0 <= len(connections) <= 100000`

## Examples
```text
Input: n = 4, connections = [[0,1],[0,2],[1,2]]
Output: 1
Explanation: The extra cable inside {0,1,2} can connect computer 3. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
A connected graph on `n` nodes needs at least `n - 1` edges. If enough cables exist, every operation can reduce the component count by one. The answer is therefore components minus one.

## Approach 1 — Naive / Brute Force
**Idea:** Build an adjacency matrix and count connected components.
```python
class Solution:
    def makeConnected(self, n: int, connections: list[list[int]]) -> int:
        if len(connections) < n - 1:
            return -1
        mat = [[False] * n for _ in range(n)]
        for a, b in connections:
            mat[a][b] = mat[b][a] = True
        seen = [False] * n; comps = 0
        for i in range(n):
            if not seen[i]:
                comps += 1; seen[i] = True; stack = [i]
                while stack:
                    u = stack.pop()
                    for v in range(n):
                        if mat[u][v] and not seen[v]:
                            seen[v] = True; stack.append(v)
        return comps - 1
```
- **Time:** O(n^2 + e) — **Space:** O(n^2)

## Approach 2 — Better
**Idea:** Use adjacency lists for component counting.
```python
class Solution:
    def makeConnected(self, n: int, connections: list[list[int]]) -> int:
        if len(connections) < n - 1:
            return -1
        adj = [[] for _ in range(n)]
        for a, b in connections:
            adj[a].append(b); adj[b].append(a)
        seen = [False] * n; comps = 0
        for i in range(n):
            if not seen[i]:
                comps += 1; seen[i] = True; stack = [i]
                while stack:
                    u = stack.pop()
                    for v in adj[u]:
                        if not seen[v]:
                            seen[v] = True; stack.append(v)
        return comps - 1
```
- **Time:** O(n + e) — **Space:** O(n + e)

## Approach 3 — Optimal
**Idea:** Union endpoints and count the remaining connected components.
```python
class Solution:
    def makeConnected(self, n: int, connections: list[list[int]]) -> int:
        if len(connections) < n - 1:
            return -1
        parent = list(range(n)); rank = [0] * n
        def find(x):
            while parent[x] != x:
                parent[x] = parent[parent[x]]; x = parent[x]
            return x
        comps = n
        for a, b in connections:
            ra, rb = find(a), find(b)
            if ra != rb:
                if rank[ra] < rank[rb]:
                    ra, rb = rb, ra
                parent[rb] = ra
                if rank[ra] == rank[rb]:
                    rank[ra] += 1
                comps -= 1
        return comps - 1
```
- **Time:** O((n + e) α(n)) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2 + e) | O(n^2) |
| Better | O(n + e) | O(n + e) |
| Optimal | O((n + e) α(n)) | O(n) |

## Edge Cases & Pitfalls
- Fewer than `n - 1` cables is impossible.
- Duplicate cables still count as spare cables.

## Related
- Union-Find
- Connected Components
