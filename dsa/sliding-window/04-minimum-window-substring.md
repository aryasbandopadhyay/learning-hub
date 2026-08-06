# 04. Minimum Window Substring

- **Difficulty:** Hard
- **Pattern:** Sliding Window
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given strings `s` and `t`, return the minimum window substring of `s` that contains every character of `t` with multiplicity. Return `""` if no such window exists. Constraints: `1 <= len(s), len(t) <= 10^5`; characters are English letters.

## Examples
```text
Input: s = "ADOBECODEBANC", t = "ABC"
Output: "BANC"
Explanation: "BANC" is the shortest substring containing A, B, and C.
```

## Understanding & Intuition
The key is to know when a window satisfies all required counts. Once valid, shrinking from the left can only improve length until validity breaks. This creates the classic expand-then-contract sliding window.

## Approach 1 — Naive / Brute Force
**Idea:** Test every substring and keep the shortest one that covers `t`.
```python
from collections import Counter

class Solution:
    def minWindow(self, s: str, t: str) -> str:
        need = Counter(t)
        best = ""
        for left in range(len(s)):
            have = Counter()
            for right in range(left, len(s)):
                have[s[right]] += 1
                if all(have[ch] >= cnt for ch, cnt in need.items()):
                    candidate = s[left:right + 1]
                    if not best or len(candidate) < len(best):
                        best = candidate
                    break
        return best
```
- **Time:** O(n^2 * u) — **Space:** O(u)

## Approach 2 — Better
**Idea:** Track satisfied required characters while expanding and contracting one window.
```python
from collections import Counter, defaultdict

class Solution:
    def minWindow(self, s: str, t: str) -> str:
        need = Counter(t)
        have = defaultdict(int)
        required = len(need)
        formed = left = 0
        best_len = float("inf")
        best_range = (0, 0)

        for right, ch in enumerate(s):
            have[ch] += 1
            if ch in need and have[ch] == need[ch]:
                formed += 1
            while formed == required:
                if right - left + 1 < best_len:
                    best_len = right - left + 1
                    best_range = (left, right + 1)
                drop = s[left]
                have[drop] -= 1
                if drop in need and have[drop] < need[drop]:
                    formed -= 1
                left += 1
        return "" if best_len == float("inf") else s[best_range[0]:best_range[1]]
```
- **Time:** O(n) — **Space:** O(u)

## Approach 3 — Optimal
**Idea:** Filter `s` to only characters in `t`, reducing work on irrelevant characters.
```python
from collections import Counter, defaultdict

class Solution:
    def minWindow(self, s: str, t: str) -> str:
        need = Counter(t)
        filtered = [(i, ch) for i, ch in enumerate(s) if ch in need]
        have = defaultdict(int)
        required = len(need)
        formed = left = 0
        best_len = float("inf")
        best = (0, 0)

        for right, (idx, ch) in enumerate(filtered):
            have[ch] += 1
            if have[ch] == need[ch]:
                formed += 1
            while formed == required:
                start = filtered[left][0]
                if idx - start + 1 < best_len:
                    best_len = idx - start + 1
                    best = (start, idx + 1)
                drop = filtered[left][1]
                have[drop] -= 1
                if have[drop] < need[drop]:
                    formed -= 1
                left += 1
        return "" if best_len == float("inf") else s[best[0]:best[1]]
```
- **Time:** O(n) — **Space:** O(n + u)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2 * u) | O(u) |
| Better | O(n) | O(u) |
| Optimal | O(n) | O(n + u) |

## Edge Cases & Pitfalls
- Multiplicity matters: `t = "AABC"` needs two `A`s.
- Return `""` when no valid window exists.
- Update `formed` only when a required count crosses its threshold.

## Related
- Permutation in String
- Find All Anagrams in a String

