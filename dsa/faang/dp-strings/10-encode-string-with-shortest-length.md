# 10. Encode String with Shortest Length

- **Difficulty:** Hard
- **Pattern:** DP on strings
- **Asked at:** Google, Amazon, Meta

## Problem
Implement `encode` for **Encode String with Shortest Length**. Given a non-empty string `s`, return its shortest encoding using `k[encoded_string]` for repeated substrings. If lengths tie, choose candidates in this order: original substring, repeated form, then left-to-right splits.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

This judge compares exactly; when encodings tie in length, use the deterministic tie rule stated in the description.

**Input**
- `s`: string; input string.

**Output**
- A string. This judge compares exactly; when encodings tie in length, use the deterministic tie rule stated in the description.

## Constraints
- `1 <= len(s) <= 160`
- lowercase English letters

## Examples
```text
Input: s = "aaaabaaaab"
Output: "2[aaaab]"
Explanation: The string is two repetitions of "aaaab". This is the required result for the given input under the rules above.
```

## Understanding & Intuition
An optimal encoded substring is either left raw, encoded as repetitions of a smaller unit, or split into two optimal encodings. Interval DP tries those options for all substrings. A fixed candidate order keeps output deterministic.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively try repetition and every split.
```python
class Solution:
    def encode(self, s: str) -> str:
        def better(a, b):
            return a if len(a) <= len(b) else b
        def period(x):
            i = (x + x).find(x, 1)
            return i if i < len(x) else 0
        def dfs(x):
            ans = x
            p = period(x)
            if p:
                ans = better(ans, str(len(x) // p) + "[" + dfs(x[:p]) + "]")
            for cut in range(1, len(x)):
                ans = better(ans, dfs(x[:cut]) + dfs(x[cut:]))
            return ans
        return dfs(s)
```
- **Time:** Exponential — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize the shortest encoding for each substring value.
```python
class Solution:
    def encode(self, s):
        from functools import lru_cache
        def better(a, b):
            return a if len(a) <= len(b) else b
        def period(x):
            i = (x + x).find(x, 1)
            return i if i < len(x) else 0
        @lru_cache(None)
        def dfs(x):
            ans = x
            p = period(x)
            if p:
                ans = better(ans, str(len(x) // p) + "[" + dfs(x[:p]) + "]")
            for cut in range(1, len(x)):
                ans = better(ans, dfs(x[:cut]) + dfs(x[cut:]))
            return ans
        return dfs(s)
```
- **Time:** O(n^4) — **Space:** O(n^3)

## Approach 3 — Optimal
**Idea:** Fill interval DP by length.
```python
class Solution:
    def encode(self, s):
        n = len(s)
        dp = [[""] * n for _ in range(n)]
        def better(a, b):
            return a if len(a) <= len(b) else b
        for length in range(1, n + 1):
            for l in range(n - length + 1):
                r = l + length - 1
                sub = s[l:r + 1]
                ans = sub
                p = (sub + sub).find(sub, 1)
                if p < len(sub):
                    ans = better(ans, str(length // p) + "[" + dp[l][l + p - 1] + "]")
                for mid in range(l, r):
                    ans = better(ans, dp[l][mid] + dp[mid + 1][r])
                dp[l][r] = ans
        return dp[0][n - 1]
```
- **Time:** O(n^4) — **Space:** O(n^3)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | Exponential | O(n) |
| Better | O(n^4) | O(n^3) |
| Optimal | O(n^4) | O(n^3) |

## Edge Cases & Pitfalls
- Do not encode unless the encoded form is no longer than the current best under the tie rule.
- The repeated unit may itself be encoded.
- Splitting can beat one repeated block.

## Related
- Scramble String
- Shortest Common Supersequence
