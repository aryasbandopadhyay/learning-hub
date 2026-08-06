# 03. Permutation in String

- **Difficulty:** Medium
- **Pattern:** Sliding Window
- **Asked at:** Amazon, Microsoft, Google, Meta

## Problem
Given strings `s1` and `s2`, return `True` if `s2` contains a substring that is a permutation of `s1`; otherwise return `False`. Constraints: `1 <= len(s1), len(s2) <= 10^4`; strings contain lowercase English letters.

## Examples
```text
Input: s1 = "ab", s2 = "eidbaooo"
Output: True
Explanation: "ba" is a permutation of "ab".
```

## Understanding & Intuition
All candidate substrings have fixed length `len(s1)`. Instead of sorting or recounting every candidate, slide one fixed-size window across `s2`. Matching character frequencies means the current window is a permutation.

## Approach 1 — Naive / Brute Force
**Idea:** Sort each length-`m` substring and compare it with sorted `s1`.
```python
class Solution:
    def checkInclusion(self, s1: str, s2: str) -> bool:
        m = len(s1)
        target = sorted(s1)
        for i in range(len(s2) - m + 1):
            if sorted(s2[i:i + m]) == target:
                return True
        return False
```
- **Time:** O((n-m+1) * m log m) — **Space:** O(m)

## Approach 2 — Better
**Idea:** Maintain frequency dictionaries for the fixed window and compare them.
```python
from collections import Counter

class Solution:
    def checkInclusion(self, s1: str, s2: str) -> bool:
        m = len(s1)
        if m > len(s2):
            return False
        need = Counter(s1)
        window = Counter(s2[:m])
        if window == need:
            return True
        for right in range(m, len(s2)):
            left_char = s2[right - m]
            window[left_char] -= 1
            if window[left_char] == 0:
                del window[left_char]
            window[s2[right]] += 1
            if window == need:
                return True
        return False
```
- **Time:** O(26n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Track how many of the 26 letter counts match between target and window.
```python
class Solution:
    def checkInclusion(self, s1: str, s2: str) -> bool:
        m, n = len(s1), len(s2)
        if m > n:
            return False
        need = [0] * 26
        window = [0] * 26
        for i in range(m):
            need[ord(s1[i]) - 97] += 1
            window[ord(s2[i]) - 97] += 1
        matches = sum(1 for i in range(26) if need[i] == window[i])
        if matches == 26:
            return True

        for right in range(m, n):
            add = ord(s2[right]) - 97
            remove = ord(s2[right - m]) - 97
            # Update match count before and after each changed bucket.
            matches -= need[add] == window[add]
            window[add] += 1
            matches += need[add] == window[add]
            matches -= need[remove] == window[remove]
            window[remove] -= 1
            matches += need[remove] == window[remove]
            if matches == 26:
                return True
        return False
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((n-m+1) * m log m) | O(m) |
| Better | O(26n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- If `s1` is longer than `s2`, no permutation exists.
- Fixed-size windows should remove exactly one old character for each new one.
- Delete zero-count keys when comparing dictionaries.

## Related
- Find All Anagrams in a String
- Minimum Window Substring

