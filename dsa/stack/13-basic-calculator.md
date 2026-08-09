# 13. Basic Calculator

- **Difficulty:** Hard
- **Pattern:** Stack
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Evaluate an arithmetic expression containing non-negative integers, `+`, `-`, parentheses, and spaces. Parentheses may be nested and determine grouping.

**Input**
- `s`: a valid expression string.

**Output**
- The integer result of evaluating the expression. **This judge compares exactly**.

## Constraints
- `1 <= s.length <= 3 * 10^5`
- `s` consists of digits, `+`, `-`, `(`, `)`, and spaces.
- The expression is valid.
- All intermediate results fit in a 32-bit signed integer.

## Examples
```text
Input: s = "(1+(4+5+2)-3)+(6+8)"
Output: 23
Explanation: The inner group `4+5+2` is `11`, so `(1+11-3) + (6+8) = 9 + 14 = 23`.
```

## Understanding & Intuition
Only addition and subtraction exist, so the current sign is enough outside parentheses. A stack stores the result and sign before each `(`. When `)` appears, the completed inner result is folded into the outer expression.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively evaluate the innermost parenthesized expression, then evaluate the flat expression.
```python
class Solution:
    def calculate(self, s: str) -> int:
        def flat(expr: str) -> int:
            total, i = 0, 0
            while i < len(expr):
                while i < len(expr) and expr[i] == " ":
                    i += 1
                sign = 1
                # Consecutive signs can appear after replacing "(...)" with a negative value.
                while i < len(expr) and expr[i] in "+-":
                    if expr[i] == "-":
                        sign *= -1
                    i += 1
                    while i < len(expr) and expr[i] == " ":
                        i += 1
                num = 0
                while i < len(expr) and expr[i].isdigit():
                    num = num * 10 + int(expr[i])
                    i += 1
                total += sign * num
            return total

        while ")" in s:
            close = s.index(")")
            open_idx = s.rfind("(", 0, close)
            value = flat(s[open_idx + 1:close])
            # Replace the innermost parenthesized value.
            s = s[:open_idx] + str(value) + s[close + 1:]
        return flat(s)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use a stack for previous result and sign at each opening parenthesis.
```python
class Solution:
    def calculate(self, s: str) -> int:
        result = 0
        sign = 1
        stack = []
        i = 0
        while i < len(s):
            ch = s[i]
            if ch.isdigit():
                num = 0
                while i < len(s) and s[i].isdigit():
                    num = num * 10 + int(s[i])
                    i += 1
                result += sign * num
                continue
            if ch == "+":
                sign = 1
            elif ch == "-":
                sign = -1
            elif ch == "(":
                stack.append(result)
                stack.append(sign)
                result, sign = 0, 1
            elif ch == ")":
                prev_sign = stack.pop()
                prev_result = stack.pop()
                result = prev_result + prev_sign * result
            i += 1
        return result
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Track a global sign stack so every number is added with the sign induced by all surrounding parentheses.
```python
class Solution:
    def calculate(self, s: str) -> int:
        total = 0
        sign = 1
        signs = [1]
        i = 0
        while i < len(s):
            ch = s[i]
            if ch.isdigit():
                num = 0
                while i < len(s) and s[i].isdigit():
                    num = num * 10 + int(s[i])
                    i += 1
                total += sign * num
                continue
            if ch == "+":
                sign = signs[-1]
            elif ch == "-":
                sign = -signs[-1]
            elif ch == "(":
                signs.append(sign)
            elif ch == ")":
                signs.pop()
            i += 1
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
- Skip spaces without changing state.
- Unary minus before a parenthesis is handled by storing the sign.
- Parse multi-digit numbers in one loop.

## Related
- Evaluate Reverse Polish Notation
- Basic Calculator II
