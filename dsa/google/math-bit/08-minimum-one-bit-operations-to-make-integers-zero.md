# 08. Minimum One Bit Operations to Make Integers Zero

- **Difficulty:** Hard
- **Pattern:** Math & Bit Manipulation
- **Asked at:** Google

## Problem
Given a non-negative integer `n`, return the minimum number of one-bit operations needed to transform it to `0`. In one operation, you may flip the lowest bit, or flip bit `i` if bit `i-1` is `1` and all lower bits are `0`. Constraints: `0 <= n <= 10^9`.

## Examples
```text
Input: n = 9
Output: 14
Explanation: The optimal count follows the inverse Gray-code recurrence for binary 1001.
```

## Understanding & Intuition
The allowed operation graph is exactly the reflected Gray code order. Therefore the answer is the position of `n` in Gray-code order, which is the inverse Gray-code transform. This can be computed recursively or by accumulating prefix XORs from high to low bits.

## Approach 1 — Naive / Brute Force
**Idea:** Process bits from high to low, toggling the parity used by the inverse Gray-code transform.
```python
class Solution:
    def minimumOneBitOperations(self, n: int) -> int:
        ans = 0
        parity = 0
        for bit in range(n.bit_length() - 1, -1, -1):
            parity ^= (n >> bit) & 1
            ans = (ans << 1) | parity
        return ans
```
- **Time:** O(log n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use the recurrence `f(n) = (2^(k+1)-1) - f(n without highest bit)` for the highest set bit `k`.
```python
class Solution:
    def minimumOneBitOperations(self, n: int) -> int:
        if n == 0:
            return 0
        k = n.bit_length() - 1
        return ((1 << (k + 1)) - 1) - self.minimumOneBitOperations(n ^ (1 << k))
```
- **Time:** O(log n) — **Space:** O(log n)

## Approach 3 — Optimal
**Idea:** Invert Gray code iteratively by XORing all right-shifted prefixes of `n`.
```python
class Solution:
    def minimumOneBitOperations(self, n: int) -> int:
        ans = 0
        while n:
            ans ^= n
            n >>= 1
        return ans
```
- **Time:** O(log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(log n) | O(1) |
| Better | O(log n) | O(log n) |
| Optimal | O(log n) | O(1) |

## Edge Cases & Pitfalls
- `0` needs zero operations.
- The recurrence subtracts the subproblem from a full Gray-code block size.
- Do not confuse this with simply counting set bits.

## Related
- Gray Code
- Number of 1 Bits
