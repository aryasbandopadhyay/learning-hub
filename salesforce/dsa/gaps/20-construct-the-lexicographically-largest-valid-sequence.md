# 20. Construct the Lexicographically Largest Valid Sequence

- **Difficulty:** Medium/Hard
- **Pattern:** Backtracking
- **Asked at:** Salesforce, Amazon, Google

## Problem
Construct the lexicographically largest sequence of length `2n-1` where `1` appears once and each `2..n` appears twice exactly that number of indices apart.

## Examples
```text
Input: n = 3
Output: [3,1,2,3,2]
Explanation: The two 3s are distance 3 apart and the two 2s are distance 2 apart.
```

## Understanding & Intuition
Try larger numbers first at the earliest empty position. The first valid complete sequence is therefore lexicographically largest.

## Approach 1 — Naive / Brute Force
**Idea:** Generate placements left to right and validate distances at the end.
```python
class Solution:
    def constructDistancedSequence(self, n: int) -> list[int]:
        seq = [0] * (2 * n - 1); rem = {1: 1, **{x: 2 for x in range(2, n + 1)}}
        def valid() -> bool:
            pos = {}
            for i, x in enumerate(seq):
                if x > 1:
                    if x in pos and i - pos[x] != x: return False
                    pos.setdefault(x, i)
            return True
        def bt(i: int) -> bool:
            if i == len(seq): return valid()
            for x in range(n, 0, -1):
                if rem[x]:
                    seq[i] = x; rem[x] -= 1
                    if bt(i + 1): return True
                    rem[x] += 1; seq[i] = 0
            return False
        bt(0); return seq
```
- **Time:** O(n^(2n)) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Place both copies of each number together when possible.
```python
class Solution:
    def constructDistancedSequence(self, n: int) -> list[int]:
        seq = [0] * (2 * n - 1); used = [False] * (n + 1)
        def bt(i: int) -> bool:
            if i == len(seq): return True
            if seq[i]: return bt(i + 1)
            for x in range(n, 0, -1):
                if used[x]: continue
                if x == 1:
                    seq[i] = 1; used[x] = True
                    if bt(i + 1): return True
                    used[x] = False; seq[i] = 0
                elif i + x < len(seq) and seq[i + x] == 0:
                    seq[i] = seq[i + x] = x; used[x] = True
                    if bt(i + 1): return True
                    used[x] = False; seq[i] = seq[i + x] = 0
            return False
        bt(0); return seq
```
- **Time:** O(n!) exponential — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Skip filled positions before branching and stop at the first valid descending search result.
```python
class Solution:
    def constructDistancedSequence(self, n: int) -> list[int]:
        seq = [0] * (2 * n - 1); used = [False] * (n + 1)
        def bt(i: int) -> bool:
            while i < len(seq) and seq[i]: i += 1
            if i == len(seq): return True
            for x in range(n, 0, -1):
                if used[x]: continue
                j = i if x == 1 else i + x
                if j < len(seq) and seq[j] == 0:
                    seq[i] = seq[j] = x; used[x] = True
                    if bt(i + 1): return True
                    used[x] = False; seq[i] = seq[j] = 0
            return False
        bt(0); return seq
```
- **Time:** O(n!) exponential — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^(2n)) | O(n) |
| Better | O(n!) exponential | O(n) |
| Optimal | O(n!) exponential | O(n) |

## Edge Cases & Pitfalls
- `1` appears once; other values appear twice.
- Descending trial order gives lexicographic maximum.
- Check `i + x` is in bounds and empty.

## Related
- N-Queens
- Beautiful Arrangement
