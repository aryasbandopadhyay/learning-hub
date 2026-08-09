# 14. Basic Calculator II

- **Difficulty:** Medium
- **Pattern:** Stack
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Evaluate an arithmetic expression containing non-negative integers, `+`, `-`, `*`, `/`, and spaces. Multiplication and division have higher precedence, and integer division truncates toward zero.

**Input**
- `s`: a valid expression string without parentheses.

**Output**
- The integer result of evaluating the expression.

## Constraints
- `1 <= s.length <= 3 * 10^5`
- `s` consists of digits, `+`, `-`, `*`, `/`, and spaces.
- The expression is valid.
- All intermediate results fit in a 32-bit signed integer.

## Examples
```text
Input: s = "3+2*2"
Output: 7
Explanation: Multiplication is evaluated first: `2 * 2 = 4`, then `3 + 4 = 7`.
```

## Understanding & Intuition
Multiplication and division should be applied before addition and subtraction. A stack can store signed terms; when `*` or `/` appears, combine immediately with the previous term. Summing the stack gives the final result.

## Approach 1 — Naive / Brute Force
**Idea:** Tokenize, repeatedly reduce all `*` and `/`, then reduce `+` and `-`.
```python
class Solution:
    def calculate(self, s: str) -> int:
        tokens = []
        i = 0
        while i < len(s):
            if s[i] == " ":
                i += 1
            elif s[i].isdigit():
                num = 0
                while i < len(s) and s[i].isdigit():
                    num = num * 10 + int(s[i])
                    i += 1
                tokens.append(num)
            else:
                tokens.append(s[i])
                i += 1
        i = 1
        while i < len(tokens) - 1:
            if tokens[i] in "*/":
                a, b = tokens[i - 1], tokens[i + 1]
                val = a * b if tokens[i] == "*" else int(a / b)
                tokens[i - 1:i + 2] = [val]
            else:
                i += 2
        result = tokens[0]
        for i in range(1, len(tokens), 2):
            result = result + tokens[i + 1] if tokens[i] == "+" else result - tokens[i + 1]
        return result
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Push signed numbers and immediately fold multiplication or division into the top stack term.
```python
class Solution:
    def calculate(self, s: str) -> int:
        stack = []
        num = 0
        op = "+"
        for ch in s + "+":
            if ch.isdigit():
                num = num * 10 + int(ch)
            elif ch != " ":
                if op == "+":
                    stack.append(num)
                elif op == "-":
                    stack.append(-num)
                elif op == "*":
                    stack.append(stack.pop() * num)
                else:
                    stack.append(int(stack.pop() / num))
                op = ch
                num = 0
        return sum(stack)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Keep the running total of completed low-precedence terms and the current last term.
```python
class Solution:
    def calculate(self, s: str) -> int:
        total = 0
        last = 0
        num = 0
        op = "+"
        for ch in s + "+":
            if ch.isdigit():
                num = num * 10 + int(ch)
            elif ch != " ":
                if op == "+":
                    total += last
                    last = num
                elif op == "-":
                    total += last
                    last = -num
                elif op == "*":
                    last *= num
                else:
                    last = int(last / num)
                op = ch
                num = 0
        return total + last
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Append a sentinel operator to process the final number.
- Division must truncate toward zero.
- Spaces should not trigger evaluation.

## Related
- Basic Calculator
- Evaluate Reverse Polish Notation

