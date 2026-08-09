# 01. Valid Number

- **Difficulty:** Hard
- **Pattern:** String Manipulation & Parsing
- **Asked at:** Google

## Problem
Given string `s`, return whether it is a valid decimal number. A number may contain one sign, one decimal point, and one exponent introduced by `e` or `E`; the exponent must be an integer. No spaces or special values are allowed.

Implement `Solution.validNumber` with the parameters below and return the requested value.

**Input**
- `s`: a `str`; the input string described above.

**Output**
- A `bool` value representing the result described above.

## Constraints
- 1 <= s.length <= 20
- `s` consists of English letters, digits, plus/minus signs, decimal points, and spaces

## Examples
```text
Input: s = "-123.45e+6"
Output: True
Explanation: The mantissa and exponent both satisfy the grammar.
```

## Understanding & Intuition
The grammar is small but strict. Splitting the mantissa and exponent or using a state machine avoids accepting partial forms like `.` or `1e`.

## Approach 1 — Naive / Brute Force
**Idea:** Let Python parse after rejecting characters and forbidden spellings.
```python
class Solution:
    def validNumber(self, s: str) -> bool:
        if not s or any(c not in "+-.eE0123456789" for c in s):
            return False
        try:
            float(s)
        except ValueError:
            return False
        t = s.lower()
        return "inf" not in t and "nan" not in t
```
- **Time:** O(n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Validate the decimal mantissa and optional integer exponent separately.
```python
class Solution:
    def validNumber(self, s: str) -> bool:
        def is_int(t: str) -> bool:
            if t and t[0] in "+-": t = t[1:]
            return bool(t) and all(c.isdigit() for c in t)
        def is_dec(t: str) -> bool:
            if t and t[0] in "+-": t = t[1:]
            if not t or t.count(".") > 1: return False
            if "." not in t: return all(c.isdigit() for c in t)
            a, b = t.split(".")
            return (a != "" or b != "") and all(c.isdigit() for c in a + b)
        p = s.replace("E", "e").split("e")
        return (len(p) == 1 and is_dec(p[0])) or (len(p) == 2 and is_dec(p[0]) and is_int(p[1]))
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Run a deterministic finite automaton and accept only completed numeric states.
```python
class Solution:
    def validNumber(self, s: str) -> bool:
        states = [{"sign":1,"digit":2,"dot":3},{"digit":2,"dot":3},{"digit":2,"dot":4,"exp":5},{"digit":4},{"digit":4,"exp":5},{"sign":6,"digit":7},{"digit":7},{"digit":7}]
        st = 0
        for c in s:
            tok = "digit" if c.isdigit() else "sign" if c in "+-" else "dot" if c == "." else "exp" if c in "eE" else "bad"
            if tok not in states[st]: return False
            st = states[st][tok]
        return st in {2,4,7}
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- `.` and `1e` are invalid.
- A sign is valid only at the beginning or just after the exponent.
- The exponent cannot be decimal.

## Related
- String to Integer Atoi
- Basic Calculator
