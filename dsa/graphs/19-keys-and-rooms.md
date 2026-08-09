# 19. Keys and Rooms

- **Difficulty:** Medium
- **Pattern:** Graphs
- **Asked at:** Amazon, Google, Meta, Airbnb

## Problem
There are `n` rooms numbered `0` through `n - 1`. `rooms[i]` lists keys found in room `i`; a key lets you enter that numbered room. You start in room `0`.

Return whether every room can be visited.

**Input**
- `rooms`: a list where `rooms[i]` contains keys in room `i`.

**Output**
- `True` if all rooms are reachable from room `0`; otherwise `False`.

## Constraints
- `n == rooms.length`
- `2 <= n <= 1000`
- `0 <= rooms[i].length <= 1000`
- `1 <= sum(rooms[i].length) <= 3000`
- `0 <= rooms[i][j] < n`.

## Examples
```text
Input: rooms = [[1],[2],[3],[]]
Output: True
Explanation: Room 0 gives a key to 1, room 1 gives a key to 2, and room 2 gives a key to 3. All rooms become reachable.
```

## Understanding & Intuition
Keys form directed edges. The task is reachability from room 0.

## Approach 1 — Naive / Brute Force
**Idea:** Use direct graph exploration from each relevant start; this is the conceptual baseline.
```python
from typing import List

class Solution:
    def canVisitAllRooms(self, rooms: List[List[int]]) -> bool:
        # DFS follows keys as directed edges.
        seen, stack = {0}, [0]
        while stack:
            room = stack.pop()
            for key in rooms[room]:
                if key not in seen:
                    seen.add(key); stack.append(key)
        return len(seen) == len(rooms)
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 2 — Better
**Idea:** Keep explicit visited/state so each node or cell is processed predictably.
```python
from typing import List

class Solution:
    def canVisitAllRooms(self, rooms: List[List[int]]) -> bool:
        # DFS follows keys as directed edges.
        seen, stack = {0}, [0]
        while stack:
            room = stack.pop()
            for key in rooms[room]:
                if key not in seen:
                    seen.add(key); stack.append(key)
        return len(seen) == len(rooms)
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Approach 3 — Optimal
**Idea:** Apply the standard optimal BFS/DFS/union-find/topological-sort pattern for this problem.
```python
from typing import List

class Solution:
    def canVisitAllRooms(self, rooms: List[List[int]]) -> bool:
        # DFS follows keys as directed edges.
        seen, stack = {0}, [0]
        while stack:
            room = stack.pop()
            for key in rooms[room]:
                if key not in seen:
                    seen.add(key); stack.append(key)
        return len(seen) == len(rooms)
```
- **Time:** O(V+E) or O(mn) — **Space:** O(V) or O(mn)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(V+E) or O(mn) | O(V) or O(mn) |
| Better | O(V+E) or O(mn) | O(V) or O(mn) |
| Optimal | O(V+E) or O(mn) | O(V) or O(mn) |

## Edge Cases & Pitfalls
- Empty or singleton graphs/grids.
- Mark visited before repeated traversal creates cycles.
- Preserve required in-place behavior when the signature returns None.

## Related
- BFS
- DFS
- Union-Find / Topological Sort
