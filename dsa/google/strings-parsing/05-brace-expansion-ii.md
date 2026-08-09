# 05. Brace Expansion II

- **Difficulty:** Hard
- **Pattern:** String Manipulation & Parsing
- **Asked at:** Google

## Problem
Given an expression containing lowercase words, braces, commas, and concatenation, return all expanded strings in lexicographic order with duplicates removed. Comma means union and adjacent terms mean concatenation.

Implement `Solution.braceExpansionII` with the parameters below and return the requested value.

**Input**
- `expression`: a `str`; the expression string described above.

**Output**
- All generated strings with duplicates removed, sorted in lexicographic order.

## Constraints
- 1 <= expression.length <= 60
- `expression` contains lowercase English letters, braces, and commas
- The expression is valid and every generated word contains only lowercase English letters

## Examples
```text
Input: expression = "{a,b}{c,{d,e}}"
Output: ["ac","ad","ae","bc","bd","be"]
Explanation: Each option from the first brace concatenates with each option from the second part. The result is shown in the required order.
```

## Understanding & Intuition
This grammar has union at comma level and implicit concatenation between factors. Splitting must respect brace depth. Sets handle duplicate expansions.

## Approach 1 — Naive / Brute Force
**Idea:** Expand the first brace pair recursively and combine its options with prefix and suffix.
```python
class Solution:
    def braceExpansionII(self, expression: str) -> list[str]:
        def split_top(s):
            out=[]; start=depth=0
            for i,c in enumerate(s):
                if c=='{': depth+=1
                elif c=='}': depth-=1
                elif c==',' and depth==0: out.append(s[start:i]); start=i+1
            return out+[s[start:]]
        def expand(e):
            l=e.find('{')
            if l<0: return {e}
            depth=0
            for r in range(l,len(e)):
                depth += e[r]=='{'; depth -= e[r]=='}'
                if depth==0: break
            ans=set()
            for opt in split_top(e[l+1:r]):
                for mid in expand(opt):
                    for tail in expand(e[r+1:]): ans.add(e[:l]+mid+tail)
            return ans
        return sorted(expand(expression))
```
- **Time:** O(r log r + rL) — **Space:** O(rL)

## Approach 2 — Better
**Idea:** Parse expression, term, and factor functions directly.
```python
class Solution:
    def braceExpansionII(self, expression: str) -> list[str]:
        i=0; n=len(expression)
        def prod(a,b): return {x+y for x in a for y in b}
        def expr():
            nonlocal i
            res=term()
            while i<n and expression[i]==',': i+=1; res |= term()
            return res
        def term():
            nonlocal i
            res={''}
            while i<n and expression[i] not in '},': res=prod(res,factor())
            return res
        def factor():
            nonlocal i
            if expression[i]=='{':
                i+=1; res=expr(); i+=1; return res
            j=i
            while i<n and expression[i].islower(): i+=1
            return {expression[j:i]}
        return sorted(expr())
```
- **Time:** O(r log r + rL) — **Space:** O(rL)

## Approach 3 — Optimal
**Idea:** Add memoization to substring expansion so repeated subexpressions are computed once.
```python
class Solution:
    def braceExpansionII(self, expression: str) -> list[str]:
        from functools import lru_cache
        def split_top(s):
            out=[]; start=depth=0
            for i,c in enumerate(s):
                depth += c=='{'; depth -= c=='}'
                if c==',' and depth==0: out.append(s[start:i]); start=i+1
            return out+[s[start:]]
        @lru_cache(None)
        def exp(s):
            parts=split_top(s)
            if len(parts)>1:
                ans=set()
                for p in parts: ans |= set(exp(p))
                return frozenset(ans)
            res={''}; i=0
            while i<len(s):
                if s[i]=='{':
                    d=0
                    for j in range(i,len(s)):
                        d += s[j]=='{'; d -= s[j]=='}'
                        if d==0: break
                    choices=exp(s[i+1:j]); i=j+1
                else:
                    j=i
                    while j<len(s) and s[j].islower(): j+=1
                    choices=frozenset({s[i:j]}); i=j
                res={a+b for a in res for b in choices}
            return frozenset(res)
        return sorted(exp(expression))
```
- **Time:** O(r log r + rL) — **Space:** O(rL)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(r log r + rL) | O(rL) |
| Better | O(r log r + rL) | O(rL) |
| Optimal | O(r log r + rL) | O(rL) |

## Edge Cases & Pitfalls
- Split commas only at depth zero.
- Concatenation binds tighter than union.
- Return sorted unique strings.

## Related
- Generate Parentheses
- Number of Atoms
