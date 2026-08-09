# 12. Strange Printer

- **Difficulty:** Hard
- **Pattern:** DP on strings
- **Asked at:** Google, Amazon, Meta

## Problem
Implement `strangePrinter` for **Strange Printer**. A printer can print a run of one repeated character over any contiguous interval in one turn, overwriting existing characters. Given `s`, return the fewest turns to print it.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `s`: string; input string.

**Output**
- A single integer.

## Constraints
- `1 <= len(s) <= 100`
- lowercase English letters

## Examples
```text
Input: s = "aba"
Output: 2
Explanation: Print "aaa", then overwrite the middle with "b". This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Printing the last character separately is always possible. If that character appeared earlier, one turn may cover both occurrences and save work. This leads to interval DP over possible merge positions.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively print the right end alone or merge it with an equal earlier character.
```python
class Solution:
    def strangePrinter(self, s: str) -> int:
        def dfs(l, r):
            if l > r:
                return 0
            ans = dfs(l, r - 1) + 1
            for mid in range(l, r):
                if s[mid] == s[r]:
                    ans = min(ans, dfs(l, mid) + dfs(mid + 1, r - 1))
            return ans
        return dfs(0, len(s) - 1)
```
- **Time:** Exponential — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize interval results.
```python
class Solution:
    def strangePrinter(self, s):
        from functools import lru_cache
        @lru_cache(None)
        def dfs(l, r):
            if l > r:
                return 0
            ans = dfs(l, r - 1) + 1
            for mid in range(l, r):
                if s[mid] == s[r]:
                    ans = min(ans, dfs(l, mid) + dfs(mid + 1, r - 1))
            return ans
        return dfs(0, len(s) - 1)
```
- **Time:** O(n^3) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Compress adjacent duplicates, then fill interval DP bottom-up.
```python
class Solution:
    def strangePrinter(self, s):
        comp = []
        for ch in s:
            if not comp or comp[-1] != ch:
                comp.append(ch)
        s = "".join(comp)
        n = len(s)
        dp = [[0] * n for _ in range(n)]
        for i in range(n):
            dp[i][i] = 1
        for length in range(2, n + 1):
            for l in range(n - length + 1):
                r = l + length - 1
                dp[l][r] = dp[l][r - 1] + 1
                for mid in range(l, r):
                    if s[mid] == s[r]:
                        inside = dp[mid + 1][r - 1] if mid + 1 <= r - 1 else 0
                        dp[l][r] = min(dp[l][r], dp[l][mid] + inside)
        return dp[0][n - 1]
```
- **Time:** O(n^3) — **Space:** O(n^2)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | Exponential | O(n) |
| Better | O(n^3) | O(n^2) |
| Optimal | O(n^3) | O(n^2) |

## Edge Cases & Pitfalls
- Adjacent duplicates can be compressed safely.
- Overwriting is allowed.
- Empty inner intervals cost zero turns.

## Related
- Encode String with Shortest Length
- Palindrome Partitioning II
