# 07. Valid Parenthesis String

- **Difficulty:** Medium
- **Pattern:** Greedy
- **Asked at:** Meta, Amazon, Google, Microsoft

## Problem
Given a string `s` containing only `'('`, `')'`, and `'*'`, return `True` if `s` can be valid after treating each `'*'` as `'('`, `')'`, or an empty string. Constraints: `1 <= len(s) <= 100`.

## Examples
```text
Input: s = "(*)"
Output: True
Explanation: Treat '*' as an empty string to get "()".
```

## Understanding & Intuition
The number of unmatched open parentheses is not fixed because `*` is flexible. Track the minimum and maximum possible open counts after each character. Greedily keeping this range is safe because all choices are summarized by the interval.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively try all three meanings of every `*`.
```python
class Solution:
    def checkValidString(self, s: str) -> bool:
        def dfs(i: int, opened: int) -> bool:
            if opened < 0:
                return False
            if i == len(s):
                return opened == 0
            if s[i] == "(":
                return dfs(i + 1, opened + 1)
            if s[i] == ")":
                return dfs(i + 1, opened - 1)
            return (
                dfs(i + 1, opened + 1) or
                dfs(i + 1, opened - 1) or
                dfs(i + 1, opened)
            )

        return dfs(0, 0)
```
- **Time:** O(3^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize index and current open count.
```python
from functools import lru_cache

class Solution:
    def checkValidString(self, s: str) -> bool:
        @lru_cache(None)
        def possible(i: int, opened: int) -> bool:
            if opened < 0:
                return False
            if i == len(s):
                return opened == 0
            if s[i] == "(":
                return possible(i + 1, opened + 1)
            if s[i] == ")":
                return possible(i + 1, opened - 1)
            return any((
                possible(i + 1, opened + 1),
                possible(i + 1, opened - 1),
                possible(i + 1, opened),
            ))

        return possible(0, 0)
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Maintain a range `[low, high]` of possible unmatched opens.
```python
class Solution:
    def checkValidString(self, s: str) -> bool:
        low = 0
        high = 0

        for ch in s:
            if ch == "(":
                low += 1
                high += 1
            elif ch == ")":
                low -= 1
                high -= 1
            else:
                low -= 1      # Treat '*' as ')' or empty for the minimum.
                high += 1     # Treat '*' as '(' for the maximum.

            if high < 0:
                return False
            low = max(low, 0)

        return low == 0
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(3^n) | O(n) |
| Better | O(n^2) | O(n^2) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- `low` must never stay negative; clamp it to zero.
- If `high` becomes negative, too many closing parentheses appeared.
- A valid final state requires `low == 0`.

## Related
- Generate Parentheses
- Parentheses Validation
