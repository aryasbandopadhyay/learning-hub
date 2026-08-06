# 02. Find Eventual Safe States

- **Difficulty:** Medium
- **Pattern:** graphs: topological sort & union-find
- **Asked at:** Google, Amazon, Bloomberg

## Problem
Given a directed graph as `graph`, where `graph[i]` lists outgoing neighbors of node `i`, return all eventual safe nodes in increasing order. A node is safe if every path starting from it eventually reaches a terminal node.

Constraints: `1 <= len(graph) <= 10000`, total edges `<= 40000`.

## Examples
```text
Input: graph = [[1,2],[2,3],[5],[0],[5],[],[]]
Output: [2,4,5,6]
Explanation: Nodes 2, 4, 5, and 6 cannot reach the cycle 0 -> 1 -> 3 -> 0.
```

## Understanding & Intuition
Unsafe nodes are exactly nodes that can reach a directed cycle. Safe nodes have all outgoing paths terminating. Reverse topological trimming starts from terminal nodes and works backward.

## Approach 1 — Naive / Brute Force
**Idea:** For every start node, DFS with a recursion-path set and reject starts that hit a back edge.
```python
class Solution:
    def eventualSafeNodes(self, graph: list[list[int]]) -> list[int]:
        n = len(graph)
        def ok(s):
            path = set()
            def dfs(u):
                if u in path:
                    return False
                path.add(u)
                for v in graph[u]:
                    if not dfs(v):
                        path.remove(u)
                        return False
                path.remove(u)
                return True
            return dfs(s)
        return [i for i in range(n) if ok(i)]
```
- **Time:** O(n(n + e)) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize DFS states: unknown, visiting, safe, and unsafe.
```python
class Solution:
    def eventualSafeNodes(self, graph: list[list[int]]) -> list[int]:
        n = len(graph)
        state = [0] * n
        def dfs(u):
            if state[u]:
                return state[u] == 2
            state[u] = 1
            for v in graph[u]:
                if state[v] == 1 or not dfs(v):
                    state[u] = 3
                    return False
            state[u] = 2
            return True
        return [i for i in range(n) if dfs(i)]
```
- **Time:** O(n + e) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Reverse all edges and peel nodes whose remaining outgoing degree becomes zero.
```python
class Solution:
    def eventualSafeNodes(self, graph: list[list[int]]) -> list[int]:
        from collections import deque
        n = len(graph)
        rev = [[] for _ in range(n)]
        out = [0] * n
        for u, nbrs in enumerate(graph):
            out[u] = len(nbrs)
            for v in nbrs:
                rev[v].append(u)
        q = deque([i for i in range(n) if out[i] == 0])
        safe = [False] * n
        while q:
            u = q.popleft(); safe[u] = True
            for p in rev[u]:
                out[p] -= 1
                if out[p] == 0:
                    q.append(p)
        return [i for i in range(n) if safe[i]]
```
- **Time:** O(n + e) — **Space:** O(n + e)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n(n + e)) | O(n) |
| Better | O(n + e) | O(n) |
| Optimal | O(n + e) | O(n + e) |

## Edge Cases & Pitfalls
- Terminal nodes are always safe.
- Return sorted output by scanning indices at the end.

## Related
- Directed Cycle Detection
- Topological Sorting
