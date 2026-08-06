# 02. Number of 1 Bits

- **Difficulty:** Easy
- **Pattern:** Bit Manipulation
- **Asked at:** Google, Amazon, Microsoft, Meta

## Problem
Given a positive integer `n`, return the number of set bits in its binary representation. Treat `n` as an unsigned 32-bit integer. Constraints: `0 <= n <= 2^32 - 1`.

## Examples
```text
Input: n = 11
Output: 3
Explanation: Binary 1011 has three 1 bits.
```

## Understanding & Intuition
Checking every bit works because a 32-bit integer has a fixed width. Brian Kernighan's trick repeatedly clears the lowest set bit with `n & (n - 1)`. That loops exactly once per set bit, not once per bit position.

## Approach 1 — Naive / Brute Force
**Idea:** Convert to a 32-bit binary string and count `'1'` characters.
```python
class Solution:
    def hammingWeight(self, n: int) -> int:
        # Mask keeps the representation within unsigned 32-bit behavior.
        bits = bin(n & 0xFFFFFFFF)
        count = 0
        for ch in bits:
            if ch == "1":
                count += 1
        return count
```
- **Time:** O(32) — **Space:** O(32)

## Approach 2 — Better
**Idea:** Test each of the 32 bit positions with a moving mask.
```python
class Solution:
    def hammingWeight(self, n: int) -> int:
        n &= 0xFFFFFFFF
        count = 0
        for i in range(32):
            if n & (1 << i):
                count += 1
        return count
```
- **Time:** O(32) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Repeatedly clear the lowest set bit until the number becomes zero.
```python
class Solution:
    def hammingWeight(self, n: int) -> int:
        n &= 0xFFFFFFFF
        count = 0
        while n:
            # Clears the rightmost 1 bit.
            n &= n - 1
            count += 1
        return count
```
- **Time:** O(k), where `k` is the number of set bits — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(32) | O(32) |
| Better | O(32) | O(1) |
| Optimal | O(k) | O(1) |

## Edge Cases & Pitfalls
- `n = 0` should return `0`.
- Mask with `0xFFFFFFFF` when modeling unsigned 32-bit input.
- Do not confuse decimal digit count with binary set-bit count.

## Related
- Counting Bits
- Power of Two
- Reverse Bits
