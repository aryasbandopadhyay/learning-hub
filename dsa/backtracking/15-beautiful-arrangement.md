# 15. Beautiful Arrangement

- **Difficulty:** Medium
- **Pattern:** Backtracking
- **Asked at:** Google, Amazon, Microsoft, Meta

## Problem
Given an integer `n`, count the number of beautiful arrangements of numbers `1` through `n`. An arrangement is beautiful if for every position `i` (1-indexed), either `perm[i]` is divisible by `i` or `i` is divisible by `perm[i]`. `1 <= n <= 15`.

## Examples
```text
Input: n = 2
Output: 2
Explanation: [1,2] and [2,1] both satisfy the divisibility condition.
```

## Understanding & Intuition
This is a constrained permutation count. At each position, only some unused numbers are legal. Precomputing legal numbers and memoizing bitmask states avoids repeating identical suffix searches.

## Approach 1 — Naive / Brute Force
**Idea:** Generate every permutation and count those that satisfy the divisibility rule.
```python
from typing import List

class Solution:
    def countArrangement(self, n: int) -> int:
        count = 0
        nums = list(range(1, n + 1))

        def valid() -> bool:
            for i, value in enumerate(nums, 1):
                if value % i != 0 and i % value != 0:
                    return False
            return True

        def permute(first: int) -> None:
            nonlocal count
            if first == n:
                if valid():
                    count += 1
                return
            for i in range(first, n):
                nums[first], nums[i] = nums[i], nums[first]
                permute(first + 1)
                nums[first], nums[i] = nums[i], nums[first]

        permute(0)
        return count
```
- **Time:** O(n * n!) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Build the permutation by position and only try unused numbers that already satisfy the current position.
```python
class Solution:
    def countArrangement(self, n: int) -> int:
        count = 0
        used = [False] * (n + 1)

        def backtrack(pos: int) -> None:
            nonlocal count
            if pos == n + 1:
                count += 1
                return
            for value in range(1, n + 1):
                if used[value]:
                    continue
                if value % pos == 0 or pos % value == 0:
                    used[value] = True
                    backtrack(pos + 1)
                    used[value] = False

        backtrack(1)
        return count
```
- **Time:** O(n!) worst case — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Precompute legal values for each position and memoize `(position, used_mask)` states.
```python
from functools import lru_cache

class Solution:
    def countArrangement(self, n: int) -> int:
        choices = {
            pos: [value for value in range(1, n + 1)
                  if value % pos == 0 or pos % value == 0]
            for pos in range(1, n + 1)
        }

        @lru_cache(None)
        def dp(pos: int, used_mask: int) -> int:
            if pos == n + 1:
                return 1
            total = 0
            for value in choices[pos]:
                bit = 1 << (value - 1)
                if not (used_mask & bit):
                    total += dp(pos + 1, used_mask | bit)
            return total

        return dp(1, 0)
```
- **Time:** O(n * 2^n) — **Space:** O(2^n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * n!) | O(n) |
| Better | O(n!) | O(n) |
| Optimal | O(n * 2^n) | O(2^n) |

## Edge Cases & Pitfalls
- Positions are 1-indexed.
- Count arrangements; do not return the actual permutations.
- Memoization state must include which numbers are already used.

## Related
- Permutations
- N-Queens
- Combinations
