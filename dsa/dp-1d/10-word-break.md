# 10. Word Break

- **Difficulty:** Medium
- **Pattern:** 1-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Meta

## Problem
Given a string `s` and a dictionary `wordDict`, determine whether `s` can be segmented into one or
more dictionary words. Words may be reused any number of times.

**Input**
- `s`: the string to segment.
- `wordDict`: a list of available words.

**Output**
- A boolean: `True` if `s` can be fully segmented into dictionary words, otherwise `False`.

## Constraints
- 1 <= s.length <= 300
- 1 <= wordDict.length <= 1000
- 1 <= wordDict[i].length <= 20
- `s` and `wordDict[i]` consist of lowercase English letters.
- All strings in `wordDict` are unique.

## Examples
```text
Input: s = "leetcode", wordDict = ["leet","code"]
Output: True
Explanation: `"leetcode"` splits into `"leet" + "code"`, and both pieces are in the dictionary.
```

## Understanding & Intuition
Let `dp[i]` mean suffix `s[i:]` can be segmented. From index `i`, try every dictionary word that matches at `i`; if `dp[i + len(word)]` is true, then `dp[i]` is true. The base case is `dp[n] = True`.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively try every matching dictionary word at the current index.
```python
from typing import List

class Solution:
    def wordBreak(self, s: str, wordDict: List[str]) -> bool:
        def dfs(i: int) -> bool:
            if i == len(s):
                return True
            for word in wordDict:
                if s.startswith(word, i) and dfs(i + len(word)):
                    return True
            return False

        return dfs(0)
```
- **Time:** O(k^n * m) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize whether each suffix index can be segmented.
```python
from typing import List

class Solution:
    def wordBreak(self, s: str, wordDict: List[str]) -> bool:
        memo = {}

        def dfs(i: int) -> bool:
            if i == len(s):
                return True
            if i not in memo:
                memo[i] = any(
                    s.startswith(word, i) and dfs(i + len(word))
                    for word in wordDict
                )
            return memo[i]

        return dfs(0)
```
- **Time:** O(n * k * m) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Fill a boolean DP array from the end of the string.
```python
from typing import List

class Solution:
    def wordBreak(self, s: str, wordDict: List[str]) -> bool:
        dp = [False] * (len(s) + 1)
        dp[len(s)] = True
        for i in range(len(s) - 1, -1, -1):
            for word in wordDict:
                if s.startswith(word, i) and dp[i + len(word)]:
                    dp[i] = True
                    break
        return dp[0]
```
- **Time:** O(n * k * m) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(k^n * m) | O(n) |
| Better | O(n * k * m) | O(n) |
| Optimal | O(n * k * m) | O(n) |

## Edge Cases & Pitfalls
- Dictionary words can be reused.
- Stop early when a valid split is found.

## Related
- Decode Ways
- Concatenated Words
