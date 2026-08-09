# 02. Valid Anagram

- **Difficulty:** Easy
- **Pattern:** Strings
- **Asked at:** Amazon, Meta, Google, Microsoft

## Problem
Given two strings `s` and `t`, return whether they are anagrams: each character must appear the same number of times in both strings.

**Input**
- `s`: the first lowercase English string.
- `t`: the second lowercase English string.

**Output**
- A boolean: `True` if the two strings have identical character counts, otherwise `False`.

## Constraints
- `1 <= s.length, t.length <= 5 * 10^4`
- `s` and `t` contain lowercase English letters.

## Examples
```text
Input: s = "anagram", t = "nagaram"
Output: true
Explanation: Both strings contain the same letters with the same frequencies, only in a different order.
```

## Understanding & Intuition
Anagrams are equal after reordering. Sorting exposes that order-independent equality, while counting avoids the sort. Since the alphabet is fixed, a small frequency array is enough.

## Approach 1 — Naive / Brute Force
**Idea:** For each character in `s`, remove one matching character from a mutable copy of `t`.
```python
class Solution:
    def isAnagram(self, s: str, t: str) -> bool:
        if len(s) != len(t):
            return False
        remaining = list(t)
        for ch in s:
            if ch not in remaining:
                return False
            remaining.remove(ch)  # Linear search plus deletion.
        return not remaining
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Sort both strings and compare the sorted character sequences.
```python
class Solution:
    def isAnagram(self, s: str, t: str) -> bool:
        if len(s) != len(t):
            return False
        return sorted(s) == sorted(t)
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Count letters from `s`, subtract letters from `t`, and ensure no count differs.
```python
class Solution:
    def isAnagram(self, s: str, t: str) -> bool:
        if len(s) != len(t):
            return False
        counts = [0] * 26
        for a, b in zip(s, t):
            counts[ord(a) - ord('a')] += 1
            counts[ord(b) - ord('a')] -= 1
        return all(count == 0 for count in counts)
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Different lengths cannot be anagrams.
- Frequency equality matters, not only character set equality.
- The O(1) space claim depends on the fixed lowercase alphabet.

## Related
- Group Anagrams
- Ransom Note
- Find All Anagrams in a String
