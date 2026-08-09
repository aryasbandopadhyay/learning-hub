# 06. Parse Ternary Expression

- **Difficulty:** Medium
- **Pattern:** stack-based parsing & evaluation
- **Asked at:** Google, Amazon, Meta

## Problem
Given a valid nested ternary expression made of digits, `T`, `F`, `?`, and `:`, evaluate it and return the resulting single-character string. Ternary operators are right-associative, so `T?1:2` returns `"1"` and `F?1:T?4:5` returns `"4"`.

**Input**
- `expression`: a `str`; the ternary expression.

**Output**
- A `str`. Return the resulting single-character string. returns `"1"` and `F?1:T?4:5` returns `"4"`.

## Constraints
- `1 <= len(expression) <= 10^4`.
- operands are single characters from `T`, `F`, or digits.

## Examples
```text
Input: expression = "T?2:3"
Output: "2"
Explanation: The condition is true, so the first branch is selected.
```

## Understanding & Intuition
Right associativity means the innermost decision is found nearest the right side. A stack can reduce a pattern `condition ? trueValue : falseValue` as soon as both branch values are known. Recursive parsing can also jump over the unused branch.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively parse from left to right, evaluating the current condition and consuming both branches.
```python
class Solution:
    def parseTernary(self, expression: str) -> str:
        def parse(i):
            val = expression[i]
            i += 1
            if i == len(expression) or expression[i] != '?':
                return val, i
            true_val, j = parse(i + 1)
            false_val, k = parse(j + 1)
            return (true_val if val == 'T' else false_val), k
        return parse(0)[0]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Scan from right to left; whenever the top of the stack starts with `'?'`, reduce one ternary expression.
```python
class Solution:
    def parseTernary(self, expression: str) -> str:
        stack = []
        for ch in reversed(expression):
            if stack and stack[-1] == '?':
                stack.pop()
                first = stack.pop()
                stack.pop()
                second = stack.pop()
                stack.append(first if ch == 'T' else second)
            else:
                stack.append(ch)
        return stack[-1]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Recursively follow only the chosen branch and skip over the other branch using a nesting counter.
```python
class Solution:
    def parseTernary(self, expression: str) -> str:
        def skip(i):
            depth = 0
            while i < len(expression):
                if expression[i] == '?':
                    depth += 1
                elif expression[i] == ':':
                    if depth == 0:
                        return i + 1
                    depth -= 1
                i += 1
            return i
        def eval_at(i):
            if i + 1 >= len(expression) or expression[i + 1] != '?':
                return expression[i]
            if expression[i] == 'T':
                return eval_at(i + 2)
            return eval_at(skip(i + 2))
        return eval_at(0)
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Ternaries are right-associative.
- Operands are single characters, not multi-digit numbers.
- The colon for a condition may be nested inside deeper ternaries.

## Related
- Basic Calculator
- Decode String
- Boolean Expression Evaluation
