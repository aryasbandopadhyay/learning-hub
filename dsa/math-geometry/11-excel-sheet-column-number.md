# 11. Excel Sheet Column Number

- **Difficulty:** Easy
- **Pattern:** Math & Geometry
- **Asked at:** Microsoft, Amazon, Google

## Problem
Excel column titles work like a 1-indexed base-26 number system: `A` represents `1`, `B` represents `2`, ..., `Z` represents `26`, `AA` represents `27`, and so on.

Given a column title, convert it to its corresponding positive integer.

**Input**
- `columnTitle`: a non-empty string of uppercase English letters.

**Output**
- The exact 1-indexed column number represented by `columnTitle`.

## Constraints
- `1 <= columnTitle.length <= 7`
- `columnTitle` contains only uppercase English letters `A` through `Z`.
- The resulting number is in the range `[1, 2^31 - 1]`.

## Examples
```text
Input: columnTitle = "AB"
Output: 28
Explanation: `A` contributes `1 * 26` in the first position and `B` contributes `2`, so `AB = 26 + 2 = 28`.
```

```text
Input: columnTitle = "A"
Output: 1
Explanation: The first Excel column is numbered `1`.
```

## Understanding & Intuition
Excel columns are base-26 numbers without a zero digit: `A=1` through `Z=26`. Scan left to right, multiplying the current value by 26 before adding the new digit.

## Approach 1 — Naive / Brute Force
**Idea:** Process from right to left using explicit powers of 26.
```python
class Solution:
    def titleToNumber(self, columnTitle: str) -> int:
        total = 0
        power = 1
        for ch in reversed(columnTitle):
            total += (ord(ch) - ord('A') + 1) * power
            power *= 26
        return total
```
- **Time:** O(n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Precompute values in a dictionary and use Horner's rule.
```python
class Solution:
    def titleToNumber(self, columnTitle: str) -> int:
        value = {chr(ord('A') + i): i + 1 for i in range(26)}
        total = 0
        for ch in columnTitle:
            total = total * 26 + value[ch]
        return total
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use Horner's rule directly with character arithmetic.
```python
class Solution:
    def titleToNumber(self, columnTitle: str) -> int:
        total = 0
        for ch in columnTitle:
            total = total * 26 + (ord(ch) - ord('A') + 1)
        return total
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(1) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- This is 1-indexed, not normal zero-indexed base 26.
- `Z` is `26`, and `AA` is `27`.

## Related
- Excel Sheet Column Title
- Base Conversion
