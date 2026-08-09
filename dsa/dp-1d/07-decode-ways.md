# 07. Decode Ways

- **Difficulty:** Medium
- **Pattern:** 1-D Dynamic Programming
- **Asked at:** Amazon, Google, Microsoft, Meta

## Problem
A message is encoded by mapping `A` to `1`, `B` to `2`, ..., and `Z` to `26`. Given a digit string
`s`, count how many ways it can be decoded into letters. A code cannot contain leading zeroes, so
`"06"` is invalid.

**Input**
- `s`: a string of digits.

**Output**
- An integer: the number of valid decodings.

## Constraints
- 1 <= s.length <= 100
- `s` contains only digits.

## Examples
```text
Input: s = "226"
Output: 3
Explanation: `"226"` can be decoded as `2|2|6` (`BBF`), `22|6` (`VF`), or `2|26` (`BZ`), so there are `3` ways.
```

## Understanding & Intuition
Let `dp[i]` be the number of decodings from index `i`. A nonzero single digit can be decoded, and a two-digit number from 10 to 26 can also be decoded. Thus `dp[i]` adds `dp[i+1]` and possibly `dp[i+2]`.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively try one-digit and two-digit choices.
```python
class Solution:
    def numDecodings(self, s: str) -> int:
        def dfs(i: int) -> int:
            if i == len(s):
                return 1
            if s[i] == "0":
                return 0
            total = dfs(i + 1)
            if i + 1 < len(s) and 10 <= int(s[i:i + 2]) <= 26:
                total += dfs(i + 2)
            return total

        return dfs(0)
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize the number of decodings starting at each index.
```python
class Solution:
    def numDecodings(self, s: str) -> int:
        memo = {}

        def dfs(i: int) -> int:
            if i == len(s):
                return 1
            if s[i] == "0":
                return 0
            if i not in memo:
                memo[i] = dfs(i + 1)
                if i + 1 < len(s) and 10 <= int(s[i:i + 2]) <= 26:
                    memo[i] += dfs(i + 2)
            return memo[i]

        return dfs(0)
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Fill from right to left with two rolling values.
```python
class Solution:
    def numDecodings(self, s: str) -> int:
        next1, next2 = 1, 0  # dp[i+1], dp[i+2]
        for i in range(len(s) - 1, -1, -1):
            current = 0
            if s[i] != "0":
                current = next1
                if i + 1 < len(s) and 10 <= int(s[i:i + 2]) <= 26:
                    current += next2
            next1, next2 = current, next1
        return next1
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- A standalone `0` is invalid.
- `10` and `20` are valid, but `30` is not.

## Related
- Decode Ways II
- Word Break
