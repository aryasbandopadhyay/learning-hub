# 04. Largest Color Value in a Directed Graph

- **Difficulty:** Hard
- **Pattern:** graphs: topological sort & union-find
- **Asked at:** Google, Amazon, Microsoft

## Problem
Implement `largestPathValue` for **Largest Color Value in a Directed Graph**. Given `colors`, where `colors[i]` is node `i`'s lowercase color, and directed `edges`, the color value of a path is the maximum frequency of one color on that path. Return the largest color value over all paths, or `-1` if the graph has a cycle.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `colors`: string; node colors.
- `edges`: list; edge list or outgoing-edge list as defined above.

**Output**
- A single integer.

## Constraints
- `1 <= len(colors) <= 100000`, `0 <= len(edges) <= 100000`

## Examples
```text
Input: colors = "abaca", edges = [[0,1],[0,2],[2,3],[3,4]]
Output: 3
Explanation: Path 0 -> 2 -> 3 -> 4 contains three 'a' nodes. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
In a DAG, each node can keep 26 best color counts for paths ending there. Topological order guarantees predecessors are processed before successors. Any cycle invalidates the answer.

## Approach 1 — Naive / Brute Force
**Idea:** DFS with memoized 26-count vectors and recursion-stack cycle detection.
```python
class Solution:
    def largestPathValue(self, colors: str, edges: list[list[int]]) -> int:
        n = len(colors)
        g = [[] for _ in range(n)]
        for a, b in edges:
            g[a].append(b)
        state = [0] * n
        memo = [[0] * 26 for _ in range(n)]
        def dfs(u):
            if state[u] == 1:
                return None
            if state[u] == 2:
                return memo[u]
            state[u] = 1
            best = [0] * 26
            for v in g[u]:
                got = dfs(v)
                if got is None:
                    return None
                for c in range(26):
                    best[c] = max(best[c], got[c])
            best[ord(colors[u]) - 97] += 1
            memo[u] = best; state[u] = 2
            return best
        ans = 0
        for i in range(n):
            got = dfs(i)
            if got is None:
                return -1
            ans = max(ans, max(got))
        return ans
```
- **Time:** O(26(n + e)) — **Space:** O(26n + e)

## Approach 2 — Better
**Idea:** Kahn topological sort pushes each node's best 26 counts to successors.
```python
class Solution:
    def largestPathValue(self, colors: str, edges: list[list[int]]) -> int:
        from collections import deque
        n = len(colors)
        g = [[] for _ in range(n)]
        indeg = [0] * n
        for a, b in edges:
            g[a].append(b); indeg[b] += 1
        dp = [[0] * 26 for _ in range(n)]
        q = deque([i for i in range(n) if indeg[i] == 0])
        seen = ans = 0
        while q:
            u = q.popleft(); seen += 1
            dp[u][ord(colors[u]) - 97] += 1
            ans = max(ans, max(dp[u]))
            for v in g[u]:
                for c in range(26):
                    dp[v][c] = max(dp[v][c], dp[u][c])
                indeg[v] -= 1
                if indeg[v] == 0:
                    q.append(v)
        return ans if seen == n else -1
```
- **Time:** O(26(n + e)) — **Space:** O(26n + e)

## Approach 3 — Optimal
**Idea:** Use the same topological DP but process nodes from a min-heap for deterministic order without changing results.
```python
class Solution:
    def largestPathValue(self, colors: str, edges: list[list[int]]) -> int:
        import heapq
        n = len(colors)
        g = [[] for _ in range(n)]
        indeg = [0] * n
        for u, v in edges:
            g[u].append(v); indeg[v] += 1
        counts = [[0] * 26 for _ in range(n)]
        heap = [i for i in range(n) if indeg[i] == 0]
        heapq.heapify(heap)
        seen = ans = 0
        while heap:
            u = heapq.heappop(heap); seen += 1
            counts[u][ord(colors[u]) - 97] += 1
            ans = max(ans, max(counts[u]))
            for v in g[u]:
                for c in range(26):
                    counts[v][c] = max(counts[v][c], counts[u][c])
                indeg[v] -= 1
                if indeg[v] == 0:
                    heapq.heappush(heap, v)
        return ans if seen == n else -1
```
- **Time:** O(26(n + e) + n log n) — **Space:** O(26n + e)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(26(n + e)) | O(26n + e) |
| Better | O(26(n + e)) | O(26n + e) |
| Optimal | O(26(n + e) + n log n) | O(26n + e) |

## Edge Cases & Pitfalls
- A self-loop is a cycle and returns `-1`.
- Counts must include the current node exactly once.

## Related
- Longest Path in DAG
- Topological Dynamic Programming
