# 15. Integer to Roman

- **Difficulty:** Medium
- **Pattern:** Math & Geometry
- **Asked at:** Amazon, Microsoft, Google, Bloomberg

## Problem
Given an integer `num`, convert it to its standard Roman numeral representation.

Use the usual symbols `I`, `V`, `X`, `L`, `C`, `D`, and `M`, including subtractive forms such as `IV`, `IX`, `XL`, `XC`, `CD`, and `CM`. The output should be the canonical shortest Roman numeral for the number.

**Input**
- `num`: an integer between `1` and `3999`.

**Output**
- The exact canonical Roman numeral string for `num`.

## Constraints
- `1 <= num <= 3999`

## Examples
```text
Input: num = 1994
Output: "MCMXCIV"
Explanation: Break `1994` into `1000 + 900 + 90 + 4`, which map to `M`, `CM`, `XC`, and `IV`, producing `"MCMXCIV"`.
```

```text
Input: num = 3
Output: "III"
Explanation: The number `3` is represented by three `I` symbols.
```

## Understanding & Intuition
Roman numerals are formed greedily from largest symbol values to smallest, including subtractive values like `CM` and `IV`. Since valid inputs are bounded to 3999, lookup tables are also practical. The greedy table is compact and reliable.

## Approach 1 — Naive / Brute Force
**Idea:** Append one-symbol denominations repeatedly, then patch invalid repeated groups.
```python
class Solution:
    def intToRoman(self, num: int) -> str:
        roman = "M" * (num // 1000)
        num %= 1000
        roman += "D" * (num // 500)
        num %= 500
        roman += "C" * (num // 100)
        num %= 100
        roman += "L" * (num // 50)
        num %= 50
        roman += "X" * (num // 10)
        num %= 10
        roman += "V" * (num // 5)
        roman += "I" * (num % 5)
        replacements = [('DCCCC', 'CM'), ('CCCC', 'CD'), ('LXXXX', 'XC'),
                        ('XXXX', 'XL'), ('VIIII', 'IX'), ('IIII', 'IV')]
        for old, new in replacements:
            roman = roman.replace(old, new)
        return roman
```
- **Time:** O(1) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use lookup tables for thousands, hundreds, tens, and ones.
```python
class Solution:
    def intToRoman(self, num: int) -> str:
        thousands = ["", "M", "MM", "MMM"]
        hundreds = ["", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"]
        tens = ["", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"]
        ones = ["", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"]
        return thousands[num // 1000] + hundreds[(num % 1000) // 100] + tens[(num % 100) // 10] + ones[num % 10]
```
- **Time:** O(1) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Greedily consume the largest possible Roman token, including subtractive tokens.
```python
class Solution:
    def intToRoman(self, num: int) -> str:
        values = [
            (1000, "M"), (900, "CM"), (500, "D"), (400, "CD"),
            (100, "C"), (90, "XC"), (50, "L"), (40, "XL"),
            (10, "X"), (9, "IX"), (5, "V"), (4, "IV"), (1, "I"),
        ]
        ans = []
        for value, symbol in values:
            count, num = divmod(num, value)
            ans.append(symbol * count)
        return ''.join(ans)
```
- **Time:** O(1) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(1) | O(1) |
| Better | O(1) | O(1) |
| Optimal | O(1) | O(1) |

## Edge Cases & Pitfalls
- Include subtractive forms for 4, 9, 40, 90, 400, and 900.
- Roman numerals here are limited to `3999`.

## Related
- Roman to Integer
- Integer to English Words
