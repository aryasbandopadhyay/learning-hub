# 17. Repeated Substring Pattern

- **Difficulty:** Easy
- **Pattern:** String Matching
- **Asked at:** Amazon, Google, Meta

## Problem
Given a non-empty string `s`, return `True` if it can be constructed by repeating one of its proper substrings one or more times. Otherwise, return `False`. The string length is at most `10^4`.

**Input**
- `s`: a `str`; the input string.

**Output**
- A `bool`. Return `True` if it can be constructed by repeating one of its proper substrings one or more times. return `False`.

## Constraints
- `1 <= len(s) <= 10^4`.
- `s` contains lowercase English letters.

## Examples
```text
Input: s = "abab"
Output: True
Explanation: The substring "ab" repeated twice forms "abab".
```

## Understanding & Intuition
A repeated pattern must have a length that divides the full string length. Checking all such lengths is straightforward but can repeat work. String matching exposes the same periodicity more directly.

## Approach 1 — Naive / Brute Force
**Idea:** Try every possible proper prefix length and rebuild the string when the length divides `len(s)`.
```python
class Solution:
    def repeatedSubstringPattern(self, s: str) -> bool:
        n = len(s)
        for length in range(1, n):
            if n % length == 0:
                if s[:length] * (n // length) == s:
                    return True
        return False
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Build the KMP longest-prefix-suffix table; a non-zero border can imply a repeated period.
```python
class Solution:
    def repeatedSubstringPattern(self, s: str) -> bool:
        n = len(s)
        lps = [0] * n
        j = 0
        for i in range(1, n):
            while j > 0 and s[i] != s[j]:
                j = lps[j - 1]
            if s[i] == s[j]:
                j += 1
                lps[i] = j
        border = lps[-1]
        period = n - border
        return border > 0 and n % period == 0
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** A repeated string appears inside `(s + s)` after removing the first and last characters.
```python
class Solution:
    def repeatedSubstringPattern(self, s: str) -> bool:
        return s in (s + s)[1:-1]
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- A one-character string can never be made by repeating a proper substring.
- The candidate substring length must divide the full length.
- Do not return `True` just because there is a repeated prefix.

## Related
- KMP Prefix Function
- Implement strStr
