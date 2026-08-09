# 13. Score of Parentheses

- **Difficulty:** Medium
- **Pattern:** String Manipulation & Parsing
- **Asked at:** Google

## Problem
Given a balanced parentheses string `s`, return its score. `()` scores 1, concatenation adds scores, and wrapping doubles the inner score.

Implement `Solution.scoreOfParentheses` with the parameters below and return the requested value.

**Input**
- `s`: a `str`; the input string described above.

**Output**
- A `int` value representing the result described above.

## Constraints
- 2 <= s.length <= 50
- `s` is a balanced parentheses string
- `s` contains only `(` and `)`

## Examples
```text
Input: s = "(()(()))"
Output: 6
Explanation: The score is 2 * (1 + 2).
```

## Understanding & Intuition
Balanced parentheses form nested expressions. A primitive `()` contributes one unit at its depth, while larger groups are sums or doubled inner scores.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively split a balanced substring into primitive balanced pieces and score each piece.
```python
class Solution:
    def scoreOfParentheses(self, s: str) -> int:
        def score(l,r):
            total=bal=0; start=l
            for i in range(l,r):
                bal += 1 if s[i]=='(' else -1
                if bal==0:
                    total += 1 if i==start+1 else 2*score(start+1,i)
                    start=i+1
            return total
        return score(0,len(s))
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Keep a stack of partial scores, one for each open parenthesis depth.
```python
class Solution:
    def scoreOfParentheses(self, s: str) -> int:
        st=[0]
        for c in s:
            if c=='(': st.append(0)
            else:
                v=st.pop(); st[-1]+=max(2*v,1)
        return st[0]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Each primitive `()` contributes `2^depth`, where depth is the number of surrounding pairs.
```python
class Solution:
    def scoreOfParentheses(self, s: str) -> int:
        depth=ans=0
        for i,c in enumerate(s):
            if c=='(': depth+=1
            else:
                depth-=1
                if s[i-1]=='(': ans += 1 << depth
        return ans
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Only primitive pairs add direct points.
- Concatenated groups add.
- Decrement depth before scoring a primitive closing parenthesis.

## Related
- Valid Parentheses
- Longest Valid Parentheses
