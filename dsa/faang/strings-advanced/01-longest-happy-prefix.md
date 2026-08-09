# 01. Longest Happy Prefix

- **Difficulty:** Hard
- **Pattern:** advanced strings
- **Asked at:** Google, Amazon, Meta

## Problem
Given a string `s`, return the longest non-empty prefix that is also a suffix, excluding the entire string. If none exists, return `""`.

**Input**
- `s`: a `str`; the input string.

**Output**
- A `str`. Return the longest non-empty prefix that is also a suffix, excluding the entire string. return `""`.

## Constraints
- `1 <= len(s) <= 100000`.
- `s` contains lowercase English letters.

## Examples
```text
Input: s = "level"
Output: "l"
Explanation: "l" is both a prefix and a suffix.
```

## Understanding & Intuition
A happy prefix is a border of the string. Brute force repeatedly compares overlapping characters. KMP's prefix table gives the longest proper border ending at the final character.

## Approach 1 — Naive / Brute Force
**Idea:** Test border lengths from longest to shortest.
```python
class Solution:
    def longestPrefix(self, s: str) -> str:
        for length in range(len(s) - 1, 0, -1):
            if s[:length] == s[-length:]:
                return s[:length]
        return ""
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Compare rolling prefix and suffix hashes, verifying any candidate.
```python
class Solution:
    def longestPrefix(self, s):
        mod, base = 1_000_000_007, 911382323
        pref = suff = 0
        power = 1
        best = 0
        n = len(s)
        for i in range(n - 1):
            pref = (pref * base + ord(s[i])) % mod
            suff = (suff + ord(s[n - 1 - i]) * power) % mod
            power = (power * base) % mod
            length = i + 1
            if pref == suff and s[:length] == s[n - length:]:
                best = length
        return s[:best]
```
- **Time:** O(n^2) worst, O(n) typical — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Build KMP's prefix-function and return the final border length.
```python
class Solution:
    def longestPrefix(self, s):
        pi = [0] * len(s)
        j = 0
        for i in range(1, len(s)):
            while j and s[i] != s[j]:
                j = pi[j - 1]
            if s[i] == s[j]:
                j += 1
                pi[i] = j
        return s[:pi[-1]] if s else ""
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n^2) worst, O(n) typical | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Do not return the whole string.
- Return `""` when there is no border.
- Verify hash candidates to avoid collision errors.

## Related
- KMP prefix function
- Shortest Palindrome
