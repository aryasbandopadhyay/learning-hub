# 15. Implement Queue using Stacks

- **Difficulty:** Easy
- **Pattern:** Stack
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Design a first-in, first-out queue using only stack operations. Implement `push`, `pop`, `peek`, and `empty` with queue semantics.

**Input**
- `MyQueue()`: initializes an empty queue.
- `push(x)`: adds integer `x` to the back.
- `pop()`: removes and returns the front element.
- `peek()`: returns the front element without removing it.
- `empty()`: returns whether the queue has no elements.

**Output**
- Constructor and `push` return `null`; `pop` and `peek` return integers; `empty` returns a boolean. **This judge compares exactly** to the operation output sequence.

## Constraints
- `1 <= x <= 9`
- At most `100` operations are performed.
- `pop` and `peek` are called only when the queue is non-empty.
- Use only standard stack operations such as push, pop, peek/top, size, and empty.

## Examples
```text
Input: ["MyQueue","push","push","peek","pop","empty"], [[],[1],[2],[],[],[]]
Output: [null,null,null,1,1,false]
Explanation: Values `1` and `2` are pushed in order; `peek` and then `pop` both observe front value `1`, and the queue still contains `2`, so `empty` is false.
```

## Understanding & Intuition
A stack reverses order, while a queue preserves arrival order. Two stacks can reverse twice: one stack accepts new items and the other exposes the oldest items. Moving elements only when needed gives amortized O(1) operations.

## Approach 1 — Naive / Brute Force
**Idea:** On every `pop` or `peek`, move all items to a temporary stack to access the oldest value, then move them back.
```python
class MyQueue:
    def __init__(self):
        self.stack = []

    def push(self, x: int) -> None:
        self.stack.append(x)

    def pop(self) -> int:
        temp = []
        while self.stack:
            temp.append(self.stack.pop())
        val = temp.pop()
        while temp:
            self.stack.append(temp.pop())
        return val

    def peek(self) -> int:
        val = self.pop()
        # Restore the peeked value at the bottom/front of the stack.
        temp = []
        while self.stack:
            temp.append(self.stack.pop())
        self.stack.append(val)
        while temp:
            self.stack.append(temp.pop())
        return val

    def empty(self) -> bool:
        return not self.stack
```
- **Time:** O(n) pop/peek, O(1) push/empty — **Space:** O(n)

## Approach 2 — Better
**Idea:** Keep the queue order directly in one stack by reversing on every push.
```python
class MyQueue:
    def __init__(self):
        self.stack = []

    def push(self, x: int) -> None:
        temp = []
        while self.stack:
            temp.append(self.stack.pop())
        self.stack.append(x)
        while temp:
            self.stack.append(temp.pop())

    def pop(self) -> int:
        return self.stack.pop()

    def peek(self) -> int:
        return self.stack[-1]

    def empty(self) -> bool:
        return len(self.stack) == 0
```
- **Time:** O(n) push, O(1) pop/peek/empty — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use an input stack for pushes and an output stack for pops/peeks, transferring only when the output stack is empty.
```python
class MyQueue:
    def __init__(self):
        self.in_stack = []
        self.out_stack = []

    def push(self, x: int) -> None:
        self.in_stack.append(x)

    def _move(self) -> None:
        # Transfer once so the oldest item becomes the top of out_stack.
        if not self.out_stack:
            while self.in_stack:
                self.out_stack.append(self.in_stack.pop())

    def pop(self) -> int:
        self._move()
        return self.out_stack.pop()

    def peek(self) -> int:
        self._move()
        return self.out_stack[-1]

    def empty(self) -> bool:
        return not self.in_stack and not self.out_stack
```
- **Time:** Amortized O(1) per operation — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) pop/peek | O(n) |
| Better | O(n) push | O(n) |
| Optimal | Amortized O(1) | O(n) |

## Edge Cases & Pitfalls
- Do not transfer from input to output if output already has older items.
- `peek` must not remove the value.
- `empty` must check both stacks in the optimal design.

## Related
- Min Stack
- Valid Parentheses
