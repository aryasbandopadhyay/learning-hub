# 21. Count Sorted Vowel Strings

- **Difficulty:** Medium
- **Pattern:** Dynamic Programming / Combinatorics
- **Asked at:** Salesforce, Amazon, Google

## Problem
Return the number of length-`n` strings made of vowels that are sorted in nondecreasing lexicographic order.

## Examples
```text
Input: n = 2
Output: 15
Explanation: There are 15 nondecreasing vowel pairs.
```

## Understanding & Intuition
A sorted vowel string is determined by counts of `a,e,i,o,u` summing to `n`, which is combinations with repetition.

## Approach 1 — Naive / Brute Force
**Idea:** Generate all vowel strings and count sorted ones.
```python
class Solution:
    def countVowelStrings(self, n: int) -> int:
        vowels = "aeiou"; ans = 0
        def dfs(s: str) -> None:
            nonlocal ans
            if len(s) == n:
                if list(s) == sorted(s): ans += 1
                return
            for ch in vowels: dfs(s + ch)
        dfs(""); return ans
```
- **Time:** O(5^n * n log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Backtrack only nondecreasing choices.
```python
class Solution:
    def countVowelStrings(self, n: int) -> int:
        ans = 0
        def dfs(length: int, start: int) -> None:
            nonlocal ans
            if length == n: ans += 1; return
            for i in range(start, 5): dfs(length + 1, i)
        dfs(0, 0); return ans
```
- **Time:** O(C(n+4,4)) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Apply stars and bars: choose 4 separators among `n+4` positions.
```python
class Solution:
    def countVowelStrings(self, n: int) -> int:
        return (n + 4) * (n + 3) * (n + 2) * (n + 1) // 24
```
- **Time:** O(1) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(5^n * n log n) | O(n) |
| Better | O(C(n+4,4)) | O(n) |
| Optimal | O(1) | O(1) |

## Edge Cases & Pitfalls
- Repeated vowels are allowed.
- Formula is `C(n+4,4)`.
- Use integer arithmetic.

## Related
- Unique Paths
- Climbing Stairs
