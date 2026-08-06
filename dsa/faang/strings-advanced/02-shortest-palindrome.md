# 02. Shortest Palindrome

- **Difficulty:** Hard
- **Pattern:** advanced strings
- **Asked at:** Google, Meta, Amazon

## Problem
Given a string `s`, add characters only to the front to make a palindrome. Return the shortest palindrome obtainable.

Constraints: `0 <= len(s) <= 100000`; `s` contains lowercase English letters.

## Examples
```text
Input: s = "aacecaaa"
Output: "aaacecaaa"
Explanation: The longest palindromic prefix is "aacecaa", so the remaining suffix is mirrored in front.
```

## Understanding & Intuition
Only the suffix after the longest palindromic prefix must be added in reverse. The core task is finding that prefix. KMP can do this by matching `s` against `reversed(s)`.

## Approach 1 — Naive / Brute Force
**Idea:** Test prefixes from longest to shortest until one is a palindrome.
```python
class Solution:
    def shortestPalindrome(self, s: str) -> str:
        for end in range(len(s), -1, -1):
            prefix = s[:end]
            if prefix == prefix[::-1]:
                return s[end:][::-1] + s
        return s
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use forward and reverse rolling hashes to identify palindromic prefix candidates.
```python
class Solution:
    def shortestPalindrome(self, s):
        mod, base = 1_000_000_007, 911382323
        forward = reverse = 0
        power = 1
        best = 0
        for i, ch in enumerate(s):
            val = ord(ch)
            forward = (forward * base + val) % mod
            reverse = (reverse + val * power) % mod
            power = (power * base) % mod
            if forward == reverse and s[:i + 1] == s[:i + 1][::-1]:
                best = i + 1
        return s[best:][::-1] + s
```
- **Time:** O(n^2) worst, O(n) typical — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** The last KMP prefix value of `s + "#" + reversed(s)` is the longest palindromic prefix length.
```python
class Solution:
    def shortestPalindrome(self, s):
        rev = s[::-1]
        combined = s + "#" + rev
        pi = [0] * len(combined)
        j = 0
        for i in range(1, len(combined)):
            while j and combined[i] != combined[j]:
                j = pi[j - 1]
            if combined[i] == combined[j]:
                j += 1
                pi[i] = j
        keep = pi[-1] if combined else 0
        return rev[:len(s) - keep] + s
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n^2) worst, O(n) typical | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Empty and already-palindromic strings should return unchanged.
- Add characters only at the front.
- Use a separator not present in the input.

## Related
- KMP
- Longest Happy Prefix
