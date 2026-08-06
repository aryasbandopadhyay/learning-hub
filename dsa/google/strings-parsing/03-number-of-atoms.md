# 03. Number of Atoms

- **Difficulty:** Hard
- **Pattern:** String Manipulation & Parsing
- **Asked at:** Google

## Problem
Given a chemical `formula`, return atom counts in canonical order: atom names sorted lexicographically, followed by their count only when greater than one. Formulas contain element names, parentheses, and positive multipliers.

## Examples
```text
Input: formula = "K4(ON(SO3)2)2"
Output: "K4N2O14S4"
Explanation: Multipliers from nested groups are applied to all atoms inside them.
```

## Understanding & Intuition
Parentheses create groups whose counts are multiplied after the closing parenthesis. A stack or recursive parser keeps group counts separate until the multiplier is known.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively parse each parenthesized group and merge multiplied counters upward.
```python
class Solution:
    def countOfAtoms(self, formula: str) -> str:
        from collections import Counter
        n = len(formula)
        def num(i):
            v = 0
            while i < n and formula[i].isdigit(): v = v * 10 + int(formula[i]); i += 1
            return (v or 1), i
        def parse(i):
            c = Counter()
            while i < n and formula[i] != ")":
                if formula[i] == "(":
                    inner, i = parse(i + 1); m, i = num(i + 1)
                    for a, x in inner.items(): c[a] += x * m
                else:
                    j = i + 1
                    while j < n and formula[j].islower(): j += 1
                    atom = formula[i:j]; m, i = num(j); c[atom] += m
            return c, i
        c, _ = parse(0)
        return "".join(a + (str(c[a]) if c[a] > 1 else "") for a in sorted(c))
```
- **Time:** O(n + a log a) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Push a fresh counter at `(` and merge it into the previous counter at `)`.
```python
class Solution:
    def countOfAtoms(self, formula: str) -> str:
        from collections import Counter
        st, i, n = [Counter()], 0, len(formula)
        while i < n:
            if formula[i] == "(": st.append(Counter()); i += 1
            elif formula[i] == ")":
                i += 1; m = 0
                while i < n and formula[i].isdigit(): m = m * 10 + int(formula[i]); i += 1
                top = st.pop()
                for a, x in top.items(): st[-1][a] += x * (m or 1)
            else:
                j = i + 1
                while j < n and formula[j].islower(): j += 1
                a, i = formula[i:j], j; m = 0
                while i < n and formula[i].isdigit(): m = m * 10 + int(formula[i]); i += 1
                st[-1][a] += m or 1
        c = st[-1]
        return "".join(a + (str(c[a]) if c[a] > 1 else "") for a in sorted(c))
```
- **Time:** O(n + a log a) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Scan right to left so each pending multiplier is already known when an atom is read.
```python
class Solution:
    def countOfAtoms(self, formula: str) -> str:
        from collections import Counter
        c, mult, pending, i = Counter(), [1], 1, len(formula) - 1
        while i >= 0:
            if formula[i].isdigit():
                place = 1; pending = 0
                while i >= 0 and formula[i].isdigit(): pending = int(formula[i]) * place + pending; place *= 10; i -= 1
            elif formula[i] == ")": mult.append(mult[-1] * pending); pending = 1; i -= 1
            elif formula[i] == "(": mult.pop(); pending = 1; i -= 1
            else:
                end = i + 1
                while i >= 0 and formula[i].islower(): i -= 1
                c[formula[i:end]] += pending * mult[-1]; pending = 1; i -= 1
        return "".join(a + (str(c[a]) if c[a] > 1 else "") for a in sorted(c))
```
- **Time:** O(n + a log a) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n + a log a) | O(n) |
| Better | O(n + a log a) | O(n) |
| Optimal | O(n + a log a) | O(n) |

## Edge Cases & Pitfalls
- Missing multipliers mean one.
- Atom names can have multiple lowercase letters.
- Final atoms must be sorted.

## Related
- Decode String
- Brace Expansion II


