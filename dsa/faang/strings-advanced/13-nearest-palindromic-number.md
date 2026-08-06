# 13. Nearest Palindromic Number

- **Difficulty:** Hard
- **Pattern:** advanced strings
- **Asked at:** Google, Meta, Amazon

## Problem
Given a string `n` representing a positive integer, return the closest integer palindrome as a string, excluding `n` itself. If two palindromes are equally close, return the smaller one.

Constraints: `1 <= len(n) <= 18`; `n` has no leading zeroes.

## Examples
```text
Input: n = "123"
Output: "121"
Explanation: 121 and 131 are nearby palindromes, and 121 is closer.
```

## Understanding & Intuition
The closest palindrome almost always comes from mirroring the left half. Prefix minus one and plus one handle carries and borrows. Extra boundary candidates handle powers of ten and all-9s cases.

## Approach 1 — Naive / Brute Force
**Idea:** Search outward for small numbers, otherwise use the complete candidate set.
```python
class Solution:
    def nearestPalindromic(self, n: str) -> str:
        value = int(n)
        if value <= 100000:
            delta = 1
            while True:
                low, high = value - delta, value + delta
                if low >= 0 and str(low) == str(low)[::-1]:
                    return str(low)
                if str(high) == str(high)[::-1]:
                    return str(high)
                delta += 1
        length = len(n)
        prefix = int(n[:(length + 1) // 2])
        candidates = {10 ** (length - 1) - 1, 10 ** length + 1}
        for p in (prefix - 1, prefix, prefix + 1):
            left = str(p)
            candidates.add(int(left + (left[-2::-1] if length % 2 else left[::-1])))
        candidates.discard(value)
        return str(min(candidates, key=lambda x: (abs(x - value), x)))
```
- **Time:** O(sqrt(N)) small, O(d) fallback — **Space:** O(d)

## Approach 2 — Better
**Idea:** Generate prefix-adjusted palindromes and choose by `(distance, value)`.
```python
class Solution:
    def nearestPalindromic(self, n):
        value = int(n)
        length = len(n)
        half = (length + 1) // 2
        prefix = int(n[:half])
        candidates = [10 ** (length - 1) - 1, 10 ** length + 1]
        for p in [prefix - 1, prefix, prefix + 1]:
            left = str(p)
            pal = left + (left[::-1] if length % 2 == 0 else left[-2::-1])
            candidates.append(int(pal))
        answer = None
        for candidate in candidates:
            if candidate == value:
                continue
            if answer is None or (abs(candidate - value), candidate) < (abs(answer - value), answer):
                answer = candidate
        return str(answer)
```
- **Time:** O(d) — **Space:** O(d)

## Approach 3 — Optimal
**Idea:** Deduplicate candidates in a set and select the best by distance, then value.
```python
class Solution:
    def nearestPalindromic(self, n):
        num = int(n)
        length = len(n)
        prefix = int(n[:(length + 1) // 2])
        candidates = {10 ** (length - 1) - 1, 10 ** length + 1}
        for p in range(prefix - 1, prefix + 2):
            left = str(p)
            mirrored = left + (left[-2::-1] if length % 2 else left[::-1])
            candidates.add(int(mirrored))
        candidates.discard(num)
        return str(min(candidates, key=lambda x: (abs(x - num), x)))
```
- **Time:** O(d) — **Space:** O(d)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(sqrt(N)) small, O(d) fallback | O(d) |
| Better | O(d) | O(d) |
| Optimal | O(d) | O(d) |

## Edge Cases & Pitfalls
- Exclude the original number.
- Powers of ten need the all-9s candidate.
- All-9s numbers need the `100...001` candidate.

## Related
- Palindrome construction
- Numeric strings
