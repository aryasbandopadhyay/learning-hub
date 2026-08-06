# 14. Roman to Integer

- **Difficulty:** Easy
- **Pattern:** Math & Geometry
- **Asked at:** Amazon, Microsoft, Google, Bloomberg

## Problem
Given a valid Roman numeral string `s`, convert it to an integer. Constraints: `1 <= s.length <= 15`, value is in `[1, 3999]`.

## Examples
```text
Input: s = "MCMXCIV"
Output: 1994
Explanation: M=1000, CM=900, XC=90, IV=4.
```

## Understanding & Intuition
Roman numerals usually add values from left to right. If a smaller value appears before a larger value, it is subtractive. This local comparison is enough.

## Approach 1 — Naive / Brute Force
**Idea:** Replace special subtractive pairs with additive placeholders, then sum characters.
```python
class Solution:
    def romanToInt(self, s: str) -> int:
        values = {'I': 1, 'V': 5, 'X': 10, 'L': 50, 'C': 100, 'D': 500, 'M': 1000,
                  'A': 4, 'B': 9, 'E': 40, 'F': 90, 'G': 400, 'H': 900}
        s = s.replace('IV', 'A').replace('IX', 'B').replace('XL', 'E')
        s = s.replace('XC', 'F').replace('CD', 'G').replace('CM', 'H')
        return sum(values[ch] for ch in s)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Scan left to right; subtract a value if the next value is larger.
```python
class Solution:
    def romanToInt(self, s: str) -> int:
        values = {'I': 1, 'V': 5, 'X': 10, 'L': 50, 'C': 100, 'D': 500, 'M': 1000}
        total = 0
        for i, ch in enumerate(s):
            current = values[ch]
            if i + 1 < len(s) and current < values[s[i + 1]]:
                total -= current
            else:
                total += current
        return total
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Scan right to left, subtracting values smaller than the largest value seen so far.
```python
class Solution:
    def romanToInt(self, s: str) -> int:
        values = {'I': 1, 'V': 5, 'X': 10, 'L': 50, 'C': 100, 'D': 500, 'M': 1000}
        total = 0
        max_seen = 0
        for ch in reversed(s):
            value = values[ch]
            if value < max_seen:
                total -= value
            else:
                total += value
                max_seen = value
        return total
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n) | O(n) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Subtractive pairs are only valid before the next larger denomination.
- Do not double-count characters in pairs.

## Related
- Integer to Roman
- Excel Sheet Column Number
