# 12. Orderly Queue

- **Difficulty:** Hard
- **Pattern:** advanced strings
- **Asked at:** Google, Amazon

## Problem
Given a string `s` and integer `k`, one operation chooses one of the first `k` characters and moves it to the end. Return the lexicographically smallest string obtainable after any number of operations.

**Input**
- `s`: a `str`; the input string.
- `k`: a `int`; the integer parameter described above.

**Output**
- A `str`. Return the lexicographically smallest string obtainable after any number of operations.

## Constraints
- `1 <= len(s) <= 1000`.
- `1 <= k <= len(s)`.

## Examples
```text
Input: s = "cba", k = 1
Output: "acb"
Explanation: With k = 1, only rotations are reachable.
```

## Understanding & Intuition
When `k = 1`, the operation is exactly rotation. When `k > 1`, repeated operations can simulate adjacent swaps, so every permutation is reachable and sorting is optimal. The only hard case is the smallest rotation.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate rotations for `k = 1`; otherwise manually selection-sort the characters.
```python
class Solution:
    def orderlyQueue(self, s: str, k: int) -> str:
        if k == 1:
            best = s
            for i in range(1, len(s)):
                best = min(best, s[i:] + s[:i])
            return best
        chars = list(s)
        for i in range(len(chars)):
            best = i
            for j in range(i + 1, len(chars)):
                if chars[j] < chars[best]:
                    best = j
            chars[i], chars[best] = chars[best], chars[i]
        return ''.join(chars)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use Booth's algorithm for the least rotation when `k = 1`; sort when `k > 1`.
```python
class Solution:
    def orderlyQueue(self, s, k):
        if k > 1:
            return ''.join(sorted(s))
        ss = s + s
        n = len(s)
        i, j, offset = 0, 1, 0
        while i < n and j < n and offset < n:
            a, b = ss[i + offset], ss[j + offset]
            if a == b:
                offset += 1
            elif a > b:
                i = i + offset + 1
                if i <= j:
                    i = j + 1
                offset = 0
            else:
                j = j + offset + 1
                if j <= i:
                    j = i + 1
                offset = 0
        start = min(i, j)
        return ss[start:start + n]
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Apply the reachability theorem directly.
```python
class Solution:
    def orderlyQueue(self, s, k):
        if k == 1:
            return min(s[i:] + s[:i] for i in range(len(s)))
        return ''.join(sorted(s))
```
- **Time:** O(n^2) or O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n^2) or O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Do not sort when `k = 1`.
- For `k > 1`, sorting is valid because all permutations are reachable.
- Repeated characters still require lexicographic comparison.

## Related
- Minimum rotation
- Booth's algorithm
