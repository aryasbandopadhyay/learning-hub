# 06. Sum of Two Integers

- **Difficulty:** Medium
- **Pattern:** Bit Manipulation
- **Asked at:** Google, Amazon, Microsoft, Meta

## Problem
Given two integers `a` and `b`, return their sum without using the `+` or `-` operators in the core addition logic. Constraints: `-1000 <= a, b <= 1000`.

## Examples
```text
Input: a = 2, b = 3
Output: 5
Explanation: Bitwise XOR adds without carries, and AND/shift carries them.
```

## Understanding & Intuition
`a ^ b` gives the sum bits without carry. `(a & b) << 1` gives the carry bits that must be added next. Python integers are unbounded, so a fixed 32-bit mask is needed to emulate two's-complement overflow and termination for negatives.

## Approach 1 — Naive / Brute Force
**Idea:** Increment or decrement one value until the other is exhausted.
```python
class Solution:
    def getSum(self, a: int, b: int) -> int:
        # This deliberately avoids + and - in the repeated adjustment.
        while b > 0:
            a = a.__add__(1)
            b = b.__sub__(1)
        while b < 0:
            a = a.__sub__(1)
            b = b.__add__(1)
        return a
```
- **Time:** O(|b|) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Add bit by bit across a fixed 32-bit width with an explicit carry.
```python
class Solution:
    def getSum(self, a: int, b: int) -> int:
        mask = 0xFFFFFFFF
        a &= mask
        b &= mask
        result = 0
        carry = 0
        for i in range(32):
            abit = (a >> i) & 1
            bbit = (b >> i) & 1
            total_bit = abit ^ bbit ^ carry
            carry = (abit & bbit) | (carry & (abit ^ bbit))
            result |= total_bit << i
        # Convert unsigned 32-bit result back to signed Python int.
        return result if result <= 0x7FFFFFFF else ~(result ^ mask)
```
- **Time:** O(32) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Repeatedly combine carry-free sum and carry until no carry remains.
```python
class Solution:
    def getSum(self, a: int, b: int) -> int:
        mask = 0xFFFFFFFF
        max_int = 0x7FFFFFFF
        while b:
            # XOR is sum without carry; AND then shift is the carry.
            a, b = (a ^ b) & mask, ((a & b) << 1) & mask
        return a if a <= max_int else ~(a ^ mask)
```
- **Time:** O(32) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(|b|) | O(1) |
| Better | O(32) | O(1) |
| Optimal | O(32) | O(1) |

## Edge Cases & Pitfalls
- Negative numbers require masking in Python to model fixed-width two's complement.
- Do not use `+` or `-` in the bitwise addition approach.
- Convert results above `0x7FFFFFFF` back to signed integers.

## Related
- Add Binary
- Single Number
- Reverse Bits
