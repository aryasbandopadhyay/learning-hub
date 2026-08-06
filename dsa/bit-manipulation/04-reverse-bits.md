# 04. Reverse Bits

- **Difficulty:** Easy
- **Pattern:** Bit Manipulation
- **Asked at:** Apple, Amazon, Google, Microsoft

## Problem
Reverse the bits of a given unsigned 32-bit integer `n` and return the resulting unsigned integer. Constraints: `0 <= n <= 2^32 - 1`.

## Examples
```text
Input: n = 43261596
Output: 964176192
Explanation: 00000010100101000001111010011100 reverses to 00111001011110000010100101000000.
```

## Understanding & Intuition
The result is built from left to right while reading `n` from right to left. Because Python integers are unbounded, masking to 32 bits keeps behavior aligned with the problem. Shifts and `& 1` expose one bit at a time.

## Approach 1 — Naive / Brute Force
**Idea:** Format as a 32-character binary string, reverse it, and parse it back.
```python
class Solution:
    def reverseBits(self, n: int) -> int:
        bits = format(n & 0xFFFFFFFF, "032b")
        reversed_bits = bits[::-1]
        return int(reversed_bits, 2)
```
- **Time:** O(32) — **Space:** O(32)

## Approach 2 — Better
**Idea:** Read each bit from `n` and set its mirrored position in the answer.
```python
class Solution:
    def reverseBits(self, n: int) -> int:
        n &= 0xFFFFFFFF
        ans = 0
        for i in range(32):
            if n & (1 << i):
                ans |= 1 << (31 - i)
        return ans
```
- **Time:** O(32) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Shift the answer left and append the current lowest bit of `n`.
```python
class Solution:
    def reverseBits(self, n: int) -> int:
        n &= 0xFFFFFFFF
        ans = 0
        for _ in range(32):
            ans = (ans << 1) | (n & 1)
            n >>= 1
        return ans & 0xFFFFFFFF
```
- **Time:** O(32) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(32) | O(32) |
| Better | O(32) | O(1) |
| Optimal | O(32) | O(1) |

## Edge Cases & Pitfalls
- Preserve leading zeros by using exactly 32 iterations or a 32-character string.
- Mask with `0xFFFFFFFF` for unsigned 32-bit behavior.
- Reversing decimal digits is unrelated to reversing bits.

## Related
- Number of 1 Bits
- Counting Bits
- Power of Two
