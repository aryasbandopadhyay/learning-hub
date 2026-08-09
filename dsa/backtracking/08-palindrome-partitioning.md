# 08. Palindrome Partitioning

- **Difficulty:** Medium
- **Pattern:** Backtracking
- **Asked at:** Amazon, Google, Meta, Bloomberg

## Problem
Given a string `s`, split it into one or more substrings so that every substring is a palindrome.
Return all possible palindrome partitions.

**Input**
- `s`: the string to partition.

**Output**
- A list of partitions. **This judge compares exactly**, so return partitions in left-to-right DFS
  order: at each position, try shorter palindromic prefixes before longer ones.

## Constraints
- 1 <= s.length <= 16
- `s` consists of lowercase English letters.

## Examples
```text
Input: s = "aab"
Output: [["a","a","b"],["aa","b"]]
Explanation: The valid partitions are three single letters and the partition that groups the first two letters as `"aa"`; every piece is a palindrome.
```

## Understanding & Intuition
Each cut position chooses a palindromic prefix of the remaining suffix. If the chosen piece is not a palindrome, that branch cannot lead to a valid partition. Precomputing palindrome ranges avoids repeated substring checks.

## Approach 1 — Naive / Brute Force
**Idea:** Try every possible cut pattern, then validate every piece at the end.
```python
from typing import List

class Solution:
    def partition(self, s: str) -> List[List[str]]:
        result = []
        path = []

        def dfs(start: int) -> None:
            if start == len(s):
                if all(part == part[::-1] for part in path):
                    result.append(path.copy())
                return
            for end in range(start + 1, len(s) + 1):
                path.append(s[start:end])
                dfs(end)
                path.pop()

        dfs(0)
        return result
```
- **Time:** O(n^2 * 2^n) — **Space:** O(n) auxiliary plus output

## Approach 2 — Better
**Idea:** Check whether each candidate substring is a palindrome before recursing.
```python
from typing import List

class Solution:
    def partition(self, s: str) -> List[List[str]]:
        result = []
        path = []

        def is_pal(left: int, right: int) -> bool:
            while left < right:
                if s[left] != s[right]:
                    return False
                left += 1
                right -= 1
            return True

        def backtrack(start: int) -> None:
            if start == len(s):
                result.append(path.copy())
                return
            for end in range(start, len(s)):
                if not is_pal(start, end):
                    continue
                path.append(s[start:end + 1])
                backtrack(end + 1)
                path.pop()

        backtrack(0)
        return result
```
- **Time:** O(n^2 * 2^n) — **Space:** O(n) auxiliary plus output

## Approach 3 — Optimal
**Idea:** Precompute a DP table where `pal[i][j]` tells whether `s[i:j+1]` is a palindrome.
```python
from typing import List

class Solution:
    def partition(self, s: str) -> List[List[str]]:
        n = len(s)
        pal = [[False] * n for _ in range(n)]
        for length in range(1, n + 1):
            for i in range(n - length + 1):
                j = i + length - 1
                pal[i][j] = s[i] == s[j] and (length <= 2 or pal[i + 1][j - 1])

        result = []
        path = []

        def backtrack(start: int) -> None:
            if start == n:
                result.append(path.copy())
                return
            for end in range(start, n):
                if pal[start][end]:
                    path.append(s[start:end + 1])
                    backtrack(end + 1)
                    path.pop()

        backtrack(0)
        return result
```
- **Time:** O(n^2 + n * 2^n) — **Space:** O(n^2) plus output

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2 * 2^n) | O(n) plus output |
| Better | O(n^2 * 2^n) | O(n) plus output |
| Optimal | O(n^2 + n * 2^n) | O(n^2) plus output |

## Edge Cases & Pitfalls
- Single characters are palindromes.
- Use inclusive end indices consistently.
- Copy `path` when a full partition is found.

## Related
- Restore IP Addresses
- Generate Parentheses
- Subsets
