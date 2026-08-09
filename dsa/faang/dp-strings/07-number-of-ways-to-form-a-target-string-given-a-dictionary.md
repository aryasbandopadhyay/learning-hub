# 07. Number of Ways to Form a Target String Given a Dictionary

- **Difficulty:** Hard
- **Pattern:** DP on strings
- **Asked at:** Google, Amazon, Meta

## Problem
Implement `numWays` for **Number of Ways to Form a Target String Given a Dictionary**. Given equal-length strings `words` and a string `target`, form `target` left to right by choosing characters from strictly increasing column indices. Return the number of ways modulo `1_000_000_007`.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `words`: list; dictionary words.
- `target`: string; target value or string.

**Output**
- A single integer.

## Constraints
- `1 <= len(words), len(words[i]), len(target) <= 1000`
- lowercase English letters

## Examples
```text
Input: words = ["acca", "bbbb", "caca"], target = "aba"
Output: 6
Explanation: Six word/column choices spell "aba". This is the required result for the given input under the rules above.
```

## Understanding & Intuition
After counting letters in each column, word identities no longer matter. At each column, either skip it or use it for the next target character. This is subsequence counting over columns with multiplicities.

## Approach 1 — Naive / Brute Force
**Idea:** Try every later column and every word that supplies the needed character.
```python
class Solution:
    def numWays(self, words: list[str], target: str) -> int:
        mod = 1000000007
        width = len(words[0])
        def dfs(pos, col):
            if pos == len(target):
                return 1
            total = 0
            for j in range(col, width):
                for word in words:
                    if word[j] == target[pos]:
                        total += dfs(pos + 1, j + 1)
            return total % mod
        return dfs(0, 0)
```
- **Time:** O((m*w)^t) — **Space:** O(t)

## Approach 2 — Better
**Idea:** Precompute column counts and memoize `(target position, column)`.
```python
class Solution:
    def numWays(self, words, target):
        from functools import lru_cache
        mod = 1000000007
        width = len(words[0])
        counts = [[0] * 26 for _ in range(width)]
        for word in words:
            for j, ch in enumerate(word):
                counts[j][ord(ch) - 97] += 1
        @lru_cache(None)
        def dfs(pos, col):
            if pos == len(target):
                return 1
            if col == width:
                return 0
            skip = dfs(pos, col + 1)
            use = counts[col][ord(target[pos]) - 97] * dfs(pos + 1, col + 1)
            return (skip + use) % mod
        return dfs(0, 0)
```
- **Time:** O(m*w + t*w) — **Space:** O(t*w)

## Approach 3 — Optimal
**Idea:** Sweep columns and update one-dimensional target counts backward.
```python
class Solution:
    def numWays(self, words, target):
        mod = 1000000007
        dp = [0] * (len(target) + 1)
        dp[0] = 1
        for col in range(len(words[0])):
            freq = [0] * 26
            for word in words:
                freq[ord(word[col]) - 97] += 1
            for pos in range(len(target) - 1, -1, -1):
                dp[pos + 1] = (dp[pos + 1] + dp[pos] * freq[ord(target[pos]) - 97]) % mod
        return dp[len(target)]
```
- **Time:** O(m*w + t*w) — **Space:** O(t)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((m*w)^t) | O(t) |
| Better | O(m*w + t*w) | O(t*w) |
| Optimal | O(m*w + t*w) | O(t) |

## Edge Cases & Pitfalls
- Update backward so each column is used once.
- If the target is longer than the column count, the result is `0`.
- Apply modulo after multiplication.

## Related
- Distinct Subsequences
- Wildcard Matching
