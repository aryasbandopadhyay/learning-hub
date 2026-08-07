# 05. Flatten 2D Vector

- **Difficulty:** Medium
- **Pattern:** Iterator / Two Pointers
- **Asked at:** Salesforce, Google, Airbnb

## Problem
Implement an iterator over a 2D vector with `next()` and `hasNext()`.

## Examples
```text
Input: vec = [[1,2], [3], [4]]
Output: [1,2,3,4]
Explanation: Values are returned in row-major order.
```

## Understanding & Intuition
The iterator must skip empty rows and keep enough state to resume where the previous call stopped.

## Approach 1 — Naive / Brute Force
**Idea:** Flatten all values during construction.
```python
class Vector2D:
    def __init__(self, vec: list[list[int]]):
        self.values = [x for row in vec for x in row]; self.index = 0
    def next(self) -> int:
        value = self.values[self.index]; self.index += 1; return value
    def hasNext(self) -> bool:
        return self.index < len(self.values)
```
- **Time:** O(n) init, O(1) operations — **Space:** O(n)

## Approach 2 — Better
**Idea:** Store row and column pointers, skipping empty rows lazily.
```python
class Vector2D:
    def __init__(self, vec: list[list[int]]):
        self.vec = vec; self.row = 0; self.col = 0
    def hasNext(self) -> bool:
        while self.row < len(self.vec) and self.col == len(self.vec[self.row]):
            self.row += 1; self.col = 0
        return self.row < len(self.vec)
    def next(self) -> int:
        if not self.hasNext(): raise StopIteration
        value = self.vec[self.row][self.col]; self.col += 1; return value
```
- **Time:** O(1) amortized — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Normalize position before both operations to make repeated `hasNext()` calls safe.
```python
class Vector2D:
    def __init__(self, vec: list[list[int]]):
        self.vec = vec; self.row = 0; self.col = 0
    def _advance(self) -> None:
        while self.row < len(self.vec) and self.col >= len(self.vec[self.row]):
            self.row += 1; self.col = 0
    def next(self) -> int:
        self._advance(); value = self.vec[self.row][self.col]; self.col += 1; return value
    def hasNext(self) -> bool:
        self._advance(); return self.row < len(self.vec)
```
- **Time:** O(1) amortized — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) init, O(1) operations | O(n) |
| Better | O(1) amortized | O(1) |
| Optimal | O(1) amortized | O(1) |

## Edge Cases & Pitfalls
- Empty rows can appear anywhere.
- `hasNext()` must not consume values.
- Avoid flattening when memory matters.

## Related
- Flatten Nested List Iterator
- Peeking Iterator
