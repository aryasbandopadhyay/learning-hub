# 06. Pow(x, n)

- **Difficulty:** Medium
- **Pattern:** Math & Geometry
- **Asked at:** Meta, Google, Amazon, Microsoft

## Problem
Implement `pow(x, n)`, returning `x` raised to integer power `n`. Constraints: `-100 < x < 100`, `-2^31 <= n <= 2^31 - 1`, result fits in a double.

## Examples
```text
Input: x = 2.0, n = 10
Output: 1024.0
Explanation: 2 multiplied by itself 10 times is 1024.
```

## Understanding & Intuition
Negative powers are reciprocals of positive powers. Multiplying one by one is too slow for large `n`. Exponentiation by squaring halves the exponent each step.

## Approach 1 — Naive / Brute Force
**Idea:** Multiply `abs(n)` times and invert if `n` is negative.
```python
class Solution:
    def myPow(self, x: float, n: int) -> float:
        result = 1.0
        for _ in range(abs(n)):
            result *= x
        return result if n >= 0 else 1.0 / result
```
- **Time:** O(|n|) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Recursive fast power: square the half result and multiply once for odd exponents.
```python
class Solution:
    def myPow(self, x: float, n: int) -> float:
        def fast_power(exp: int) -> float:
            if exp == 0:
                return 1.0
            half = fast_power(exp // 2)
            # Odd exponents need one extra factor of x.
            return half * half if exp % 2 == 0 else half * half * x

        ans = fast_power(abs(n))
        return ans if n >= 0 else 1.0 / ans
```
- **Time:** O(log |n|) — **Space:** O(log |n|)

## Approach 3 — Optimal
**Idea:** Iteratively square the base and consume binary bits of the exponent.
```python
class Solution:
    def myPow(self, x: float, n: int) -> float:
        exp = abs(n)
        base = x
        result = 1.0
        while exp:
            if exp & 1:
                result *= base
            base *= base
            exp >>= 1
        return result if n >= 0 else 1.0 / result
```
- **Time:** O(log |n|) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(|n|) | O(1) |
| Better | O(log |n|) | O(log |n|) |
| Optimal | O(log |n|) | O(1) |

## Edge Cases & Pitfalls
- `n = 0` returns `1.0`.
- Convert negative exponents before looping to avoid reciprocal mistakes.

## Related
- Sqrt(x)
- Super Pow
