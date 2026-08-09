# 12. Excel Sheet Column Title

- **Difficulty:** Easy
- **Pattern:** Math & Geometry
- **Asked at:** Microsoft, Amazon, Google

## Problem
Convert a positive integer into its Excel column title.

Excel titles use letters `A` through `Z` in a 1-indexed base-26 system: `1 -> A`, `2 -> B`, ..., `26 -> Z`, `27 -> AA`, and so on. Return the exact title for the given column number.

**Input**
- `columnNumber`: a positive integer.

**Output**
- The Excel column title string for `columnNumber`.

## Constraints
- `1 <= columnNumber <= 2^31 - 1`

## Examples
```text
Input: columnNumber = 28
Output: "AB"
Explanation: `28` is one full block of `26` plus `2`, which maps to `A` followed by `B`, giving `"AB"`.
```

```text
Input: columnNumber = 1
Output: "A"
Explanation: Column `1` maps directly to the title `A`.
```

## Understanding & Intuition
Excel titles are like base 26, but digits run from 1 to 26 instead of 0 to 25. Subtract one before taking modulo so `A` corresponds to remainder `0`. Build characters from right to left.

## Approach 1 — Naive / Brute Force
**Idea:** Generate titles one by one until reaching the requested number.
```python
class Solution:
    def convertToTitle(self, columnNumber: int) -> str:
        title = ""
        for _ in range(columnNumber):
            # Increment an Excel-style title by one.
            i = len(title) - 1
            chars = list(title)
            while i >= 0 and chars[i] == 'Z':
                chars[i] = 'A'
                i -= 1
            if i < 0:
                chars.insert(0, 'A')
            else:
                chars[i] = chr(ord(chars[i]) + 1)
            title = ''.join(chars)
        return title
```
- **Time:** O(n log n) — **Space:** O(log n)

## Approach 2 — Better
**Idea:** Use repeated division and append remainders, then reverse at the end.
```python
class Solution:
    def convertToTitle(self, columnNumber: int) -> str:
        chars = []
        while columnNumber:
            columnNumber -= 1
            chars.append(chr(ord('A') + columnNumber % 26))
            columnNumber //= 26
        return ''.join(reversed(chars))
```
- **Time:** O(log n) — **Space:** O(log n)

## Approach 3 — Optimal
**Idea:** Same math, but fill the answer with a list; this is optimal for Python strings.
```python
class Solution:
    def convertToTitle(self, columnNumber: int) -> str:
        ans = []
        while columnNumber > 0:
            columnNumber -= 1
            ans.append(chr(65 + columnNumber % 26))
            columnNumber //= 26
        ans.reverse()
        return ''.join(ans)
```
- **Time:** O(log n) — **Space:** O(log n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n log n) | O(log n) |
| Better | O(log n) | O(log n) |
| Optimal | O(log n) | O(log n) |

## Edge Cases & Pitfalls
- Subtract one before modulo to handle multiples of 26.
- `26` is `Z`, not `AZ`.

## Related
- Excel Sheet Column Number
- Base Conversion
