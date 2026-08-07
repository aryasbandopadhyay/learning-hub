# 09. Design Snake Game

- **Difficulty:** Medium
- **Pattern:** Design / Queue
- **Asked at:** Salesforce, Google, Amazon

## Problem
Design a snake game. `move(direction)` returns score or `-1` if the snake hits a wall or itself.

## Examples
```text
Input: SnakeGame(3, 2, [[1,2],[0,1]]), move("R"), move("D"), move("R"), move("U"), move("L")
Output: [null,0,0,1,1,2]
Explanation: The score increases when the next food cell is reached.
```

## Understanding & Intuition
The body needs ordered head/tail updates and fast collision checks. Remove the tail before checking collision when the snake does not grow.

## Approach 1 — Naive / Brute Force
**Idea:** Keep the body in a list and scan for self-collision.
```python
class SnakeGame:
    def __init__(self, width: int, height: int, food: list[list[int]]):
        self.width = width; self.height = height; self.food = food; self.i = 0; self.body = [(0, 0)]
        self.d = {"U": (-1, 0), "D": (1, 0), "L": (0, -1), "R": (0, 1)}
    def move(self, direction: str) -> int:
        dr, dc = self.d[direction]; head = (self.body[0][0] + dr, self.body[0][1] + dc)
        if head[0] < 0 or head[0] >= self.height or head[1] < 0 or head[1] >= self.width: return -1
        grow = self.i < len(self.food) and self.food[self.i] == [head[0], head[1]]
        if head in (self.body if grow else self.body[:-1]): return -1
        self.body.insert(0, head)
        if grow: self.i += 1
        else: self.body.pop()
        return self.i
```
- **Time:** O(n) per move — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a deque for O(1) head and tail updates, but scan for collision.
```python
from collections import deque
class SnakeGame:
    def __init__(self, width: int, height: int, food: list[list[int]]):
        self.width = width; self.height = height; self.food = food; self.i = 0; self.body = deque([(0, 0)])
        self.d = {"U": (-1, 0), "D": (1, 0), "L": (0, -1), "R": (0, 1)}
    def move(self, direction: str) -> int:
        dr, dc = self.d[direction]; head = (self.body[0][0] + dr, self.body[0][1] + dc)
        if not (0 <= head[0] < self.height and 0 <= head[1] < self.width): return -1
        grow = self.i < len(self.food) and self.food[self.i] == [head[0], head[1]]
        tail = None if grow else self.body.pop()
        if head in self.body: return -1
        self.body.appendleft(head)
        if grow: self.i += 1
        return self.i
```
- **Time:** O(n) per move — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Pair the deque with a set of occupied cells for O(1) collision checks.
```python
from collections import deque
class SnakeGame:
    def __init__(self, width: int, height: int, food: list[list[int]]):
        self.width = width; self.height = height; self.food = food; self.i = 0
        self.body = deque([(0, 0)]); self.seen = {(0, 0)}
        self.d = {"U": (-1, 0), "D": (1, 0), "L": (0, -1), "R": (0, 1)}
    def move(self, direction: str) -> int:
        dr, dc = self.d[direction]; nr, nc = self.body[0][0] + dr, self.body[0][1] + dc
        if nr < 0 or nr >= self.height or nc < 0 or nc >= self.width: return -1
        grow = self.i < len(self.food) and self.food[self.i] == [nr, nc]
        if not grow: self.seen.remove(self.body.pop())
        if (nr, nc) in self.seen: return -1
        self.body.appendleft((nr, nc)); self.seen.add((nr, nc))
        if grow: self.i += 1
        return self.i
```
- **Time:** O(1) per move — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) per move | O(n) |
| Better | O(n) per move | O(n) |
| Optimal | O(1) per move | O(n) |

## Edge Cases & Pitfalls
- Remove the tail before collision check if not growing.
- Constructor uses width and height, but positions are row and column.
- Food is consumed in order.

## Related
- Design Tic-Tac-Toe
- LRU Cache
