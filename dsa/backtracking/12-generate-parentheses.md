# 12. Generate Parentheses

- **Difficulty:** Medium
- **Pattern:** Backtracking
- **Asked at:** Google, Meta, Amazon, Microsoft

## Problem
Given `n` pairs of parentheses, generate all combinations of well-formed parentheses. `1 <= n <= 8`. The answer may be returned in any order.

## Examples
```text
Input: n = 3
Output: ["((()))","(()())","(())()","()(())","()()()"]
Explanation: These are all valid strings using three opening and three closing parentheses.
```

## Understanding & Intuition
A valid prefix never has more closing than opening parentheses. We can build strings left to right while tracking how many opens and closes were used. Pruning invalid prefixes avoids generating most impossible strings.

## Approach 1 — Naive / Brute Force
**Idea:** Generate every binary string of length `2n`, treating `0` as `(` and `1` as `)`, then validate.
```python
from typing import List

class Solution:
    def generateParenthesis(self, n: int) -> List[str]:
        result = []

        def valid(s: str) -> bool:
            balance = 0
            for ch in s:
                balance += 1 if ch == "(" else -1
                if balance < 0:
                    return False
            return balance == 0

        def dfs(s: str) -> None:
            if len(s) == 2 * n:
                if valid(s):
                    result.append(s)
                return
            dfs(s + "(")
            dfs(s + ")")

        dfs("")
        return result
```
- **Time:** O(n * 4^n) — **Space:** O(n) auxiliary plus output

## Approach 2 — Better
**Idea:** Backtrack only when a prefix can still become valid: add `(` if available and `)` if it will not exceed opens.
```python
from typing import List

class Solution:
    def generateParenthesis(self, n: int) -> List[str]:
        result = []
        path = []

        def backtrack(opened: int, closed: int) -> None:
            if len(path) == 2 * n:
                result.append("".join(path))
                return
            if opened < n:
                path.append("(")
                backtrack(opened + 1, closed)
                path.pop()
            if closed < opened:
                path.append(")")
                backtrack(opened, closed + 1)
                path.pop()

        backtrack(0, 0)
        return result
```
- **Time:** O(n * Catalan(n)) — **Space:** O(n) auxiliary plus output

## Approach 3 — Optimal
**Idea:** Track remaining left and right parentheses; only place `)` when more right parentheses remain than left.
```python
from typing import List

class Solution:
    def generateParenthesis(self, n: int) -> List[str]:
        result = []
        chars = [""] * (2 * n)

        def dfs(pos: int, left: int, right: int) -> None:
            if pos == 2 * n:
                result.append("".join(chars))
                return
            if left > 0:
                chars[pos] = "("
                dfs(pos + 1, left - 1, right)
            if right > left:
                chars[pos] = ")"
                dfs(pos + 1, left, right - 1)

        dfs(0, n, n)
        return result
```
- **Time:** O(n * Catalan(n)) — **Space:** O(n) auxiliary plus output

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * 4^n) | O(n) plus output |
| Better | O(n * Catalan(n)) | O(n) plus output |
| Optimal | O(n * Catalan(n)) | O(n) plus output |

## Edge Cases & Pitfalls
- Never place `)` if it would make the prefix invalid.
- Exactly `n` opens and `n` closes must be used.
- Joining a mutable path is cheaper than repeated string concatenation.

## Related
- Palindrome Partitioning
- Letter Combinations of a Phone Number
- Combinations
