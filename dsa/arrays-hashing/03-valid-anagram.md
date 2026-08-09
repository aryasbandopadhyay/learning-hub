# 03. Valid Anagram

- **Difficulty:** Easy
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Bloomberg

## Problem
Given two strings `s` and `t`, decide whether `t` is an anagram of `s`: both strings must contain exactly the same characters with the same frequencies.

**Input**
- `s`: the first string.
- `t`: the second string.

**Output**
- `True` if the strings are anagrams; otherwise `False`.

## Constraints
- `1 <= s.length, t.length <= 5 * 10^4`
- `s` and `t` consist of lowercase English letters.

## Examples
```text
Input: s = "anagram", t = "nagaram"
Output: True
Explanation: Both strings contain `a` three times and `n`, `g`, `r`, and `m` once each, so they are anagrams.
```

## Understanding & Intuition
Anagrams are equal multisets of characters. Sorting canonicalizes the strings, while counting characters is linear and more direct.

## Approach 1 — Naive / Brute Force
**Idea:** Remove matching characters one by one.
```python
class Solution:
    def isAnagram(self, s: str, t: str) -> bool:
        if len(s) != len(t):
            return False
        rest = list(t)
        for ch in s:
            for i, other in enumerate(rest):
                if ch == other:
                    rest.pop(i)
                    break
            else:
                return False
        return not rest
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort both strings.
```python
class Solution:
    def isAnagram(self, s: str, t: str) -> bool:
        return sorted(s) == sorted(t)
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Count in `s` and subtract using `t`.
```python
class Solution:
    def isAnagram(self, s: str, t: str) -> bool:
        if len(s) != len(t):
            return False
        counts = {}
        for ch in s:
            counts[ch] = counts.get(ch, 0) + 1
        for ch in t:
            if counts.get(ch, 0) == 0:
                return False
            counts[ch] -= 1
        return True
```
- **Time:** O(n) — **Space:** O(k)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(k) |

## Edge Cases & Pitfalls
- Check lengths first.
- Use a dict for general characters.
- A missing character fails immediately.

## Related
- Group Anagrams
- Find All Anagrams in a String
