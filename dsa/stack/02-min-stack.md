# 02. Min Stack

- **Difficulty:** Medium
- **Pattern:** Stack
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Design a stack supporting `push`, `pop`, `top`, and `getMin`, where `getMin` returns the minimum element currently in the stack. Constraints: operations are valid, values fit in signed 32-bit integers, and up to `3 * 10^4` calls are made.

## Examples
```text
Input: ["MinStack","push","push","push","getMin","pop","top","getMin"], [[],[-2],[0],[-3],[],[],[],[]]
Output: [null,null,null,null,-3,null,0,-2]
Explanation: The minimum updates when -3 is pushed and reverts after it is popped.
```

## Understanding & Intuition
A normal stack can return the top quickly but not the minimum without scanning. To make `getMin` fast, store extra information about the current minimum. The usual optimal design keeps each operation O(1).

## Approach 1 — Naive / Brute Force
**Idea:** Store only the values and scan the whole stack for `getMin`.
```python
class MinStack:
    def __init__(self):
        self.stack = []

    def push(self, val: int) -> None:
        self.stack.append(val)

    def pop(self) -> None:
        self.stack.pop()

    def top(self) -> int:
        return self.stack[-1]

    def getMin(self) -> int:
        # Brute force: compute the minimum only when asked.
        return min(self.stack)
```
- **Time:** O(1) push/pop/top, O(n) getMin — **Space:** O(n)

## Approach 2 — Better
**Idea:** Keep a second stack of minimum values, pushing only when a new value is at most the current minimum.
```python
class MinStack:
    def __init__(self):
        self.stack = []
        self.mins = []

    def push(self, val: int) -> None:
        self.stack.append(val)
        if not self.mins or val <= self.mins[-1]:
            self.mins.append(val)

    def pop(self) -> None:
        val = self.stack.pop()
        # Pop from mins only when the removed value was the active minimum.
        if val == self.mins[-1]:
            self.mins.pop()

    def top(self) -> int:
        return self.stack[-1]

    def getMin(self) -> int:
        return self.mins[-1]
```
- **Time:** O(1) per operation — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Store `(value, current_min)` pairs so each stack entry knows the minimum at that depth.
```python
class MinStack:
    def __init__(self):
        self.stack = []

    def push(self, val: int) -> None:
        current_min = val if not self.stack else min(val, self.stack[-1][1])
        # Pair the value with the minimum after this push.
        self.stack.append((val, current_min))

    def pop(self) -> None:
        self.stack.pop()

    def top(self) -> int:
        return self.stack[-1][0]

    def getMin(self) -> int:
        return self.stack[-1][1]
```
- **Time:** O(1) per operation — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) getMin | O(n) |
| Better | O(1) | O(n) |
| Optimal | O(1) | O(n) |

## Edge Cases & Pitfalls
- Duplicate minimum values must be tracked correctly.
- Do not call operations on an empty stack unless the problem guarantees validity.
- Pair storage is simpler; separate min stack can use less extra memory in some inputs.

## Related
- Implement Queue using Stacks
- Valid Parentheses

