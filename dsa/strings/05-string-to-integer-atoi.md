# 05. String to Integer (atoi)

- **Difficulty:** Medium
- **Pattern:** Strings
- **Asked at:** Amazon, Microsoft, Meta, Google

## Problem
Implement `myAtoi(s)`, which converts a string to a signed 32-bit integer. Ignore leading spaces, read an optional `+` or `-`, then read consecutive digits until a non-digit; if no digits are read return `0`. Clamp values below `-2^31` to `-2^31` and above `2^31 - 1` to `2^31 - 1`. Constraints: `0 <= len(s) <= 200`.

## Examples
```text
Input: s = "   -42"
Output: -42
Explanation: Leading spaces are skipped, '-' sets the sign, and digits form 42.
```

## Understanding & Intuition
The conversion is a small state machine: whitespace, optional sign, digits, then stop. Characters after the digit run are ignored. Overflow must be handled according to 32-bit signed bounds.

## Approach 1 — Naive / Brute Force
**Idea:** Trim left spaces, build the digit substring, convert it, then clamp.
```python
class Solution:
    def myAtoi(self, s: str) -> int:
        INT_MIN, INT_MAX = -2**31, 2**31 - 1
        s = s.lstrip()
        if not s:
            return 0
        sign = 1
        i = 0
        if s[0] in '+-':
            sign = -1 if s[0] == '-' else 1
            i = 1
        digits = []
        while i < len(s) and s[i].isdigit():
            digits.append(s[i])
            i += 1
        if not digits:
            return 0
        value = sign * int(''.join(digits))
        return max(INT_MIN, min(INT_MAX, value))
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Parse digits into an integer incrementally and clamp at the end.
```python
class Solution:
    def myAtoi(self, s: str) -> int:
        INT_MIN, INT_MAX = -2**31, 2**31 - 1
        i, n = 0, len(s)
        while i < n and s[i] == ' ':
            i += 1
        sign = 1
        if i < n and s[i] in '+-':
            sign = -1 if s[i] == '-' else 1
            i += 1
        value = 0
        while i < n and s[i].isdigit():
            value = value * 10 + (ord(s[i]) - ord('0'))
            i += 1
        value *= sign
        return max(INT_MIN, min(INT_MAX, value))
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Detect overflow before multiplying by 10 so the parser can return immediately.
```python
class Solution:
    def myAtoi(self, s: str) -> int:
        INT_MIN, INT_MAX = -2**31, 2**31 - 1
        i, n = 0, len(s)
        while i < n and s[i] == ' ':
            i += 1
        sign = 1
        if i < n and s[i] in '+-':
            sign = -1 if s[i] == '-' else 1
            i += 1
        value = 0
        limit = INT_MAX if sign == 1 else -INT_MIN
        while i < n and s[i].isdigit():
            digit = ord(s[i]) - ord('0')
            if value > (limit - digit) // 10:
                return INT_MAX if sign == 1 else INT_MIN
            value = value * 10 + digit
            i += 1
        return sign * value
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- `"+"`, `"-"`, and words before digits return `0`.
- Stop at the first non-digit after parsing starts.
- Clamp, do not wrap, on overflow.

## Related
- Valid Number
- Integer to Roman
- String Parsing
