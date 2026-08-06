# 02. Brick Wall

- **Difficulty:** Medium
- **Pattern:** hashing / hashmap counting & grouping
- **Asked at:** Facebook, Amazon, Bloomberg

## Problem
You are given a rectangular wall represented by rows of brick widths. A vertical line drawn from top to bottom crosses a brick unless it passes exactly through a brick edge. The line cannot be drawn along the far left or far right border of the wall.

Return the minimum number of bricks crossed by one vertical line.

Constraints: `1 <= len(wall) <= 10^4`, `1 <= len(wall[i])`, all rows have the same total width, and every brick width is positive.

## Examples
```text
Input: wall = [[1, 2, 2, 1], [3, 1, 2], [1, 3, 2], [2, 4], [3, 1, 2], [1, 3, 1, 1]]
Output: 2
Explanation: The best line passes through four internal edges, so it crosses 6 - 4 = 2 bricks.
```

## Understanding & Intuition
A line crosses fewer bricks when it aligns with more internal brick edges. Therefore, count how many rows have an edge at each prefix width, excluding the right boundary. The answer is total rows minus the largest edge frequency.

## Approach 1 — Naive / Brute Force
**Idea:** Try every possible position inside the wall and count how many rows do not have an edge there.
```python
class Solution:
    def leastBricks(self, wall: list[list[int]]) -> int:
        width = sum(wall[0])
        best = len(wall)
        for pos in range(1, width):
            crossed = 0
            for row in wall:
                acc = 0
                has_edge = False
                for brick in row[:-1]:
                    acc += brick
                    if acc == pos:
                        has_edge = True
                        break
                    if acc > pos:
                        break
                if not has_edge:
                    crossed += 1
            best = min(best, crossed)
        return best
```
- **Time:** O(W · B) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Build a list of all internal edge positions, sort it, and count the longest equal run.
```python
class Solution:
    def leastBricks(self, wall):
        edges = []
        for row in wall:
            acc = 0
            for brick in row[:-1]:
                acc += brick
                edges.append(acc)
        if not edges:
            return len(wall)
        edges.sort()
        best = cur = 1
        for i in range(1, len(edges)):
            if edges[i] == edges[i - 1]:
                cur += 1
            else:
                best = max(best, cur)
                cur = 1
        best = max(best, cur)
        return len(wall) - best
```
- **Time:** O(B log B) — **Space:** O(B)

## Approach 3 — Optimal
**Idea:** Count each internal edge position directly in a hashmap and keep the highest frequency.
```python
class Solution:
    def leastBricks(self, wall):
        counts = {}
        best_edges = 0
        for row in wall:
            acc = 0
            for brick in row[:-1]:
                acc += brick
                counts[acc] = counts.get(acc, 0) + 1
                best_edges = max(best_edges, counts[acc])
        return len(wall) - best_edges
```
- **Time:** O(B) — **Space:** O(B)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(W · B) | O(1) |
| Better | O(B log B) | O(B) |
| Optimal | O(B) | O(B) |

## Edge Cases & Pitfalls
- Do not count the right border as an allowed edge.
- If every row has one brick, the answer is the number of rows.
- Width can be large, so enumerating positions is only the brute-force baseline.

## Related
- Subarray Sum Equals K
- Maximum Frequency Stack
