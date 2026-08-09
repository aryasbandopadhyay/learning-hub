# 09. Baseball Game

- **Difficulty:** Medium
- **Pattern:** stack-based parsing & evaluation
- **Asked at:** Amazon, Google, Apple

## Problem
You are given a list of baseball scoring operations. An integer records a new score, `"+"` records the sum of the previous two valid scores, `"D"` records double the previous valid score, and `"C"` invalidates the previous valid score. Return the sum of all valid scores after processing every operation.

**Input**
- `ops`: a `list[str]`; the scoring operations.

**Output**
- A `int`. Return the sum of all valid scores after processing every operation.

## Constraints
- `1 <= len(ops) <= 10^4`.
- operations are valid and integer scores fit in 32-bit signed range.

## Examples
```text
Input: ops = ["5","2","C","D","+"]
Output: 30
Explanation: Scores become [5], [5,2], [5], [5,10], [5,10,15].
```

## Understanding & Intuition
Each operation depends only on recent valid scores, especially the last one or two. Invalidating a score also removes it from future consideration. A stack of valid scores gives constant-time access to exactly the needed history.

## Approach 1 — Naive / Brute Force
**Idea:** Keep all valid scores, but recompute the final total with a separate summation after all operations.
```python
class Solution:
    def calPoints(self, ops: list[str]) -> int:
        scores = []
        for op in ops:
            if op == 'C':
                scores = scores[:-1]
            elif op == 'D':
                scores.append(scores[-1] * 2)
            elif op == '+':
                scores.append(scores[-1] + scores[-2])
            else:
                scores.append(int(op))
        total = 0
        for x in scores:
            total += x
        return total
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a normal stack and Python's `sum` at the end.
```python
class Solution:
    def calPoints(self, ops: list[str]) -> int:
        stack = []
        for op in ops:
            if op == 'C':
                stack.pop()
            elif op == 'D':
                stack.append(2 * stack[-1])
            elif op == '+':
                stack.append(stack[-1] + stack[-2])
            else:
                stack.append(int(op))
        return sum(stack)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Maintain the running total alongside the score stack so cancellation and additions update the answer immediately.
```python
class Solution:
    def calPoints(self, ops: list[str]) -> int:
        stack = []
        total = 0
        for op in ops:
            if op == 'C':
                total -= stack.pop()
            elif op == 'D':
                val = 2 * stack[-1]
                stack.append(val)
                total += val
            elif op == '+':
                val = stack[-1] + stack[-2]
                stack.append(val)
                total += val
            else:
                val = int(op)
                stack.append(val)
                total += val
        return total
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Scores may be negative integers.
- `"C"` removes the score from both the stack and total.
- The `"+"` operation uses the previous two valid scores, not the previous two operations.

## Related
- Evaluate Reverse Polish Notation
- Basic Calculator II
- Validate Stack Sequences
