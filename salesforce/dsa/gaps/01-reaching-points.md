# 01. Reaching Points

- **Difficulty:** Hard
- **Pattern:** Math / Greedy
- **Asked at:** Salesforce, Google, Amazon

## Problem
Given `(sx, sy)` and `(tx, ty)`, decide whether moves `(x+y, y)` or `(x, x+y)` can reach the target.

## Examples
```text
Input: sx = 1, sy = 1, tx = 3, ty = 5
Output: true
Explanation: (1,1) -> (1,2) -> (3,2) -> (3,5).
```

## Understanding & Intuition
Forward search branches and only increases coordinates. Backward, the larger target coordinate must have been created by adding the smaller coordinate, so modulo skips repeated subtractions.

## Approach 1 — Naive / Brute Force
**Idea:** DFS reachable states while staying inside the target rectangle.
```python
class Solution:
    def reachingPoints(self, sx: int, sy: int, tx: int, ty: int) -> bool:
        stack = [(sx, sy)]
        seen = {(sx, sy)}
        while stack:
            x, y = stack.pop()
            if (x, y) == (tx, ty):
                return True
            for nx, ny in ((x + y, y), (x, x + y)):
                if nx <= tx and ny <= ty and (nx, ny) not in seen:
                    seen.add((nx, ny)); stack.append((nx, ny))
        return False
```
- **Time:** O(tx * ty) — **Space:** O(tx * ty)

## Approach 2 — Better
**Idea:** Reverse one subtraction at a time from the larger coordinate.
```python
class Solution:
    def reachingPoints(self, sx: int, sy: int, tx: int, ty: int) -> bool:
        while tx >= sx and ty >= sy and tx != ty:
            if tx > ty:
                tx -= ty
            else:
                ty -= tx
        return tx == sx and ty == sy
```
- **Time:** O(tx + ty) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Reverse repeated additions with modulo, then check the final aligned row or column.
```python
class Solution:
    def reachingPoints(self, sx: int, sy: int, tx: int, ty: int) -> bool:
        while tx > sx and ty > sy and tx != ty:
            if tx > ty:
                tx %= ty
            else:
                ty %= tx
        if tx == sx and ty >= sy:
            return (ty - sy) % sx == 0
        if ty == sy and tx >= sx:
            return (tx - sx) % sy == 0
        return tx == sx and ty == sy
```
- **Time:** O(log(max(tx, ty))) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(tx * ty) | O(tx * ty) |
| Better | O(tx + ty) | O(1) |
| Optimal | O(log(max(tx, ty))) | O(1) |

## Edge Cases & Pitfalls
- Stop modulo when one coordinate reaches its start value.
- Coordinates are positive, so modulo by zero is not a concern.
- Forward DFS is only illustrative and will time out.

## Related
- Water and Jug Problem
- Number of Steps to Reduce a Number to Zero
