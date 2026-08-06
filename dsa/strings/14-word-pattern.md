# 14. Word Pattern

- **Difficulty:** Easy
- **Pattern:** Strings
- **Asked at:** Amazon, Uber, Google, Microsoft

## Problem
Given a pattern string and a space-separated string `s`, return `True` if `s` follows the same pattern. Each pattern character must map to exactly one word, and each word must map to exactly one pattern character. Constraints: `1 <= len(pattern) <= 300`; `s` contains lowercase words separated by single spaces.

## Examples
```text
Input: pattern = "abba", s = "dog cat cat dog"
Output: true
Explanation: 'a' maps to "dog" and 'b' maps to "cat".
```

## Understanding & Intuition
This is isomorphism between pattern characters and words. The number of words must match the pattern length. We need a bijection, so checking only character-to-word mapping is insufficient.

## Approach 1 — Naive / Brute Force
**Idea:** Compare equality relationships for every pair of positions.
```python
class Solution:
    def wordPattern(self, pattern: str, s: str) -> bool:
        words = s.split()
        if len(pattern) != len(words):
            return False
        for i in range(len(pattern)):
            for j in range(i + 1, len(pattern)):
                if (pattern[i] == pattern[j]) != (words[i] == words[j]):
                    return False
        return True
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Maintain two maps: pattern-to-word and word-to-pattern.
```python
class Solution:
    def wordPattern(self, pattern: str, s: str) -> bool:
        words = s.split()
        if len(pattern) != len(words):
            return False
        p_to_w = {}
        w_to_p = {}
        for p, word in zip(pattern, words):
            if p in p_to_w and p_to_w[p] != word:
                return False
            if word in w_to_p and w_to_p[word] != p:
                return False
            p_to_w[p] = word
            w_to_p[word] = p
        return True
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Compare first-seen position patterns of the pattern string and word list.
```python
class Solution:
    def wordPattern(self, pattern: str, s: str) -> bool:
        words = s.split()
        if len(pattern) != len(words):
            return False

        def encode(items) -> list[int]:
            first_seen = {}
            result = []
            for i, item in enumerate(items):
                if item not in first_seen:
                    first_seen[item] = i
                result.append(first_seen[item])
            return result

        return encode(pattern) == encode(words)
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Pattern length and word count must be equal.
- The mapping must be bijective.
- Repeated words are meaningful even when pattern characters differ.

## Related
- Isomorphic Strings
- Hash Maps
- String Split
