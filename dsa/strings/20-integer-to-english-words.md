# 20. Integer to English Words

- **Difficulty:** Hard
- **Pattern:** Strings
- **Asked at:** Meta, Amazon, Microsoft, Google

## Problem
Convert a non-negative integer `num` to its English words representation. Constraints: `0 <= num <= 2^31 - 1`.

## Examples
```text
Input: num = 12345
Output: "Twelve Thousand Three Hundred Forty Five"
Explanation: Split into thousands groups: 12 thousand and 345.
```

## Understanding & Intuition
English names naturally group digits in chunks of three: hundreds, thousands, millions, and billions. Each chunk below 1000 can be converted independently. Then non-zero chunks are combined with their scale names.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively convert ranges using subtraction and division rules.
```python
class Solution:
    def numberToWords(self, num: int) -> str:
        if num == 0:
            return "Zero"
        below_20 = ["", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
                    "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
                    "Sixteen", "Seventeen", "Eighteen", "Nineteen"]
        tens = ["", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"]

        def say(n: int) -> str:
            if n < 20:
                return below_20[n]
            if n < 100:
                return (tens[n // 10] + " " + say(n % 10)).strip()
            if n < 1000:
                return (say(n // 100) + " Hundred " + say(n % 100)).strip()
            if n < 1_000_000:
                return (say(n // 1000) + " Thousand " + say(n % 1000)).strip()
            if n < 1_000_000_000:
                return (say(n // 1_000_000) + " Million " + say(n % 1_000_000)).strip()
            return (say(n // 1_000_000_000) + " Billion " + say(n % 1_000_000_000)).strip()

        return say(num)
```
- **Time:** O(1) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Convert each three-digit chunk with a helper, then attach scale words.
```python
class Solution:
    def numberToWords(self, num: int) -> str:
        if num == 0:
            return "Zero"
        below_20 = ["", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
                    "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
                    "Sixteen", "Seventeen", "Eighteen", "Nineteen"]
        tens = ["", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"]
        scales = ["", "Thousand", "Million", "Billion"]

        def chunk_to_words(n: int) -> list[str]:
            words = []
            if n >= 100:
                words += [below_20[n // 100], "Hundred"]
                n %= 100
            if n >= 20:
                words.append(tens[n // 10])
                n %= 10
            if n > 0:
                words.append(below_20[n])
            return words

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
        return ' '.join(parts)
```
- **Time:** O(1) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Process chunks from highest scale to lowest, appending only non-zero chunk phrases.
```python
class Solution:
    def numberToWords(self, num: int) -> str:
        if num == 0:
            return "Zero"
        below_20 = ["", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
                    "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
                    "Sixteen", "Seventeen", "Eighteen", "Nineteen"]
        tens = ["", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"]
        groups = [(1_000_000_000, "Billion"), (1_000_000, "Million"), (1000, "Thousand"), (1, "")]

        def under_thousand(n: int) -> list[str]:
            words = []
            if n >= 100:
                words.append(below_20[n // 100])
                words.append("Hundred")
                n %= 100
            if n >= 20:
                words.append(tens[n // 10])
                n %= 10
            if n:
                words.append(below_20[n])
            return words

        result = []
        for value, name in groups:
            chunk, num = divmod(num, value)
            if chunk:
                result.extend(under_thousand(chunk))
                if name:
                    result.append(name)
        return ' '.join(result)
```
- **Time:** O(1) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(1) | O(1) |
| Better | O(1) | O(1) |
| Optimal | O(1) | O(1) |

## Edge Cases & Pitfalls
- `0` must return `"Zero"`.
- Skip scale names for zero chunks.
- Do not include `"And"` or hyphens for LeetCode's expected format.

## Related
- Integer to Roman
- String Formatting
- Recursion
