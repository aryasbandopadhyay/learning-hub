# 11. Combinations

- **Difficulty:** Medium
- **Pattern:** Backtracking
- **Asked at:** Amazon, Google, Microsoft, Adobe

## Problem
Given two integers `n` and `k`, return all possible combinations of `k` numbers chosen from the range `1` to `n`. `1 <= k <= n <= 20`. The answer may be returned in any order.

## Examples
```text
Input: n = 4, k = 2
Output: [[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]
Explanation: These are all pairs chosen from 1 through 4.
```

## Understanding & Intuition
Combinations are ordered by increasing values to avoid duplicates. Backtracking chooses the next number from numbers greater than the last chosen one. Pruning can stop loops that cannot fill the remaining slots.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate every subset of `1..n` using a bitmask and keep masks with exactly `k` bits.
```python
from typing import List

class Solution:
    def combine(self, n: int, k: int) -> List[List[int]]:
        result = []
        for mask in range(1 << n):
            if mask.bit_count() != k:
                continue
            combo = []
            for i in range(n):
                if mask & (1 << i):
                    combo.append(i + 1)
            result.append(combo)
        return result
```
- **Time:** O(n * 2^n) — **Space:** O(k * C(n,k))

## Approach 2 — Better
**Idea:** Standard backtracking chooses increasing numbers until the path length is `k`.
```python
from typing import List

class Solution:
    def combine(self, n: int, k: int) -> List[List[int]]:
        result = []
        path = []

        def backtrack(start: int) -> None:
            if len(path) == k:
                result.append(path.copy())
                return
            for value in range(start, n + 1):
                path.append(value)
                backtrack(value + 1)
                path.pop()

        backtrack(1)
        return result
```
- **Time:** O(k * C(n,k)) — **Space:** O(k) auxiliary plus output

## Approach 3 — Optimal
**Idea:** Prune the loop upper bound so enough numbers remain to complete a size-`k` combination.
```python
from typing import List

class Solution:
    def combine(self, n: int, k: int) -> List[List[int]]:
        result = []
        path = []

        def backtrack(start: int) -> None:
            if len(path) == k:
                result.append(path.copy())
                return
            need = k - len(path)
            for value in range(start, n - need + 2):
                path.append(value)
                backtrack(value + 1)
                path.pop()

        backtrack(1)
        return result
```
- **Time:** O(k * C(n,k)) — **Space:** O(k) auxiliary plus output

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * 2^n) | O(k * C(n,k)) |
| Better | O(k * C(n,k)) | O(k) plus output |
| Optimal | O(k * C(n,k)) | O(k) plus output |

## Edge Cases & Pitfalls
- Values run from `1` through `n`, inclusive.
- Use increasing order to avoid duplicate combinations.
- Pruned loop end is inclusive through `n - need + 1`.

## Related
- Subsets
- Combination Sum
- Generate Parentheses
