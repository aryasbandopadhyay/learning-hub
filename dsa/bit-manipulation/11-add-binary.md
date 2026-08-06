# 11. Add Binary

- **Difficulty:** Easy
- **Pattern:** Bit Manipulation
- **Asked at:** Facebook, Amazon, Google, Microsoft

## Problem
Given two binary strings `a` and `b`, return their sum as a binary string. Constraints: `1 <= len(a), len(b) <= 10^4`, and each string contains only `'0'` or `'1'`.

## Examples
```text
Input: a = "1010", b = "1011"
Output: "10101"
Explanation: 10 + 11 = 21, whose binary representation is 10101.
```

## Understanding & Intuition
Binary addition is the same carry process as decimal addition, but each digit is base two. XOR gives the sum bit when combining two bits and carry. For very long strings, avoid converting the whole input to fixed-width integers.

## Approach 1 — Naive / Brute Force
**Idea:** Convert both strings to integers, add them, and convert back to binary.
```python
class Solution:
    def addBinary(self, a: str, b: str) -> str:
        # Simple, but relies on Python big integers.
        return bin(int(a, 2) + int(b, 2))[2:]
```
- **Time:** O(n + m) — **Space:** O(n + m)

## Approach 2 — Better
**Idea:** Simulate addition from right to left with numeric carry.
```python
class Solution:
    def addBinary(self, a: str, b: str) -> str:
        i = len(a) - 1
        j = len(b) - 1
        carry = 0
        out = []
        while i >= 0 or j >= 0 or carry:
            total = carry
            if i >= 0:
                total += ord(a[i]) - ord("0")
                i -= 1
            if j >= 0:
                total += ord(b[j]) - ord("0")
                j -= 1
            out.append(str(total & 1))
            carry = total >> 1
        return "".join(reversed(out))
```
- **Time:** O(n + m) — **Space:** O(n + m)

## Approach 3 — Optimal
**Idea:** Simulate addition using XOR for the output bit and majority logic for carry.
```python
class Solution:
    def addBinary(self, a: str, b: str) -> str:
        i = len(a) - 1
        j = len(b) - 1
        carry = 0
        out = []
        while i >= 0 or j >= 0 or carry:
            abit = ord(a[i]) - ord("0") if i >= 0 else 0
            bbit = ord(b[j]) - ord("0") if j >= 0 else 0
            out.append(str(abit ^ bbit ^ carry))
            carry = (abit & bbit) | (carry & (abit ^ bbit))
            i -= 1
            j -= 1
        return "".join(reversed(out))
```
- **Time:** O(n + m) — **Space:** O(n + m)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n + m) | O(n + m) |
| Better | O(n + m) | O(n + m) |
| Optimal | O(n + m) | O(n + m) |

## Edge Cases & Pitfalls
- Inputs may have different lengths.
- A final carry can add one more digit.
- Python integers are unbounded, but manual addition is safer for interview constraints with very long strings.

## Related
- Sum of Two Integers
- Reverse Bits
- Number of 1 Bits
