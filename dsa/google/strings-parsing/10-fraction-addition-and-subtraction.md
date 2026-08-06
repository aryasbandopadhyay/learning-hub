# 10. Fraction Addition and Subtraction

- **Difficulty:** Medium
- **Pattern:** String Manipulation & Parsing
- **Asked at:** Google

## Problem
Given an expression string of signed fractions added and subtracted, return the result as an irreducible fraction string `"numerator/denominator"`. Denominators are positive and there are no spaces.

## Examples
```text
Input: expression = "-1/2+1/2+1/3"
Output: "1/3"
Explanation: The first two fractions cancel and one third remains.
```

## Understanding & Intuition
The expression is a stream of signed numerator-denominator pairs. Accumulating fractions requires a common denominator and gcd reduction.

## Approach 1 — Naive / Brute Force
**Idea:** Parse all fractions, multiply all denominators into one common denominator, then reduce.
```python
class Solution:
    def fractionAddition(self, expression: str) -> str:
        import math
        fr=[]; i=0
        while i<len(expression):
            sign=1
            if expression[i] in '+-': sign=-1 if expression[i]=='-' else 1; i+=1
            a=0
            while expression[i].isdigit(): a=a*10+int(expression[i]); i+=1
            i+=1; b=0
            while i<len(expression) and expression[i].isdigit(): b=b*10+int(expression[i]); i+=1
            fr.append((sign*a,b))
        den=1
        for _,b in fr: den*=b
        num=sum(a*(den//b) for a,b in fr); g=math.gcd(abs(num),den)
        return f"{num//g}/{den//g}"
```
- **Time:** O(n + m) — **Space:** O(m)

## Approach 2 — Better
**Idea:** Add fractions one by one using least common denominator and reduce each step.
```python
class Solution:
    def fractionAddition(self, expression: str) -> str:
        import math
        num,den,i=0,1,0
        while i<len(expression):
            sign=1
            if expression[i] in '+-': sign=-1 if expression[i]=='-' else 1; i+=1
            a=0
            while expression[i].isdigit(): a=a*10+int(expression[i]); i+=1
            i+=1; b=0
            while i<len(expression) and expression[i].isdigit(): b=b*10+int(expression[i]); i+=1
            a*=sign; l=den*b//math.gcd(den,b)
            num=num*(l//den)+a*(l//b); den=l; g=math.gcd(abs(num),den); num//=g; den//=g
        return f"{num}/{den}"
```
- **Time:** O(n) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Add a sentinel sign, slice each fraction, and normalize the running pair directly.
```python
class Solution:
    def fractionAddition(self, expression: str) -> str:
        import math
        s=expression if expression[0] in '+-' else '+'+expression
        num,den,i=0,1,0
        while i<len(s):
            sign=-1 if s[i]=='-' else 1; i+=1; j=i
            while s[j]!='/': j+=1
            a=sign*int(s[i:j]); i=j+1; j=i
            while j<len(s) and s[j] not in '+-': j+=1
            b=int(s[i:j]); num=num*b+a*den; den*=b
            g=math.gcd(abs(num),den); num//=g; den//=g; i=j
        return f"{num}/{den}"
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n + m) | O(m) |
| Better | O(n) | O(1) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- The first fraction may omit `+`.
- Reduce zero to `0/1`.
- Keep the denominator positive.

## Related
- Basic Calculator II
- Valid Number
