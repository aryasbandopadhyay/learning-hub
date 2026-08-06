# 03. Nth Digit

- **Difficulty:** Medium
- **Pattern:** Math & Bit Manipulation
- **Asked at:** Google

## Problem
Given an integer `n`, return the `n`th digit in the infinite sequence `1234567891011121314...` using 1-indexing. Constraints: `1 <= n <= 2^31 - 1`.

## Examples
```text
Input: n = 250
Output: 1
Explanation: The 250th digit lies in the two-digit block and belongs to number 170.
```

## Understanding & Intuition
Numbers with the same digit length form contiguous blocks. There are `9 * 10^(len-1)` numbers of a given length, contributing that count times the length digits. After skipping whole blocks, simple indexing identifies the target number and digit.

## Approach 1 — Naive / Brute Force
**Idea:** Walk digit-length groups one by one, then materialize only the final target number as a string.
```python
class Solution:
    def findNthDigit(self, n: int) -> int:
        length = 1
        start = 1
        while n > 9 * start * length:
            n -= 9 * start * length
            length += 1
            start *= 10
        number = start + (n - 1) // length
        return int(str(number)[(n - 1) % length])
```
- **Time:** O(log n) — **Space:** O(log n)

## Approach 2 — Better
**Idea:** Count through numbers without storing the whole prefix, stopping when the target digit is inside the current number.
```python
class Solution:
    def findNthDigit(self, n: int) -> int:
        x = 1
        while n > len(str(x)):
            n -= len(str(x))
            x += 1
        return int(str(x)[n - 1])
```
- **Time:** O(n log n) — **Space:** O(log n)

## Approach 3 — Optimal
**Idea:** Skip entire digit-length blocks, then compute the exact number and offset arithmetically.
```python
class Solution:
    def findNthDigit(self, n: int) -> int:
        length, count, start = 1, 9, 1
        while n > length * count:
            n -= length * count
            length += 1
            count *= 10
            start *= 10
        number = start + (n - 1) // length
        index = (n - 1) % length
        return int(str(number)[index])
```
- **Time:** O(log n) — **Space:** O(log n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(log n) | O(log n) |
| Better | O(n log n) | O(log n) |
| Optimal | O(log n) | O(log n) |

## Edge Cases & Pitfalls
- The sequence is 1-indexed.
- Use `(n - 1)` when converting to zero-based positions.
- Account for all 9 one-digit numbers before moving to two-digit numbers.

## Related
- Integer to Roman
- Excel Sheet Column Title
