# 06. Number of Substrings Containing All Three Characters

- **Difficulty:** Medium
- **Pattern:** Sliding Window (Fixed & Variable)
- **Asked at:** Google

## Problem
Given a string `s` consisting only of `'a'`, `'b'`, and `'c'`, return the number of substrings containing at least one occurrence of all three characters.

Constraints: `3 <= len(s) <= 100000`.

## Examples
```text
Input: s = "abcabc"
Output: 10
Explanation: There are 10 substrings that contain `a`, `b`, and `c` at least once.
```

## Understanding & Intuition
Once a window contains all three characters, any extension to the right remains valid. Counting many substrings at once avoids enumerating all endings. Last-seen indices give an even shorter way to count valid starts for each ending.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate substrings and scan each one for all three letters.
```python
class Solution:
    def numberOfSubstrings(self, s: str) -> int:
        n = len(s)
        ans = 0
        for i in range(n):
            for j in range(i, n):
                seen = set()
                for p in range(i, j + 1):
                    seen.add(s[p])
                if len(seen) == 3:
                    ans += 1
        return ans
```
- **Time:** O(n^3) — **Space:** O(1)

## Approach 2 — Better
**Idea:** For each start, extend right and add all remaining endings once all three letters appear.
```python
class Solution:
    def numberOfSubstrings(self, s: str) -> int:
        n = len(s)
        ans = 0
        for i in range(n):
            count = {'a': 0, 'b': 0, 'c': 0}
            have = 0
            for j in range(i, n):
                if count[s[j]] == 0:
                    have += 1
                count[s[j]] += 1
                if have == 3:
                    ans += n - j
                    break
        return ans
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Track the last index of each character; the minimum last index determines valid starts.
```python
class Solution:
    def numberOfSubstrings(self, s: str) -> int:
        last = {'a': -1, 'b': -1, 'c': -1}
        ans = 0
        for i, ch in enumerate(s):
            last[ch] = i
            earliest = min(last['a'], last['b'], last['c'])
            if earliest != -1:
                ans += earliest + 1
        return ans
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(1) |
| Better | O(n^2) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Add `earliest + 1`, not just one, because every earlier start is valid.
- The input alphabet is exactly `a`, `b`, and `c`.

## Related
- Minimum Window Substring
- Subarrays with K Different Integers
