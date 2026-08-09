# 02. Min Stack

- **Difficulty:** Medium
- **Pattern:** Stack
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Design a stack that supports normal stack operations plus retrieving the current minimum element in constant time.

**Input**
- `MinStack()`: initializes an empty stack.
- `push(val)`: pushes integer `val`.
- `pop()`: removes the top element.
- `top()`: returns the top element.
- `getMin()`: returns the minimum value currently in the stack.

**Output**
- Constructor, `push`, and `pop` return `null`; `top` returns the current top; `getMin` returns the current minimum. **This judge compares exactly** to the operation output sequence.

## Constraints
- `-2^31 <= val <= 2^31 - 1`
- At most `3 * 10^4` operations are performed.
- `pop`, `top`, and `getMin` are called only when the stack is non-empty.
- Each operation should run in `O(1)` time.

## Examples
```text
Input: ["MinStack","push","push","push","getMin","pop","top","getMin"], [[],[-2],[0],[-3],[],[],[],[]]
Output: [null,null,null,null,-3,null,0,-2]
Explanation: After pushing `-2`, `0`, and `-3`, the minimum is `-3`. Popping removes `-3`, so the top is `0` and the minimum returns to `-2`.
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

