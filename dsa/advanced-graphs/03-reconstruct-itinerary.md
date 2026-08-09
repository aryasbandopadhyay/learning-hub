# 03. Reconstruct Itinerary

- **Difficulty:** Hard
- **Pattern:** Advanced Graphs
- **Asked at:** Google, Amazon, Meta, Microsoft

## Problem
You are given airline tickets as directed pairs `[from, to]`. Use every ticket exactly once and start the itinerary at `"JFK"`.

Return the valid itinerary with the smallest lexicographic order when read as a list of airport codes.

**Input**
- `tickets`: directed flights `[from, to]`.

**Output**
- A list of airport codes. **This judge compares exactly**, so when multiple complete itineraries exist, return the lexicographically smallest one.

## Constraints
- `1 <= tickets.length <= 300`
- `tickets[i].length == 2`
- Airport codes are three uppercase English letters.
- At least one itinerary uses all tickets from `"JFK"`.

## Examples
```text
Input: tickets = [["MUC","LHR"],["JFK","MUC"],["SFO","SJC"],["LHR","SFO"]]
Output: ["JFK","MUC","LHR","SFO","SJC"]
Explanation: Starting from `JFK`, the route `JFK -> MUC -> LHR -> SFO -> SJC` uses every ticket exactly once.
```

## Understanding & Intuition
Using every directed edge exactly once is an Eulerian path problem. Backtracking tries lexical choices until all tickets are consumed. Hierholzer's algorithm is optimal: consume edges in lexical order and add airports after exhausting outgoing edges.

## Approach 1 — Naive / Brute Force
**Idea:** Sort tickets and backtrack, marking each ticket as used.
```python
from typing import List

class Solution:
    def findItinerary(self, tickets: List[List[str]]) -> List[str]:
        tickets.sort()
        used = [False] * len(tickets)
        route = ["JFK"]

        def backtrack(airport: str) -> bool:
            if len(route) == len(tickets) + 1:
                return True
            for i, (src, dst) in enumerate(tickets):
                if not used[i] and src == airport:
                    used[i] = True
                    route.append(dst)
                    if backtrack(dst):
                        return True
                    route.pop()
                    used[i] = False
            return False

        backtrack("JFK")
        return route
```
- **Time:** O(E!) — **Space:** O(E)

## Approach 2 — Better
**Idea:** Store lexical adjacency lists and backtrack by removing one edge at a time.
```python
from collections import defaultdict
from typing import List

class Solution:
    def findItinerary(self, tickets: List[List[str]]) -> List[str]:
        graph = defaultdict(list)
        for src, dst in sorted(tickets):
            graph[src].append(dst)

        route = ["JFK"]

        def dfs(airport: str) -> bool:
            if len(route) == len(tickets) + 1:
                return True
            for i, dst in enumerate(list(graph[airport])):
                graph[airport].pop(i)
                route.append(dst)
                if dfs(dst):
                    return True
                route.pop()
                graph[airport].insert(i, dst)
            return False

        dfs("JFK")
        return route
```
- **Time:** O(E^2 * E!) worst case — **Space:** O(E)

## Approach 3 — Optimal
**Idea:** Reverse-sort destinations so `pop()` returns the smallest lexical edge, then perform Hierholzer DFS.
```python
from collections import defaultdict
from typing import List

class Solution:
    def findItinerary(self, tickets: List[List[str]]) -> List[str]:
        graph = defaultdict(list)
        for src, dst in sorted(tickets, reverse=True):
            graph[src].append(dst)

        route = []

        def visit(airport: str) -> None:
            while graph[airport]:
                visit(graph[airport].pop())  # smallest remaining destination
            route.append(airport)

        visit("JFK")
        return route[::-1]
```
- **Time:** O(E log E) — **Space:** O(E)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(E!) | O(E) |
| Better | O(E^2 * E!) | O(E) |
| Optimal | O(E log E) | O(E) |

## Edge Cases & Pitfalls
- Duplicate tickets are distinct edges.
- Lexicographic order must be decided among full valid itineraries, not just local-looking paths.
- Append to the Euler route after outgoing edges are exhausted.

## Related
- Eulerian Path
- Hierholzer's Algorithm

