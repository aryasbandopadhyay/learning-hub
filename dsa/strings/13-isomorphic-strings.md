# 13. Isomorphic Strings

- **Difficulty:** Easy
- **Pattern:** Strings
- **Asked at:** Google, Amazon, LinkedIn, Microsoft

## Problem
Given two strings `s` and `t`, return `True` if characters in `s` can be replaced to get `t`. Each occurrence of a character must map to the same character, and no two characters may map to the same character. Constraints: `1 <= len(s), len(t) <= 5 * 10^4`; strings contain ASCII characters.

## Examples
```text
Input: s = "egg", t = "add"
Output: true
Explanation: 'e' maps to 'a' and 'g' maps to 'd'.
```

## Understanding & Intuition
The mapping must be consistent and one-to-one. Checking only one direction misses cases where two source characters map to the same target. We can enforce the bijection directly or compare first-seen patterns.

## Approach 1 — Naive / Brute Force
**Idea:** For every pair of positions with equal source characters, ensure target characters are equal, and vice versa.
```python
class Solution:
    def isIsomorphic(self, s: str, t: str) -> bool:
        if len(s) != len(t):
            return False
        n = len(s)
        for i in range(n):
            for j in range(i + 1, n):
                if (s[i] == s[j]) != (t[i] == t[j]):
                    return False
        return True
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Maintain forward and reverse dictionaries to enforce a one-to-one mapping.
```python
class Solution:
    def isIsomorphic(self, s: str, t: str) -> bool:
        s_to_t = {}
        t_to_s = {}
        for a, b in zip(s, t):
            if a in s_to_t and s_to_t[a] != b:
                return False
            if b in t_to_s and t_to_s[b] != a:
                return False
            s_to_t[a] = b
            t_to_s[b] = a
        return True
```
- **Time:** O(n) — **Space:** O(k)

## Approach 3 — Optimal
**Idea:** Encode each string by the first index at which every character appeared and compare encodings.
```python
class Solution:
    def isIsomorphic(self, s: str, t: str) -> bool:
        def pattern(word: str) -> list[int]:
            first_seen = {}
            encoded = []
            for i, ch in enumerate(word):
                if ch not in first_seen:
                    first_seen[ch] = i
                encoded.append(first_seen[ch])
            return encoded

        return pattern(s) == pattern(t)
```
- **Time:** O(n) — **Space:** O(k)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(k) |
| Optimal | O(n) | O(k) |

## Edge Cases & Pitfalls
- Two different source characters cannot map to the same target.
- Same source character must always map to the same target.
- Equal lengths are required.

## Related
- Word Pattern
- Encode and Decode Strings
- Hash Maps
