# 14. Integer to English Words

- **Difficulty:** Hard
- **Pattern:** String Construction
- **Asked at:** Meta, Amazon, Microsoft, Google

## Problem
Convert a non-negative integer `num` to its English words representation. The input is in the range `0 <= num <= 2^31 - 1`, and the output must not contain extra spaces.

**Input**
- `num`: a `int`; the non-negative integer.

**Output**
- A `str`. Return the value produced by `numberToWords`.

## Constraints
- `0 <= num <= 2^31 - 1`.

## Examples
```text
Input: num = 12345
Output: "Twelve Thousand Three Hundred Forty Five"
Explanation: Split the number into thousands groups and spell each group.
```

## Understanding & Intuition
English number words repeat every three digits, then add a scale such as Thousand or Million. Handling zero separately avoids returning an empty string. The key is converting each three-digit chunk consistently and joining only non-empty chunks.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively peel off the largest named unit and concatenate the word for the quotient and remainder.
```python
class Solution:
    def numberToWords(self, num: int) -> str:
        below_20 = ["", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"]
        tens = ["", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"]
        units = [(1000000000, "Billion"), (1000000, "Million"), (1000, "Thousand"), (100, "Hundred")]

        def words(n):
            if n == 0:
                return []
            if n < 20:
                return [below_20[n]]
            if n < 100:
                return [tens[n // 10]] + words(n % 10)
            for value, name in units:
                if n >= value:
                    return words(n // value) + [name] + words(n % value)
            return []

        if num == 0:
            return "Zero"
        return " ".join(words(num))
```
- **Time:** O(1) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Convert each three-digit group using conditionals, then scan groups from billions down to ones.
```python
class Solution:
    def numberToWords(self, num: int) -> str:
        ones = ["", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"]
        tens = ["", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"]
        scales = [(1000000000, "Billion"), (1000000, "Million"), (1000, "Thousand"), (1, "")]

        def three(n):
            parts = []
            if n >= 100:
                parts.append(ones[n // 100])
                parts.append("Hundred")
                n %= 100
            if n >= 20:
                parts.append(tens[n // 10])
                n %= 10
            if n > 0:
                parts.append(ones[n])
            return parts

        if num == 0:
            return "Zero"
        ans = []
        for value, scale in scales:
            group = num // value
            if group:
                ans.extend(three(group))
                if scale:
                    ans.append(scale)
                num %= value
        return " ".join(ans)
```
- **Time:** O(1) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Process base-1000 chunks from right to left and prepend each non-zero chunk with its scale.
```python
class Solution:
    def numberToWords(self, num: int) -> str:
        if num == 0:
            return "Zero"
        small = ["", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"]
        tens = ["", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"]
        scales = ["", "Thousand", "Million", "Billion"]

        def chunk_to_words(n):
            res = []
            if n >= 100:
                res += [small[n // 100], "Hundred"]
                n %= 100
            if n >= 20:
                res.append(tens[n // 10])
                n %= 10
            if n:
                res.append(small[n])
            return res

        parts = []
        scale = 0
        while num:
            chunk = num % 1000
            if chunk:
                words = chunk_to_words(chunk)
                if scales[scale]:
                    words.append(scales[scale])
                parts = words + parts
            num //= 1000
            scale += 1
        return " ".join(parts)
```
- **Time:** O(1) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(1) | O(1) |
| Better | O(1) | O(1) |
| Optimal | O(1) | O(1) |

## Edge Cases & Pitfalls
- Return exactly `"Zero"` for input `0`.
- Do not leave double spaces between skipped zero chunks.
- Remember the scale names in the correct order.

## Related
- Integer to Roman
- String Construction
