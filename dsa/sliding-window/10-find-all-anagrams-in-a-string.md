# 10. Find All Anagrams in a String

- **Difficulty:** Medium
- **Pattern:** Sliding Window
- **Asked at:** Amazon, Microsoft, Google, Adobe

## Problem
Given strings `s` and `p`, return all start indexes of `p`'s anagrams in `s` in any order. Constraints: `1 <= len(s), len(p) <= 3 * 10^4`; strings contain lowercase English letters.

## Examples
```text
Input: s = "cbaebabacd", p = "abc"
Output: [0, 6]
Explanation: "cba" and "bac" are anagrams of "abc".
```

## Understanding & Intuition
Every candidate window has length `len(p)`. A window is an anagram exactly when its character counts match `p`'s counts. Sliding one character at a time avoids rebuilding the full count from scratch.

## Approach 1 — Naive / Brute Force
**Idea:** Sort every candidate substring and compare it to sorted `p`.
```python
from typing import List

class Solution:
    def findAnagrams(self, s: str, p: str) -> List[int]:
        m = len(p)
        target = sorted(p)
        ans = []
        for i in range(len(s) - m + 1):
            if sorted(s[i:i + m]) == target:
                ans.append(i)
        return ans
```
- **Time:** O((n-m+1) * m log m) — **Space:** O(m)

## Approach 2 — Better
**Idea:** Slide a frequency counter and compare it with the target counter.
```python
from typing import List
from collections import Counter

class Solution:
    def findAnagrams(self, s: str, p: str) -> List[int]:
        m = len(p)
        if m > len(s):
            return []
        need = Counter(p)
        window = Counter(s[:m])
        ans = [0] if window == need else []
        for right in range(m, len(s)):
            old = s[right - m]
            window[old] -= 1
            if window[old] == 0:
                del window[old]
            window[s[right]] += 1
            if window == need:
                ans.append(right - m + 1)
        return ans
```
- **Time:** O(26n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use 26-length arrays and a match count for constant-time equality checks.
```python
from typing import List

class Solution:
    def findAnagrams(self, s: str, p: str) -> List[int]:
        m, n = len(p), len(s)
        if m > n:
            return []
        need = [0] * 26
        window = [0] * 26
        for i in range(m):
            need[ord(p[i]) - 97] += 1
            window[ord(s[i]) - 97] += 1
        matches = sum(need[i] == window[i] for i in range(26))
        ans = [0] if matches == 26 else []
        for right in range(m, n):
            add = ord(s[right]) - 97
            remove = ord(s[right - m]) - 97
            matches -= need[add] == window[add]
            window[add] += 1
            matches += need[add] == window[add]
            matches -= need[remove] == window[remove]
            window[remove] -= 1
            matches += need[remove] == window[remove]
            if matches == 26:
                ans.append(right - m + 1)
        return ans
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((n-m+1) * m log m) | O(m) |
| Better | O(26n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- If `p` is longer than `s`, return an empty list.
- Output indexes should be start positions, not end positions.
- Keep the window exactly length `len(p)`.

## Related
- Permutation in String
- Minimum Window Substring

