# 08. Replace the Substring for Balanced String

- **Difficulty:** Medium
- **Pattern:** Sliding Window (Fixed & Variable)
- **Asked at:** Google

## Problem
A string `s` of length divisible by 4 contains only `Q`, `W`, `E`, and `R`. Return the minimum length substring that can be replaced so that each character appears exactly `len(s) / 4` times.

Constraints: `4 <= len(s) <= 100000`; `len(s)` is divisible by 4.

## Examples
```text
Input: s = "WQWRQQQW"
Output: 3
Explanation: Replacing a length-3 substring can balance all four character counts.
```

## Understanding & Intuition
The replaced substring can contain anything, so only the characters left outside it must already be no more than the target count. We therefore seek the shortest window whose removal makes outside counts valid. Sliding the candidate replacement window gives the minimum.

## Approach 1 — Naive / Brute Force
**Idea:** Try every substring removal and count the outside characters from scratch.
```python
class Solution:
    def balancedString(self, s: str) -> int:
        n = len(s)
        target = n // 4
        best = n
        chars = 'QWER'
        for i in range(n):
            for j in range(i, n):
                outside = {c: 0 for c in chars}
                for p in range(n):
                    if p < i or p > j:
                        outside[s[p]] += 1
                ok = True
                for c in chars:
                    if outside[c] > target:
                        ok = False
                if ok:
                    best = min(best, j - i + 1)
        if all(s.count(c) == target for c in chars):
            return 0
        return best
```
- **Time:** O(n^3) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Build prefix counts so outside counts for each candidate window are available in O(1).
```python
class Solution:
    def balancedString(self, s: str) -> int:
        n = len(s)
        target = n // 4
        chars = 'QWER'
        pref = [{c: 0 for c in chars}]
        for ch in s:
            cur = pref[-1].copy()
            cur[ch] += 1
            pref.append(cur)
        if all(pref[n][c] == target for c in chars):
            return 0
        best = n
        for i in range(n):
            for j in range(i, n):
                ok = True
                for c in chars:
                    inside = pref[j + 1][c] - pref[i][c]
                    if pref[n][c] - inside > target:
                        ok = False
                if ok:
                    best = min(best, j - i + 1)
        return best
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Move right to remove characters from outside counts, then shrink left while outside is balanced.
```python
class Solution:
    def balancedString(self, s: str) -> int:
        n = len(s)
        target = n // 4
        count = {'Q': 0, 'W': 0, 'E': 0, 'R': 0}
        for ch in s:
            count[ch] += 1
        if all(count[c] == target for c in count):
            return 0
        left = 0
        ans = n
        for right, ch in enumerate(s):
            count[ch] -= 1
            while left <= right and all(count[c] <= target for c in count):
                ans = min(ans, right - left + 1)
                count[s[left]] += 1
                left += 1
        return ans
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(1) |
| Better | O(n^2) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Already balanced strings return `0`.
- Check outside counts, not counts inside the replacement window.

## Related
- Minimum Window Substring
- Get Equal Substrings Within Budget
