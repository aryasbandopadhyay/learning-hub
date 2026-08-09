# 12. Is Subsequence

- **Difficulty:** Easy
- **Pattern:** Two Pointers
- **Asked at:** Google, Meta, Amazon, Bloomberg

## Problem
Given strings `s` and `t`, determine whether `s` is a subsequence of `t`. A subsequence keeps characters in order while skipping any characters from `t`.

**Input**
- `s`: the candidate subsequence.
- `t`: the source string.

**Output**
- `True` if every character of `s` can be matched in order within `t`; otherwise `False`.

## Constraints
- `0 <= s.length <= 100`
- `0 <= t.length <= 10^4`
- `s` and `t` consist of lowercase English letters.

## Examples
```text
Input: s = "abc", t = "ahbgdc"
Output: True
Explanation: The characters `a`, `b`, and `c` appear in `t` in that order, so `abc` is a subsequence.
```

## Understanding & Intuition
We need to match all characters of `s` in order, not necessarily contiguously. A pointer into `s` advances only when the current character in `t` matches it.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively choose matching or skipping positions.
```python
class Solution:
    def isSubsequence(self, s: str, t: str) -> bool:
        def dfs(i: int, j: int) -> bool:
            if i == len(s):
                return True
            if j == len(t):
                return False
            if s[i] == t[j] and dfs(i + 1, j + 1):
                return True
            return dfs(i, j + 1)

        return dfs(0, 0)
```
- **Time:** O(2^m) worst case — **Space:** O(m)

## Approach 2 — Better
**Idea:** Use repeated `find` calls to locate each next character after the previous match.
```python
class Solution:
    def isSubsequence(self, s: str, t: str) -> bool:
        start = 0
        for ch in s:
            pos = t.find(ch, start)
            if pos == -1:
                return False
            start = pos + 1
        return True
```
- **Time:** O(len(s) * len(t)) worst case — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Scan `t` once with a pointer tracking how much of `s` has matched.
```python
class Solution:
    def isSubsequence(self, s: str, t: str) -> bool:
        i = 0
        for ch in t:
            if i < len(s) and s[i] == ch:
                i += 1
        return i == len(s)
```
- **Time:** O(len(t)) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^m) worst | O(m) |
| Better | O(len(s) * len(t)) worst | O(1) |
| Optimal | O(len(t)) | O(1) |

## Edge Cases & Pitfalls
- Empty `s` is always a subsequence.
- Empty `t` only works if `s` is empty.
- Do not reset the search position in `t`.

## Related
- Number of Matching Subsequences
- Longest Common Subsequence
