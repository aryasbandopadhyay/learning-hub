# 04. Longest Valid Parentheses

- **Difficulty:** Hard
- **Pattern:** stack-based parsing & evaluation
- **Asked at:** Google, Amazon, Meta

## Problem
Given a string `s` containing only `'('` and `')'`, return the length of the longest contiguous substring that is a valid parentheses string.

Constraints: `0 <= len(s) <= 30_000`.

## Examples
```text
Input: s = ")()())"
Output: 4
Explanation: The longest valid substring is "()()".
```

## Understanding & Intuition
A valid substring has matched pairs and no prefix with negative balance. The key is remembering the most recent index that cannot be part of a valid substring. A stack of unmatched opening indices lets each closing parenthesis determine the longest valid suffix ending there.

## Approach 1 — Naive / Brute Force
**Idea:** Try every start index and extend while maintaining balance; update the answer whenever balance returns to zero.
```python
class Solution:
    def longestValidParentheses(self, s: str) -> int:
        best = 0
        n = len(s)
        for i in range(n):
            bal = 0
            for j in range(i, n):
                bal += 1 if s[j] == '(' else -1
                if bal < 0:
                    break
                if bal == 0:
                    best = max(best, j - i + 1)
        return best
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Dynamic programming stores the length of the valid substring ending at each closing parenthesis.
```python
class Solution:
    def longestValidParentheses(self, s: str) -> int:
        n = len(s)
        dp = [0] * n
        best = 0
        for i in range(1, n):
            if s[i] == ')':
                if s[i - 1] == '(':
                    dp[i] = 2 + (dp[i - 2] if i >= 2 else 0)
                else:
                    j = i - dp[i - 1] - 1
                    if j >= 0 and s[j] == '(':
                        dp[i] = dp[i - 1] + 2 + (dp[j - 1] if j >= 1 else 0)
                best = max(best, dp[i])
        return best
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use a stack initialized with a sentinel invalid index; the distance from the top sentinel/open index to the current close is the valid suffix length.
```python
class Solution:
    def longestValidParentheses(self, s: str) -> int:
        stack = [-1]
        best = 0
        for i, ch in enumerate(s):
            if ch == '(':
                stack.append(i)
            else:
                stack.pop()
                if not stack:
                    stack.append(i)
                else:
                    best = max(best, i - stack[-1])
        return best
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- A closing parenthesis with no matching open resets the base index.
- The answer is length, not the substring itself.
- Adjacent valid blocks can merge into a longer valid substring.

## Related
- Valid Parentheses
- Score of Parentheses
- Minimum Remove to Make Valid Parentheses
