# 07. Multiply Strings

- **Difficulty:** Medium
- **Pattern:** Math & Geometry
- **Asked at:** Google, Meta, Amazon, Microsoft

## Problem
Given two non-negative integers `num1` and `num2` written as decimal strings, return their product as a decimal string.

The inputs can be too large to fit in standard integer types, so the multiplication should be treated as string/digit arithmetic. Do not include leading zeroes in the answer unless the product is exactly zero.

**Input**
- `num1`: a string of decimal digits representing a non-negative integer.
- `num2`: a string of decimal digits representing a non-negative integer.

**Output**
- The exact product `num1 * num2` as a decimal string.

## Constraints
- `1 <= num1.length, num2.length <= 200`
- `num1` and `num2` contain only digits `0` through `9`.
- Neither input has leading zeroes unless it is exactly `"0"`.

## Examples
```text
Input: num1 = "123", num2 = "456"
Output: "56088"
Explanation: Multiplying by place value gives `123 * 456 = 56088`, and the product is returned as the string `"56088"`.
```

```text
Input: num1 = "0", num2 = "123"
Output: "0"
Explanation: Any number multiplied by zero has product zero.
```

## Understanding & Intuition
Grade-school multiplication places each digit product according to its decimal position. Carries can be accumulated in a fixed array of length `m+n`. Leading zeros are removed at the end.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly add `num1` exactly `num2` times using string addition; valid but intentionally slow.
```python
class Solution:
    def multiply(self, num1: str, num2: str) -> str:
        def add(a: str, b: str) -> str:
            i, j, carry, out = len(a) - 1, len(b) - 1, 0, []
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
            return ''.join(reversed(out))

        def dec(s: str) -> str:
            digits = list(s)
            i = len(digits) - 1
            while digits[i] == '0':
                digits[i] = '9'
                i -= 1
            digits[i] = str(ord(digits[i]) - ord('0') - 1)
            return ''.join(digits).lstrip('0') or "0"

        if num1 == "0" or num2 == "0":
            return "0"
        result = "0"
        times = num2
        while times != "0":
            result = add(result, num1)
            times = dec(times)
        return result
```
- **Time:** O(value(num2) * (m+n)) — **Space:** O(m+n)

## Approach 2 — Better
**Idea:** Build shifted partial products for each digit of `num2`, then add strings.
```python
class Solution:
    def multiply(self, num1: str, num2: str) -> str:
        def add(a: str, b: str) -> str:
            i, j, carry, out = len(a) - 1, len(b) - 1, 0, []
            while i >= 0 or j >= 0 or carry:
                total = carry
                if i >= 0:
                    total += ord(a[i]) - 48
                    i -= 1
                if j >= 0:
                    total += ord(b[j]) - 48
                    j -= 1
                out.append(str(total % 10))
                carry = total // 10
            return ''.join(reversed(out))

        if num1 == "0" or num2 == "0":
            return "0"
        result = "0"
        zeros = 0
        for ch in reversed(num2):
            carry, part = 0, []
            digit = ord(ch) - 48
            for a in reversed(num1):
                product = (ord(a) - 48) * digit + carry
                part.append(str(product % 10))
                carry = product // 10
            if carry:
                part.append(str(carry))
            result = add(result, ''.join(reversed(part)) + "0" * zeros)
            zeros += 1
        return result
```
- **Time:** O(mn + n(m+n)) — **Space:** O(m+n)

## Approach 3 — Optimal
**Idea:** Accumulate every digit product directly into its final carry array.
```python
class Solution:
    def multiply(self, num1: str, num2: str) -> str:
        if num1 == "0" or num2 == "0":
            return "0"
        m, n = len(num1), len(num2)
        pos = [0] * (m + n)
        for i in range(m - 1, -1, -1):
            for j in range(n - 1, -1, -1):
                product = (ord(num1[i]) - 48) * (ord(num2[j]) - 48)
                p1, p2 = i + j, i + j + 1
                total = product + pos[p2]
                pos[p2] = total % 10
                pos[p1] += total // 10
        result = ''.join(map(str, pos)).lstrip('0')
        return result or "0"
```
- **Time:** O(mn) — **Space:** O(m+n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(value(num2) * (m+n)) | O(m+n) |
| Better | O(mn + n(m+n)) | O(m+n) |
| Optimal | O(mn) | O(m+n) |

## Edge Cases & Pitfalls
- Return exactly `"0"` if either input is zero.
- Do not use Python big integer conversion in the optimal solution.

## Related
- Add Strings
- Plus One
