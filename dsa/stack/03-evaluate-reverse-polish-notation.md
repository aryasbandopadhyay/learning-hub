# 03. Evaluate Reverse Polish Notation

- **Difficulty:** Medium
- **Pattern:** Stack
- **Asked at:** Amazon, Bloomberg, Google, LinkedIn

## Problem
Given tokens of an arithmetic expression in Reverse Polish Notation, evaluate the expression. Operators appear after their two operands, and integer division truncates toward zero.

**Input**
- `tokens`: a list of string tokens, each either an integer or one of `+`, `-`, `*`, `/`.

**Output**
- The integer value of the expression.

## Constraints
- `1 <= tokens.length <= 10^4`
- Each number token is an integer in `[-200, 200]`.
- The expression is valid RPN.
- Intermediate and final results fit in a 32-bit signed integer.

## Examples
```text
Input: tokens = ["2","1","+","3","*"]
Output: 9
Explanation: First compute `2 + 1 = 3`; then multiply by `3`, giving `9`.
```

## Understanding & Intuition
In RPN, operands appear before their operator. A stack stores pending operands; when an operator appears, the two most recent operands are combined. Operand order matters for subtraction and division.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly find an operator with two numbers before it, replace that triple with the result, and continue.
```python
class Solution:
    def evalRPN(self, tokens: list[str]) -> int:
        arr = tokens[:]
        ops = {"+", "-", "*", "/"}
        while len(arr) > 1:
            for i, tok in enumerate(arr):
                if tok in ops:
                    a, b = int(arr[i - 2]), int(arr[i - 1])
                    if tok == "+":
                        val = a + b
                    elif tok == "-":
                        val = a - b
                    elif tok == "*":
                        val = a * b
                    else:
                        val = int(a / b)  # Truncates toward zero.
                    arr[i - 2:i + 1] = [str(val)]
                    break
        return int(arr[0])
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a stack and branch on each operator.
```python
class Solution:
    def evalRPN(self, tokens: list[str]) -> int:
        stack = []
        for tok in tokens:
            if tok not in "+-*/":
                stack.append(int(tok))
                continue
            b = stack.pop()
            a = stack.pop()
            # Apply the operator to the earlier operand first.
            if tok == "+":
                stack.append(a + b)
            elif tok == "-":
                stack.append(a - b)
            elif tok == "*":
                stack.append(a * b)
            else:
                stack.append(int(a / b))
        return stack[-1]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Map operators to small functions for clean O(n) stack evaluation.
```python
class Solution:
    def evalRPN(self, tokens: list[str]) -> int:
        def div(a: int, b: int) -> int:
            return int(a / b)  # LeetCode requires truncation toward zero.

        ops = {
            "+": lambda a, b: a + b,
            "-": lambda a, b: a - b,
            "*": lambda a, b: a * b,
            "/": div,
        }
        stack = []
        for tok in tokens:
            if tok in ops:
                b = stack.pop()
                a = stack.pop()
                stack.append(ops[tok](a, b))
            else:
                stack.append(int(tok))
        return stack.pop()
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Division must truncate toward zero, not floor.
- Negative numbers are tokens like `"-11"`, not operators.
- Pop `b` first and `a` second, then compute `a op b`.

## Related
- Basic Calculator
- Basic Calculator II

