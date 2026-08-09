# 04. Parsing Boolean Expression

- **Difficulty:** Hard
- **Pattern:** String Manipulation & Parsing
- **Asked at:** Google

## Problem
Given a boolean expression with `t`, `f`, operators `!`, `&`, `|`, parentheses, and commas, return its value. `!` has one operand; `&` and `|` have one or more operands.

Implement `Solution.parseBoolExpr` with the parameters below and return the requested value.

**Input**
- `expression`: a `str`; the expression string described above.

**Output**
- A `bool` value representing the result described above.

## Constraints
- 1 <= expression.length <= 2 * 10^4
- `expression` is a valid boolean expression using `t`, `f`, `!`, `&`, `|`, parentheses, and commas

## Examples
```text
Input: expression = "|(&(t,f,t),!(t))"
Output: False
Explanation: Both top-level operands evaluate to false.
```

## Understanding & Intuition
The expression is fully parenthesized. Every closing parenthesis completes one operator call, so it can be reduced recursively or with a stack.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively parse an operator frame and evaluate its collected operands.
```python
class Solution:
    def parseBoolExpr(self, expression: str) -> bool:
        def parse(i):
            if expression[i] in "tf": return expression[i] == "t", i + 1
            op, i, vals = expression[i], i + 2, []
            while expression[i] != ")":
                if expression[i] == ",": i += 1
                else:
                    v, i = parse(i); vals.append(v)
            return ((not vals[0]) if op == "!" else all(vals) if op == "&" else any(vals)), i + 1
        return parse(0)[0]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Push literals and operators; reduce the top frame whenever `)` is seen.
```python
class Solution:
    def parseBoolExpr(self, expression: str) -> bool:
        st = []
        for c in expression:
            if c in ",(": continue
            if c != ")": st.append(c); continue
            vals = []
            while st[-1] in "tf": vals.append(st.pop() == "t")
            op = st.pop()
            st.append("t" if ((not vals[0]) if op == "!" else all(vals) if op == "&" else any(vals)) else "f")
        return st[-1] == "t"
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Keep compact frames of whether each operator has seen any true or false operand.
```python
class Solution:
    def parseBoolExpr(self, expression: str) -> bool:
        frames=[]; result=False
        def feed(v):
            nonlocal result
            if not frames:
                result=v
            else:
                frames[-1][1] = frames[-1][1] or v
                frames[-1][2] = frames[-1][2] or (not v)
        for c in expression:
            if c in '!&|':
                frames.append([c,False,False])
            elif c=='t':
                feed(True)
            elif c=='f':
                feed(False)
            elif c==')':
                op,has_t,has_f=frames.pop()
                feed((not has_t) if op=='!' else (not has_f) if op=='&' else has_t)
        return result
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Commas are separators only.
- `!` always has exactly one operand.
- Nested operators must finish before their parents.

## Related
- Basic Calculator
- Valid Parentheses
