# 11. Ambiguous Coordinates

- **Difficulty:** Medium
- **Pattern:** String Manipulation & Parsing
- **Asked at:** Google

## Problem
A coordinate string had commas, spaces, and decimal points removed. Given the parenthesized digit string `s`, return every possible original coordinate in lexicographic order. Numbers cannot have extra leading zeroes, and decimals cannot have trailing zeroes.

Implement `Solution.ambiguousCoordinates` with the parameters below and return the requested value.

**Input**
- `s`: a `str`; the input string described above.

**Output**
- All valid coordinate strings in lexicographic order.

## Constraints
- 4 <= s.length <= 12
- `s` starts with `(`, ends with `)`, and contains only digits inside the parentheses

## Examples
```text
Input: s = "(123)"
Output: ["(1, 2.3)","(1, 23)","(1.2, 3)","(12, 3)"]
Explanation: All valid comma splits and decimal placements are returned sorted. The result is shown in the required order.
```

## Understanding & Intuition
Choose the comma split, then choose an optional decimal point on each side. Leading and trailing zero rules fully determine validity.

## Approach 1 — Naive / Brute Force
**Idea:** Generate every decimal placement and filter invalid numeric strings.
```python
class Solution:
    def ambiguousCoordinates(self, s: str) -> list[str]:
        d=s[1:-1]
        def valid(x):
            if '.' in x:
                a,b=x.split('.')
                return a and b and (a=='0' or not a.startswith('0')) and not b.endswith('0')
            return x=='0' or not x.startswith('0')
        ans=[]
        for i in range(1,len(d)):
            L=[d[:i]]+[d[:k]+'.'+d[k:i] for k in range(1,i)]
            R=[d[i:]]+[d[i:k]+'.'+d[k:] for k in range(i+1,len(d))]
            for a in L:
                for b in R:
                    if valid(a) and valid(b): ans.append(f"({a}, {b})")
        return sorted(ans)
```
- **Time:** O(n^4 log n) — **Space:** O(n^3)

## Approach 2 — Better
**Idea:** Generate only valid forms for each side before combining.
```python
class Solution:
    def ambiguousCoordinates(self, s: str) -> list[str]:
        d=s[1:-1]
        def forms(p):
            if len(p)==1: return [p]
            ans=[]
            if not p.startswith('0'): ans.append(p)
            if p.endswith('0'): return ans
            if p.startswith('0'): ans.append('0.'+p[1:])
            else:
                for i in range(1,len(p)): ans.append(p[:i]+'.'+p[i:])
            return ans
        return sorted(f"({a}, {b})" for i in range(1,len(d)) for a in forms(d[:i]) for b in forms(d[i:]))
```
- **Time:** O(n^3 log n) — **Space:** O(n^3)

## Approach 3 — Optimal
**Idea:** Cache valid forms for each substring range.
```python
class Solution:
    def ambiguousCoordinates(self, s: str) -> list[str]:
        from functools import lru_cache
        d=s[1:-1]
        @lru_cache(None)
        def forms(l,r):
            p=d[l:r]; out=[]
            if len(p)==1: out.append(p)
            else:
                if not p.startswith('0'): out.append(p)
                if not p.endswith('0'):
                    if p.startswith('0'): out.append('0.'+p[1:])
                    else:
                        for k in range(1,len(p)): out.append(p[:k]+'.'+p[k:])
            return tuple(out)
        return sorted(f"({a}, {b})" for i in range(1,len(d)) for a in forms(0,i) for b in forms(i,len(d)))
```
- **Time:** O(n^3 log n) — **Space:** O(n^3)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^4 log n) | O(n^3) |
| Better | O(n^3 log n) | O(n^3) |
| Optimal | O(n^3 log n) | O(n^3) |

## Edge Cases & Pitfalls
- `0` is valid, but `00` is not.
- Decimal forms cannot end with zero.
- Sort results for deterministic output.

## Related
- Restore IP Addresses
- Valid Number

