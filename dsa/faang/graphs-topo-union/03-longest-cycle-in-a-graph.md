# 03. Longest Cycle in a Graph

- **Difficulty:** Hard
- **Pattern:** graphs: topological sort & union-find
- **Asked at:** Google, Meta, Amazon

## Problem
You are given a directed graph where each node has at most one outgoing edge. `edges[i]` is the node that `i` points to, or `-1` if it has no outgoing edge. Return the length of the longest directed cycle, or `-1` if no cycle exists.

Constraints: `1 <= len(edges) <= 100000`, each value is `-1` or a valid node index.

## Examples
```text
Input: edges = [3,3,4,2,3]
Output: 3
Explanation: The longest cycle is 2 -> 4 -> 3 -> 2.
```

## Understanding & Intuition
A functional graph is made of chains feeding into cycles. Following pointers from an unvisited node either exits the graph or discovers one cycle. Timestamps let us compute the cycle length exactly when a node is revisited in the same walk.

## Approach 1 — Naive / Brute Force
**Idea:** Start from every node and follow pointers while recording positions in that single walk.
```python
class Solution:
    def longestCycle(self, edges: list[int]) -> int:
        n = len(edges)
        ans = -1
        for s in range(n):
            pos = {}
            u = s
            step = 0
            while u != -1 and u not in pos and step <= n:
                pos[u] = step
                step += 1
                u = edges[u]
            if u in pos:
                ans = max(ans, step - pos[u])
        return ans
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Kahn-trim all nodes not in cycles, then count each remaining cycle.
```python
class Solution:
    def longestCycle(self, edges: list[int]) -> int:
        from collections import deque
        n = len(edges)
        indeg = [0] * n
        for v in edges:
            if v != -1:
                indeg[v] += 1
        q = deque([i for i in range(n) if indeg[i] == 0])
        removed = [False] * n
        while q:
            u = q.popleft(); removed[u] = True
            v = edges[u]
            if v != -1:
                indeg[v] -= 1
                if indeg[v] == 0:
                    q.append(v)
        seen = [False] * n
        ans = -1
        for i in range(n):
            if not removed[i] and not seen[i]:
                cur = i; length = 0
                while not seen[cur]:
                    seen[cur] = True; length += 1; cur = edges[cur]
                ans = max(ans, length)
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use one global timestamp array; a revisited node whose timestamp is inside the current walk starts a cycle.
```python
class Solution:
    def longestCycle(self, edges: list[int]) -> int:
        n = len(edges)
        seen = [0] * n
        time = 1
        ans = -1
        for i in range(n):
            if seen[i]:
                continue
            start = time
            u = i
            while u != -1 and seen[u] == 0:
                seen[u] = time
                time += 1
                u = edges[u]
            if u != -1 and seen[u] >= start:
                ans = max(ans, time - seen[u])
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- A self-loop has length `1`.
- Do not count a cycle discovered by a previous walk as new.

## Related
- Functional Graphs
- Eventual Safe States
