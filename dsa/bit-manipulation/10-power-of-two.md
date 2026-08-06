# 10. Power of Two

- **Difficulty:** Easy
- **Pattern:** Bit Manipulation
- **Asked at:** Google, Amazon, Microsoft, Apple

## Problem
Given an integer `n`, return `True` if it is a power of two; otherwise return `False`. An integer is a power of two if there exists an integer `x` such that `n == 2^x`. Constraints: `-2^31 <= n <= 2^31 - 1`.

## Examples
```text
Input: n = 16
Output: True
Explanation: 16 equals 2^4.
```

## Understanding & Intuition
Positive powers of two have exactly one set bit. Subtracting one flips that bit and all lower bits, so `n & (n - 1)` becomes zero only for powers of two. Non-positive numbers must be rejected first.

## Approach 1 — Naive / Brute Force
**Idea:** Repeatedly multiply by two until reaching or passing `n`.
```python
class Solution:
    def isPowerOfTwo(self, n: int) -> bool:
        if n <= 0:
            return False
        value = 1
        while value < n:
            value *= 2
        return value == n
```
- **Time:** O(log n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Count set bits and require exactly one.
```python
class Solution:
    def isPowerOfTwo(self, n: int) -> bool:
        if n <= 0:
            return False
        count = 0
        while n:
            count += n & 1
            n >>= 1
        return count == 1
```
- **Time:** O(log n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use the one-set-bit identity `n & (n - 1) == 0`.
```python
class Solution:
    def isPowerOfTwo(self, n: int) -> bool:
        return n > 0 and (n & (n - 1)) == 0
```
- **Time:** O(1) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(log n) | O(1) |
| Better | O(log n) | O(1) |
| Optimal | O(1) | O(1) |

## Edge Cases & Pitfalls
- `0` is not a power of two.
- Negative numbers are not powers of two in this problem.
- Parentheses help avoid precedence mistakes in `n & (n - 1)`.

## Related
- Number of 1 Bits
- Counting Bits
- Bitwise AND of Numbers Range
