# 08. Validate Stack Sequences

- **Difficulty:** Medium
- **Pattern:** stack-based parsing & evaluation
- **Asked at:** Amazon, Google, Meta

## Problem
Given two integer arrays `pushed` and `popped` with distinct values, determine whether they could be the push and pop order of a single stack.

**Input**
- `pushed`: a `list[int]`; the pushed sequence.
- `popped`: a `list[int]`; the popped sequence.

**Output**
- A `bool`. Return the value produced by `validateStackSequences`.

## Constraints
- `1 <= len(pushed) == len(popped) <= 10^5`.
- values are distinct.

## Examples
```text
Input: pushed = [1,2,3,4,5], popped = [4,5,3,2,1]
Output: True
Explanation: Push 1,2,3,4; pop 4; push 5; then pop 5,3,2,1.
```

## Understanding & Intuition
The next popped value must be at the top of the stack when it is removed. Simulating pushes and greedily popping whenever possible is forced; there is no benefit to delaying a valid pop. This makes a stack simulation both intuitive and complete.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively try all legal push/pop choices and see whether the target popped sequence can be produced.
```python
class Solution:
    def validateStackSequences(self, pushed: list[int], popped: list[int]) -> bool:
        n = len(pushed)
        seen = set()
        def dfs(i, j, stack):
            state = (i, j, tuple(stack))
            if state in seen:
                return False
            seen.add(state)
            if j == n:
                return True
            if stack and stack[-1] == popped[j] and dfs(i, j + 1, stack[:-1]):
                return True
            if i < n and dfs(i + 1, j, stack + [pushed[i]]):
                return True
            return False
        return dfs(0, 0, [])
```
- **Time:** O(2^n) — **Space:** O(2^n)

## Approach 2 — Better
**Idea:** Simulate the process with an auxiliary stack and a pointer into `popped`.
```python
class Solution:
    def validateStackSequences(self, pushed: list[int], popped: list[int]) -> bool:
        stack = []
        j = 0
        for x in pushed:
            stack.append(x)
            while stack and stack[-1] == popped[j]:
                stack.pop()
                j += 1
        return j == len(popped)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Reuse the `pushed` array itself as the stack buffer and keep only the simulated stack size.
```python
class Solution:
    def validateStackSequences(self, pushed: list[int], popped: list[int]) -> bool:
        top = 0
        j = 0
        for x in pushed:
            pushed[top] = x
            top += 1
            while top and pushed[top - 1] == popped[j]:
                top -= 1
                j += 1
        return top == 0
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(2^n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Values are distinct, so equality to the next pop target is unambiguous.
- Pop greedily as soon as the top matches; postponing cannot help.
- The in-place approach mutates `pushed`, which is fine because only the return value matters.

## Related
- Exclusive Time of Functions
- Min Stack
- Implement Queue Using Stacks
