# 07. Repeated Substring Pattern

- **Difficulty:** Easy
- **Pattern:** Strings
- **Asked at:** Google, Amazon, Meta, Microsoft

## Problem
Given a string `s`, return `True` if it can be constructed by taking a non-empty substring and repeating it at least twice. Constraints: `1 <= len(s) <= 10^4`; `s` contains lowercase English letters.

## Examples
```text
Input: s = "abab"
Output: true
Explanation: "ab" repeated twice forms "abab".
```

## Understanding & Intuition
If a repeated unit has length `k`, then `k` must divide the string length. We can test divisors directly. A string trick and KMP both use the periodic structure more efficiently.

## Approach 1 — Naive / Brute Force
**Idea:** Try every possible prefix length and repeat it enough times.
```python
class Solution:
    def repeatedSubstringPattern(self, s: str) -> bool:
        n = len(s)
        for length in range(1, n):
            if n % length == 0:
                part = s[:length]
                if part * (n // length) == s:
                    return True
        return False
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Check whether `s` appears inside `(s + s)` after removing the first and last character.
```python
class Solution:
    def repeatedSubstringPattern(self, s: str) -> bool:
        doubled_middle = (s + s)[1:-1]
        return s in doubled_middle
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use the KMP prefix table; a valid period is `n - lps[-1]` when it divides `n`.
```python
class Solution:
    def repeatedSubstringPattern(self, s: str) -> bool:
        n = len(s)
        lps = [0] * n
        length = 0
        for i in range(1, n):
            while length and s[i] != s[length]:
                length = lps[length - 1]
            if s[i] == s[length]:
                length += 1
                lps[i] = length
        longest_border = lps[-1]
        period = n - longest_border
        return longest_border > 0 and n % period == 0
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- A one-character string cannot be made from at least two repeats.
- Only prefix lengths that divide `n` can work.
- A border alone is not enough; the derived period must divide `n`.

## Related
- KMP
- Implement strStr()
- String Periodicity
