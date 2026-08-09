# 05. Minimum ASCII Delete Sum for Two Strings

- **Difficulty:** Medium
- **Pattern:** DP on strings
- **Asked at:** Amazon, Google, Microsoft

## Problem
Implement `minimumDeleteSum` for **Minimum ASCII Delete Sum for Two Strings**. Given strings `s1` and `s2`, return the minimum total ASCII value of deleted characters needed to make the two strings equal.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `s1`: string; first string.
- `s2`: string; second string.

**Output**
- A single integer.

## Constraints
- `0 <= len(s1), len(s2) <= 1000`
- lowercase English letters

## Examples
```text
Input: s1 = "sea", s2 = "eat"
Output: 231
Explanation: Delete 's' and 't', costing 115 + 116. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Equal characters can be kept for free. If they differ, delete one side and pay its ASCII value. Suffix states overlap heavily.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively delete from either side on mismatch.
```python
class Solution:
    def minimumDeleteSum(self, s1: str, s2: str) -> int:
        def dfs(i, j):
            if i == len(s1):
                return sum(ord(c) for c in s2[j:])
            if j == len(s2):
                return sum(ord(c) for c in s1[i:])
            if s1[i] == s2[j]:
                return dfs(i + 1, j + 1)
            return min(ord(s1[i]) + dfs(i + 1, j), ord(s2[j]) + dfs(i, j + 1))
        return dfs(0, 0)
```
- **Time:** O(2^(n+m)*(n+m)) — **Space:** O(n+m)

## Approach 2 — Better
**Idea:** Memoize suffix states and precompute suffix ASCII sums.
```python
class Solution:
    def minimumDeleteSum(self, s1, s2):
        from functools import lru_cache
        a = [0] * (len(s1) + 1)
        b = [0] * (len(s2) + 1)
        for i in range(len(s1) - 1, -1, -1):
            a[i] = a[i + 1] + ord(s1[i])
        for j in range(len(s2) - 1, -1, -1):
            b[j] = b[j + 1] + ord(s2[j])
        @lru_cache(None)
        def dfs(i, j):
            if i == len(s1):
                return b[j]
            if j == len(s2):
                return a[i]
            if s1[i] == s2[j]:
                return dfs(i + 1, j + 1)
            return min(ord(s1[i]) + dfs(i + 1, j), ord(s2[j]) + dfs(i, j + 1))
        return dfs(0, 0)
```
- **Time:** O(n*m) — **Space:** O(n*m)

## Approach 3 — Optimal
**Idea:** Bottom-up suffix DP using one row for `s2`.
```python
class Solution:
    def minimumDeleteSum(self, s1, s2):
        m = len(s2)
        dp = [0] * (m + 1)
        for j in range(m - 1, -1, -1):
            dp[j] = dp[j + 1] + ord(s2[j])
        for i in range(len(s1) - 1, -1, -1):
            ndp = [0] * (m + 1)
            ndp[m] = dp[m] + ord(s1[i])
            for j in range(m - 1, -1, -1):
                if s1[i] == s2[j]:
                    ndp[j] = dp[j + 1]
                else:
                    ndp[j] = min(ord(s1[i]) + dp[j], ord(s2[j]) + ndp[j + 1])
            dp = ndp
        return dp[0]
```
- **Time:** O(n*m) — **Space:** O(m)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^(n+m)*(n+m)) | O(n+m) |
| Better | O(n*m) | O(n*m) |
| Optimal | O(n*m) | O(m) |

## Edge Cases & Pitfalls
- Empty strings cost the other string's ASCII sum.
- This is weighted deletion, not edit distance.
- Keep equal characters.

## Related
- Delete Operation for Two Strings
- Edit Distance
