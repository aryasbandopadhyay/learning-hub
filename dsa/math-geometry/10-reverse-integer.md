# 10. Reverse Integer

- **Difficulty:** Medium
- **Pattern:** Math & Geometry
- **Asked at:** Bloomberg, Amazon, Apple, Google

## Problem
Given a signed 32-bit integer `x`, return its digits reversed. If reversing causes overflow outside `[-2^31, 2^31 - 1]`, return `0`.

## Examples
```text
Input: x = -123
Output: -321
Explanation: Reverse the digits and preserve the sign.
```

## Understanding & Intuition
The sign can be handled separately. Reversal repeatedly pops the last digit and appends it to the answer. Overflow must be checked before returning.

## Approach 1 — Naive / Brute Force
**Idea:** Reverse the string representation and validate the 32-bit range.
```python
class Solution:
    def reverse(self, x: int) -> int:
        sign = -1 if x < 0 else 1
        rev = int(str(abs(x))[::-1]) * sign
        return rev if -2**31 <= rev <= 2**31 - 1 else 0
```
- **Time:** O(d) — **Space:** O(d)

## Approach 2 — Better
**Idea:** Build the reversed number with arithmetic, then range-check once.
```python
class Solution:
    def reverse(self, x: int) -> int:
        sign = -1 if x < 0 else 1
        x = abs(x)
        rev = 0
        while x:
            rev = rev * 10 + x % 10
            x //= 10
        rev *= sign
        return rev if -2**31 <= rev <= 2**31 - 1 else 0
```
- **Time:** O(d) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Check overflow before each append, matching fixed-width integer constraints.
```python
class Solution:
    def reverse(self, x: int) -> int:
        limit = 2**31 - 1
        sign = -1 if x < 0 else 1
        x = abs(x)
        rev = 0
        while x:
            digit = x % 10
            if rev > (limit - digit) // 10:
                return 0
            rev = rev * 10 + digit
            x //= 10
        return sign * rev
```
- **Time:** O(d) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(d) | O(d) |
| Better | O(d) | O(1) |
| Optimal | O(d) | O(1) |

## Edge Cases & Pitfalls
- Reversed overflow returns `0`.
- Leading zeros in the reversed value naturally disappear.

## Related
- Palindrome Number
- String to Integer (atoi)
