# 26. Flatten Nested List Iterator

- **Difficulty:** Medium
- **Pattern:** Stack / Iterator
- **Asked at:** Salesforce, Google, Facebook

## Problem
Implement `NestedIterator` with `next()` and `hasNext()` over a nested list of integers.

## Examples
```text
Input: nestedList = [[1,1],2,[1,1]]
Output: [1,1,2,1,1]
Explanation: Values are flattened left to right.
```

## Understanding & Intuition
Flattening can be eager or lazy. A reversed stack lets `hasNext()` expand nested lists only until the top is an integer.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively flatten everything at construction.
```python
class NestedInteger:
    def isInteger(self) -> bool: ...
    def getInteger(self) -> int: ...
    def getList(self) -> list['NestedInteger']: ...
class NestedIterator:
    def __init__(self, nestedList: list[NestedInteger]):
        self.values = []
        def flat(items: list[NestedInteger]) -> None:
            for item in items:
                if item.isInteger(): self.values.append(item.getInteger())
                else: flat(item.getList())
        flat(nestedList); self.i = 0
    def next(self) -> int:
        val = self.values[self.i]; self.i += 1; return val
    def hasNext(self) -> bool:
        return self.i < len(self.values)
```
- **Time:** O(n) init, O(1) operations — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a generator and one-value buffer.
```python
class NestedIterator:
    def __init__(self, nestedList: list[NestedInteger]):
        self.it = self._flat(nestedList); self.peek = None; self.ready = False
    def _flat(self, items: list[NestedInteger]):
        for item in items:
            if item.isInteger(): yield item.getInteger()
            else: yield from self._flat(item.getList())
    def hasNext(self) -> bool:
        if self.ready: return True
        try:
            self.peek = next(self.it); self.ready = True; return True
        except StopIteration: return False
    def next(self) -> int:
        if not self.hasNext(): raise StopIteration
        self.ready = False; return self.peek
```
- **Time:** O(1) amortized — **Space:** O(d)

## Approach 3 — Optimal
**Idea:** Expand nested lists on demand with an explicit stack.
```python
class NestedIterator:
    def __init__(self, nestedList: list[NestedInteger]):
        self.stack = nestedList[::-1]
    def next(self) -> int:
        if not self.hasNext(): raise StopIteration
        return self.stack.pop().getInteger()
    def hasNext(self) -> bool:
        while self.stack:
            top = self.stack[-1]
            if top.isInteger(): return True
            self.stack.pop(); self.stack.extend(top.getList()[::-1])
        return False
```
- **Time:** O(1) amortized — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) init, O(1) operations | O(n) |
| Better | O(1) amortized | O(d) |
| Optimal | O(1) amortized | O(n) |

## Edge Cases & Pitfalls
- `hasNext()` must not consume an integer.
- Empty nested lists should be skipped.
- Push child lists in reverse order.

## Related
- Flatten 2D Vector
- Peeking Iterator
