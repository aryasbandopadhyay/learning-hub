# 08. Wildcard Matching

- **Difficulty:** Hard
- **Pattern:** DP on strings
- **Asked at:** Google, Amazon, Microsoft

## Problem
Implement `isMatch` for **Wildcard Matching**. Given a string `s` and pattern `p`, return whether the whole string matches the pattern. In `p`, `?` matches one character and `*` matches any sequence, including empty.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `s`: string; input string.
- `p`: string; pattern.

**Output**
- `True` or `False`.

## Constraints
- `0 <= len(s), len(p) <= 2000`

## Examples
```text
Input: s = "adceb", p = "*a*b"
Output: True
Explanation: The stars can cover the empty prefix and "dce". This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Literal characters and `?` advance both strings. A `*` can either match nothing or consume one character. The full-string requirement makes suffix states natural.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively branch on each `*`.
```python
class Solution:
    def isMatch(self, s: str, p: str) -> bool:
        def dfs(i, j):
            if j == len(p):
                return i == len(s)
            if i == len(s):
                return all(ch == '*' for ch in p[j:])
            if p[j] == '*':
                return dfs(i, j + 1) or dfs(i + 1, j)
            if p[j] == '?' or p[j] == s[i]:
                return dfs(i + 1, j + 1)
            return False
        return dfs(0, 0)
```
- **Time:** O(2^(n+m)) — **Space:** O(n+m)

## Approach 2 — Better
**Idea:** Cache `(i, j)` states.
```python
class Solution:
    def isMatch(self, s, p):
        from functools import lru_cache
        @lru_cache(None)
        def dfs(i, j):
            if j == len(p):
                return i == len(s)
            if i == len(s):
                return all(ch == '*' for ch in p[j:])
            if p[j] == '*':
                return dfs(i, j + 1) or dfs(i + 1, j)
            if p[j] == '?' or p[j] == s[i]:
                return dfs(i + 1, j + 1)
            return False
        return dfs(0, 0)
```
- **Time:** O(n*m) — **Space:** O(n*m)

## Approach 3 — Optimal
**Idea:** Greedily remember the last `*` and expand it only after a mismatch.
```python
class Solution:
    def isMatch(self, s, p):
        i = j = 0
        star = -1
        match = 0
        while i < len(s):
            if j < len(p) and (p[j] == '?' or p[j] == s[i]):
                i += 1; j += 1
            elif j < len(p) and p[j] == '*':
                star = j; match = i; j += 1
            elif star != -1:
                j = star + 1; match += 1; i = match
            else:
                return False
        return all(ch == '*' for ch in p[j:])
```
- **Time:** O(n+m) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^(n+m)) | O(n+m) |
| Better | O(n*m) | O(n*m) |
| Optimal | O(n+m) | O(1) |

## Edge Cases & Pitfalls
- The entire string must match.
- Remaining pattern after string exhaustion must be all `*`.
- Consecutive stars behave like one star.

## Related
- Regular Expression Matching
- Number of Ways to Form a Target String Given a Dictionary
