# 11. Palindrome Partitioning II

- **Difficulty:** Hard
- **Pattern:** DP on strings
- **Asked at:** Amazon, Google, Meta

## Problem
Implement `minCut` for **Palindrome Partitioning II**. Given a string `s`, return the minimum number of cuts needed to partition it so every substring is a palindrome.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `s`: string; input string.

**Output**
- A single integer.

## Constraints
- `1 <= len(s) <= 2000`
- lowercase English letters

## Examples
```text
Input: s = "aab"
Output: 1
Explanation: The partition "aa" | "b" needs one cut. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
A cut is useful when the piece after it is a palindrome. We can minimize palindrome pieces and subtract one, or directly relax cuts while discovering palindromes. Center expansion avoids storing all intervals.

## Approach 1 — Naive / Brute Force
**Idea:** Try every palindromic prefix recursively.
```python
class Solution:
    def minCut(self, s: str) -> int:
        def is_pal(x):
            return x == x[::-1]
        def dfs(i):
            if i == len(s):
                return 0
            best = len(s) - i
            for j in range(i + 1, len(s) + 1):
                if is_pal(s[i:j]):
                    best = min(best, 1 + dfs(j))
            return best
        return dfs(0) - 1
```
- **Time:** O(n^2*2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Precompute palindromic intervals and memoize minimum pieces.
```python
class Solution:
    def minCut(self, s):
        from functools import lru_cache
        n = len(s)
        pal = [[False] * n for _ in range(n)]
        for length in range(1, n + 1):
            for l in range(n - length + 1):
                r = l + length - 1
                pal[l][r] = s[l] == s[r] and (length <= 2 or pal[l + 1][r - 1])
        @lru_cache(None)
        def dfs(i):
            if i == n:
                return 0
            return 1 + min(dfs(j + 1) for j in range(i, n) if pal[i][j])
        return dfs(0) - 1
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Expand palindromes around each center and relax cut counts.
```python
class Solution:
    def minCut(self, s):
        n = len(s)
        cuts = [i - 1 for i in range(n + 1)]
        for c in range(n):
            l = r = c
            while l >= 0 and r < n and s[l] == s[r]:
                cuts[r + 1] = min(cuts[r + 1], cuts[l] + 1)
                l -= 1; r += 1
            l, r = c, c + 1
            while l >= 0 and r < n and s[l] == s[r]:
                cuts[r + 1] = min(cuts[r + 1], cuts[l] + 1)
                l -= 1; r += 1
        return cuts[n]
```
- **Time:** O(n^2) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2*2^n) | O(n) |
| Better | O(n^2) | O(n^2) |
| Optimal | O(n^2) | O(n) |

## Edge Cases & Pitfalls
- Whole-string palindromes need `0` cuts.
- Distinguish pieces from cuts.
- Expand both odd and even centers.

## Related
- Palindrome Partitioning
- Minimum Insertion Steps to Make a String Palindrome
