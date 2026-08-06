# 15. Multiply Strings

- **Difficulty:** Medium
- **Pattern:** Simulation
- **Asked at:** Meta, Amazon, Google, Microsoft

## Problem
Given two non-negative integers `num1` and `num2` represented as decimal strings, return their product as a string. The inputs contain only digits, have no leading zeros except the string `"0"`, and may be too large for fixed-width integer types.

## Examples
```text
Input: num1 = "123", num2 = "456"
Output: "56088"
Explanation: 123 multiplied by 456 equals 56088.
```

## Understanding & Intuition
String multiplication mirrors the grade-school algorithm. Each digit pair contributes to a position determined by its indices. The main pitfalls are carrying digits correctly and removing leading zeros.

## Approach 1 — Naive / Brute Force
**Idea:** Parse the strings digit by digit into Python integers, multiply them, and convert the result back manually.
```python
class Solution:
    def multiply(self, num1: str, num2: str) -> str:
        a = 0
        for ch in num1:
            a = a * 10 + (ord(ch) - ord('0'))
        b = 0
        for ch in num2:
            b = b * 10 + (ord(ch) - ord('0'))
        product = a * b
        if product == 0:
            return "0"
        digits = []
        while product:
            digits.append(chr(ord('0') + product % 10))
            product //= 10
        return "".join(reversed(digits))
```
- **Time:** O(n + m) arithmetic words — **Space:** O(n + m)

## Approach 2 — Better
**Idea:** Multiply each digit of `num2` by all digits of `num1`, add shifted partial products, and manage carries.
```python
class Solution:
    def multiply(self, num1: str, num2: str) -> str:
        if num1 == "0" or num2 == "0":
            return "0"

        def add(a, b):
            i, j, carry = len(a) - 1, len(b) - 1, 0
            out = []
            while i >= 0 or j >= 0 or carry:
                total = carry
                if i >= 0:
                    total += ord(a[i]) - ord('0')
                    i -= 1
                if j >= 0:
                    total += ord(b[j]) - ord('0')
                    j -= 1
                out.append(str(total % 10))
                carry = total // 10
            return "".join(reversed(out))

        result = "0"
        zeros = ""
        for d2 in reversed(num2):
            carry = 0
            partial = []
            for d1 in reversed(num1):
                total = (ord(d1) - ord('0')) * (ord(d2) - ord('0')) + carry
                partial.append(str(total % 10))
                carry = total // 10
            if carry:
                partial.append(str(carry))
            result = add(result, "".join(reversed(partial)) + zeros)
            zeros += "0"
        return result
```
- **Time:** O(nm + m(n + m)) — **Space:** O(n + m)

## Approach 3 — Optimal
**Idea:** Accumulate all digit products directly in an array of length `n + m`, then propagate carries once.
```python
class Solution:
    def multiply(self, num1: str, num2: str) -> str:
        if num1 == "0" or num2 == "0":
            return "0"
        n, m = len(num1), len(num2)
        pos = [0] * (n + m)
        for i in range(n - 1, -1, -1):
            for j in range(m - 1, -1, -1):
                mul = (ord(num1[i]) - ord('0')) * (ord(num2[j]) - ord('0'))
                total = mul + pos[i + j + 1]
                pos[i + j + 1] = total % 10
                pos[i + j] += total // 10
        start = 0
        while start < len(pos) - 1 and pos[start] == 0:
            start += 1
        return "".join(str(d) for d in pos[start:])
```
- **Time:** O(nm) — **Space:** O(n + m)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n + m) arithmetic words | O(n + m) |
| Better | O(nm + m(n + m)) | O(n + m) |
| Optimal | O(nm) | O(n + m) |

## Edge Cases & Pitfalls
- If either input is `"0"`, return `"0"` immediately.
- Do not keep leading zeros in the final answer.
- Carry propagation must affect the position to the left.

## Related
- Add Strings
- Plus One
