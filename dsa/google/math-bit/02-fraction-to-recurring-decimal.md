# 02. Fraction to Recurring Decimal

- **Difficulty:** Medium
- **Pattern:** Math & Bit Manipulation
- **Asked at:** Google

## Problem
Given integers `numerator` and `denominator`, return their decimal representation as a string. If the fractional part repeats, enclose the repeating cycle in parentheses.

Implement `Solution.fractionToDecimal` with the parameters below and return the requested value.

**Input**
- `numerator`: a `int`; the fraction numerator.
- `denominator`: a `int`; the non-zero fraction denominator.

**Output**
- A `str` value representing the result described above.

## Constraints
- `-2^31 <= numerator <= 2^31 - 1`, `-2^31 <= denominator <= 2^31 - 1`, and `denominator != 0`

## Examples
```text
Input: numerator = 4, denominator = 333
Output: "0.(012)"
Explanation: Long division repeats the remainder after producing digits 012.
```

## Understanding & Intuition
Decimal long division is determined entirely by the current remainder. If the same remainder appears again, the digits between the two visits form the repeating cycle. The sign and integer part can be emitted before simulating fractional digits.

## Approach 1 — Naive / Brute Force
**Idea:** Generate decimal digits and keep a list of seen remainders, linearly searching for repeats.
```python
class Solution:
    def fractionToDecimal(self, numerator: int, denominator: int) -> str:
        if numerator == 0:
            return "0"
        sign = "-" if (numerator < 0) != (denominator < 0) else ""
        n, d = abs(numerator), abs(denominator)
        integer = str(n // d)
        rem = n - (n // d) * d
        if rem == 0:
            return sign + integer
        digits, seen = [], []
        while rem and rem not in seen:
            seen.append(rem)
            rem *= 10
            digits.append(str(rem // d))
            rem -= (rem // d) * d
        if rem == 0:
            return sign + integer + "." + "".join(digits)
        idx = seen.index(rem)
        return sign + integer + "." + "".join(digits[:idx]) + "(" + "".join(digits[idx:]) + ")"
```
- **Time:** O(k^2) — **Space:** O(k)

## Approach 2 — Better
**Idea:** Use a dictionary from remainder to output index so cycle detection is constant time.
```python
class Solution:
    def fractionToDecimal(self, numerator: int, denominator: int) -> str:
        if numerator == 0:
            return "0"
        sign = "-" if (numerator < 0) != (denominator < 0) else ""
        n, d = abs(numerator), abs(denominator)
        parts = [sign + str(n // d)]
        rem = n % d
        if rem == 0:
            return parts[0]
        parts.append(".")
        seen = {}
        while rem:
            if rem in seen:
                i = seen[rem]
                parts.insert(i, "(")
                parts.append(")")
                break
            seen[rem] = len(parts)
            rem *= 10
            parts.append(str(rem // d))
            rem %= d
        return "".join(parts)
```
- **Time:** O(k) — **Space:** O(k)

## Approach 3 — Optimal
**Idea:** Store fractional digits separately and record each remainder's fractional digit position, avoiding list insertion.
```python
class Solution:
    def fractionToDecimal(self, numerator: int, denominator: int) -> str:
        if numerator == 0:
            return "0"
        neg = (numerator < 0) != (denominator < 0)
        n, d = abs(numerator), abs(denominator)
        prefix = ("-" if neg else "") + str(n // d)
        rem = n % d
        if rem == 0:
            return prefix
        digits, pos = [], {}
        while rem and rem not in pos:
            pos[rem] = len(digits)
            rem *= 10
            digits.append(str(rem // d))
            rem %= d
        if rem == 0:
            return prefix + "." + "".join(digits)
        i = pos[rem]
        return prefix + "." + "".join(digits[:i]) + "(" + "".join(digits[i:]) + ")"
```
- **Time:** O(k) — **Space:** O(k)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(k^2) | O(k) |
| Better | O(k) | O(k) |
| Optimal | O(k) | O(k) |

## Edge Cases & Pitfalls
- Zero numerator returns exactly `"0"`.
- The negative sign applies once before the integer part.
- Parentheses start where a remainder first repeats, not where a digit first repeats.

## Related
- Divide Two Integers
- Multiply Strings
