# 06. Alien Dictionary

- **Difficulty:** Hard
- **Pattern:** Advanced Graphs
- **Asked at:** Google, Meta, Amazon, Airbnb

## Problem
You are given words sorted according to an unknown alien alphabet. Adjacent words reveal ordering constraints between characters.

Return one valid ordering containing every distinct character, or `""` if the ordering is impossible.

**Input**
- `words`: words sorted by the alien alphabet.

**Output**
- A string with each distinct character once in a valid order, or `""` if no valid order exists.

## Constraints
- `1 <= words.length <= 100`
- `1 <= words[i].length <= 100`
- `words[i]` contains lowercase English letters.

## Examples
```text
Input: words = ["wrt","wrf","er","ett","rftt"]
Output: "wertf"
Explanation: Adjacent comparisons imply `w < e`, `e < r`, `r < t`, and `t < f`. The order `wertf` satisfies all constraints.
```

## Understanding & Intuition
The first differing character between adjacent words creates a directed precedence edge. The answer is a topological ordering of characters. Brute force can try permutations, but DFS or Kahn's BFS topological sort is the practical solution.

## Approach 1 — Naive / Brute Force
**Idea:** Generate all character permutations and return the first one satisfying every precedence edge.
```python
from itertools import permutations
from typing import List

class Solution:
    def alienOrder(self, words: List[str]) -> str:
        chars = sorted(set("".join(words)))
        edges = []
        for a, b in zip(words, words[1:]):
            if len(a) > len(b) and a.startswith(b):
                return ""
            for x, y in zip(a, b):
                if x != y:
                    edges.append((x, y))
                    break

        for perm in permutations(chars):
            pos = {ch: i for i, ch in enumerate(perm)}
            if all(pos[u] < pos[v] for u, v in edges):
                return "".join(perm)
        return ""
```
- **Time:** O(C! * E) — **Space:** O(C + E)

## Approach 2 — Better
**Idea:** Use DFS topological sort with colors to detect cycles.
```python
from collections import defaultdict
from typing import List

class Solution:
    def alienOrder(self, words: List[str]) -> str:
        graph = {ch: set() for word in words for ch in word}
        for a, b in zip(words, words[1:]):
            if len(a) > len(b) and a.startswith(b):
                return ""
            for x, y in zip(a, b):
                if x != y:
                    graph[x].add(y)
                    break

        color = defaultdict(int)  # 0=unvisited, 1=visiting, 2=done
        order = []

        def dfs(ch: str) -> bool:
            if color[ch] == 1:
                return False
            if color[ch] == 2:
                return True
            color[ch] = 1
            for nxt in graph[ch]:
                if not dfs(nxt):
                    return False
            color[ch] = 2
            order.append(ch)
            return True

        for ch in graph:
            if not dfs(ch):
                return ""
        return "".join(reversed(order))
```
- **Time:** O(C + E) — **Space:** O(C + E)

## Approach 3 — Optimal
**Idea:** Kahn's algorithm repeatedly removes zero-indegree characters and builds a topological order.
```python
from collections import deque
from typing import List

class Solution:
    def alienOrder(self, words: List[str]) -> str:
        graph = {ch: set() for word in words for ch in word}
        indegree = {ch: 0 for ch in graph}

        for a, b in zip(words, words[1:]):
            if len(a) > len(b) and a.startswith(b):
                return ""
            for x, y in zip(a, b):
                if x != y:
                    if y not in graph[x]:
                        graph[x].add(y)
                        indegree[y] += 1
                    break

        queue = deque([ch for ch in graph if indegree[ch] == 0])
        order = []
        while queue:
            ch = queue.popleft()
            order.append(ch)
            for nxt in graph[ch]:
                indegree[nxt] -= 1
                if indegree[nxt] == 0:
                    queue.append(nxt)

        return "".join(order) if len(order) == len(graph) else ""
```
- **Time:** O(C + E) — **Space:** O(C + E)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(C! * E) | O(C + E) |
| Better | O(C + E) | O(C + E) |
| Optimal | O(C + E) | O(C + E) |

## Edge Cases & Pitfalls
- `"abc"` before `"ab"` is invalid.
- Only the first differing character between adjacent words creates an edge.
- Include characters that have no edges.

## Related
- Course Schedule
- Topological Sort

