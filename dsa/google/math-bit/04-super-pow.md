# 04. Super Pow

- **Difficulty:** Medium
- **Pattern:** Math & Bit Manipulation
- **Asked at:** Google

## Problem
Given an integer `a` and a non-empty list of decimal digits `b`, return `a^B mod 1337`, where `B` is the integer represented by `b`. Constraints: `1 <= a <= 2^31 - 1`, `1 <= len(b) <= 2000`, and each digit is between `0` and `9` with no leading zero unless `B` is zero.

## Examples
```text
Input: a = 2, b = [1, 0]
Output: 1024
Explanation: The exponent is 10, and 2^10 mod 1337 is 1024.
```

## Understanding & Intuition
The exponent can be huge, so it should not be converted into an ordinary power calculation. Decimal digits can be folded because `a^(prefix*10 + digit) = (a^prefix)^10 * a^digit`. Modular exponentiation keeps every intermediate small.

## Approach 1 — Naive / Brute Force
**Idea:** Convert the digit list to an integer and use modular exponentiation directly.
```python
class Solution:
    def superPow(self, a: int, b: list[int]) -> int:
        mod = 1337
        exp = 0
        for digit in b:
            exp = exp * 10 + digit
        return pow(a, exp, mod)
```
- **Time:** O(len(b) + log B) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Convert the exponent but evaluate it with binary fast exponentiation.
```python
class Solution:
    def superPow(self, a: int, b: list[int]) -> int:
        mod = 1337
        exp = 0
        for digit in b:
            exp = exp * 10 + digit
        ans, base = 1, a % mod
        while exp:
            if exp & 1:
                ans = (ans * base) % mod
            base = (base * base) % mod
            exp >>= 1
        return ans
```
- **Time:** O(len(b) + log B) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Fold digits directly in base 10 using modular exponentiation of small exponents.
```python
class Solution:
    def superPow(self, a: int, b: list[int]) -> int:
        mod = 1337
        def mod_pow(x, e):
            res = 1
            x %= mod
            while e:
                if e & 1:
                    res = (res * x) % mod
                x = (x * x) % mod
                e >>= 1
            return res
        ans = 1
        for digit in b:
            ans = (mod_pow(ans, 10) * mod_pow(a, digit)) % mod
        return ans
```
- **Time:** O(len(b)) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(len(b) + log B) | O(1) |
| Better | O(len(b) + log B) | O(1) |
| Optimal | O(len(b)) | O(1) |

## Edge Cases & Pitfalls
- Never compute `a^B` directly.
- Reduce `a` modulo 1337 before repeated multiplication.
- A zero digit still contributes the previous exponent multiplied by 10.

## Related
- Pow(x, n)
- Modular Exponentiation
