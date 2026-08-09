# 01. Divide Two Integers

- **Difficulty:** Medium
- **Pattern:** Math & Bit Manipulation
- **Asked at:** Google

## Problem
Given two 32-bit signed integers `dividend` and `divisor`, return the quotient after dividing `dividend` by `divisor` truncated toward zero. Do not use multiplication, division, or modulo in the optimal approach. Clamp overflow to `[-2^31, 2^31 - 1]`.

Implement `Solution.divide` with the parameters below and return the requested value.

**Input**
- `dividend`: a `int`; the integer being divided.
- `divisor`: a `int`; the non-zero integer divisor.

**Output**
- A `int` value representing the result described above.

## Constraints
- `-2^31 <= dividend <= 2^31 - 1`, `-2^31 <= divisor <= 2^31 - 1`, and `divisor != 0`

## Examples
```text
Input: dividend = 43, divisor = -8
Output: -5
Explanation: 43 / -8 is -5.375, which truncates toward zero to -5.
```

## Understanding & Intuition
The sign can be handled independently from the magnitude. Division asks how many copies of the divisor fit into the dividend; bit shifts let us subtract large doubled chunks at once. The only overflow case is `-2^31 / -1`.

## Approach 1 — Naive / Brute Force
**Idea:** Use Python's positive integer quotient after separating signs, then apply the required truncation and clamp.
```python
class Solution:
    def divide(self, dividend: int, divisor: int) -> int:
        INT_MIN, INT_MAX = -2**31, 2**31 - 1
        if dividend == INT_MIN and divisor == -1:
            return INT_MAX
        negative = (dividend < 0) != (divisor < 0)
        a, b = abs(dividend), abs(divisor)
        q = a // b
        if negative:
            q = -q
        return max(INT_MIN, min(INT_MAX, q))
```
- **Time:** O(1) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Binary search the quotient magnitude and test candidates by repeated addition, avoiding direct division.
```python
class Solution:
    def divide(self, dividend: int, divisor: int) -> int:
        INT_MIN, INT_MAX = -2**31, 2**31 - 1
        if dividend == INT_MIN and divisor == -1:
            return INT_MAX
        negative = (dividend < 0) != (divisor < 0)
        a, b = abs(dividend), abs(divisor)
        lo, hi, ans = 0, a, 0
        while lo <= hi:
            mid = (lo + hi) >> 1
            prod = 0
            x, y = b, mid
            while y:
                if y & 1:
                    prod += x
                x += x
                y >>= 1
            if prod <= a:
                ans = mid
                lo = mid + 1
            else:
                hi = mid - 1
        ans = -ans if negative else ans
        return max(INT_MIN, min(INT_MAX, ans))
```
- **Time:** O(log^2 |dividend|) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** From the highest bit downward, subtract shifted divisors that still fit and set the corresponding quotient bit.
```python
class Solution:
    def divide(self, dividend: int, divisor: int) -> int:
        INT_MIN, INT_MAX = -2**31, 2**31 - 1
        if dividend == INT_MIN and divisor == -1:
            return INT_MAX
        negative = (dividend < 0) != (divisor < 0)
        a, b = abs(dividend), abs(divisor)
        q = 0
        for shift in range(31, -1, -1):
            if (b << shift) <= a:
                a -= b << shift
                q |= 1 << shift
        q = -q if negative else q
        return max(INT_MIN, min(INT_MAX, q))
```
- **Time:** O(log |dividend|) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(1) | O(1) |
| Better | O(log^2 n) | O(1) |
| Optimal | O(log n) | O(1) |

## Edge Cases & Pitfalls
- Clamp only the `-2^31 / -1` overflow case.
- Python `//` floors negative results, but this problem truncates toward zero.
- Work with absolute values so shifts are simple.

## Related
- Sum of Two Integers
- Bitwise AND of Numbers Range
