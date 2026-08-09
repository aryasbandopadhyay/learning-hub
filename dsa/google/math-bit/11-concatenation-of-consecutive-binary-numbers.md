# 11. Concatenation of Consecutive Binary Numbers

- **Difficulty:** Medium
- **Pattern:** Math & Bit Manipulation
- **Asked at:** Google

## Problem
Given an integer `n`, concatenate the binary representations of all integers from `1` through `n`, interpret the result as a binary number, and return it modulo `1_000_000_007`.

Implement `Solution.concatenatedBinary` with the parameters below and return the requested value.

**Input**
- `n`: a `int`; the size/count parameter described above.

**Output**
- A `int` value representing the result described above.

## Constraints
- `1 <= n <= 10^5`

## Examples
```text
Input: n = 12
Output: 505379714
Explanation: The concatenation is 1101110010111011110001001101010111100 in binary.
```

## Understanding & Intuition
Appending the binary representation of `x` means shifting the accumulated value left by `bit_length(x)` and adding `x`. The bit length increases exactly when `x` is a power of two. Taking the modulus after every append keeps the value bounded.

## Approach 1 — Naive / Brute Force
**Idea:** Build the entire binary string, convert it to an integer, and take the modulus.
```python
class Solution:
    def concatenatedBinary(self, n: int) -> int:
        s = ""
        for x in range(1, n + 1):
            s += bin(x)[2:]
        return int(s, 2) % 1000000007
```
- **Time:** O(n^2) — **Space:** O(n log n)

## Approach 2 — Better
**Idea:** Store binary pieces in a list, join once, then convert.
```python
class Solution:
    def concatenatedBinary(self, n: int) -> int:
        parts = []
        for x in range(1, n + 1):
            parts.append(bin(x)[2:])
        return int("".join(parts), 2) % 1000000007
```
- **Time:** O(n log n) — **Space:** O(n log n)

## Approach 3 — Optimal
**Idea:** Update the numeric answer incrementally with shifts and modular reduction.
```python
class Solution:
    def concatenatedBinary(self, n: int) -> int:
        mod = 1000000007
        ans = 0
        length = 0
        for x in range(1, n + 1):
            if x & (x - 1) == 0:
                length += 1
            ans = ((ans << length) | x) % mod
        return ans
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n log n) |
| Better | O(n log n) | O(n log n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Increase the bit length before appending a power of two.
- Apply the modulus at every step to avoid enormous integers.
- `| x` is equivalent to adding `x` after shifting because the low bits are zero.

## Related
- Add Binary
- Counting Bits
