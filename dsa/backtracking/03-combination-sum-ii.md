# 03. Combination Sum II

- **Difficulty:** Medium
- **Pattern:** Backtracking
- **Asked at:** Amazon, Adobe, Microsoft, Meta

## Problem
Given integers `candidates` that may contain duplicates and an integer `target`, return all unique combinations where each number may be used at most once and the sum is `target`. `1 <= len(candidates) <= 100`, `1 <= target <= 30`.

## Examples
```text
Input: candidates = [10,1,2,7,6,1,5], target = 8
Output: [[1,1,6],[1,2,5],[1,7],[2,6]]
Explanation: Duplicate candidate values must not create duplicate combinations.
```

## Understanding & Intuition
Unlike Combination Sum, each array element can be used once. Sorting groups duplicates, which lets us skip equal values at the same recursion depth. The remaining sum prunes impossible branches.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate every subset by index and deduplicate valid sorted combinations in a set.
```python
from typing import List

class Solution:
    def combinationSum2(self, candidates: List[int], target: int) -> List[List[int]]:
        found = set()
        path = []

        def dfs(i: int, total: int) -> None:
            if i == len(candidates):
                if total == target:
                    found.add(tuple(sorted(path)))
                return
            dfs(i + 1, total)
            path.append(candidates[i])
            dfs(i + 1, total + candidates[i])
            path.pop()

        dfs(0, 0)
        return [list(combo) for combo in sorted(found)]
```
- **Time:** O(n * 2^n) — **Space:** O(n * 2^n)

## Approach 2 — Better
**Idea:** Sort and skip duplicate values at each level, but still continue scanning after values that are too large.
```python
from typing import List

class Solution:
    def combinationSum2(self, candidates: List[int], target: int) -> List[List[int]]:
        candidates.sort()
        result = []
        path = []

        def dfs(start: int, remaining: int) -> None:
            if remaining == 0:
                result.append(path.copy())
                return
            if remaining < 0:
                return
            prev = None
            for i in range(start, len(candidates)):
                if candidates[i] == prev:
                    continue
                prev = candidates[i]
                path.append(candidates[i])
                dfs(i + 1, remaining - candidates[i])
                path.pop()

        dfs(0, target)
        return result
```
- **Time:** O(n * 2^n) — **Space:** O(n) auxiliary plus output

## Approach 3 — Optimal
**Idea:** Sort, skip duplicates at the same depth, and break as soon as a candidate exceeds the remaining sum.
```python
from typing import List

class Solution:
    def combinationSum2(self, candidates: List[int], target: int) -> List[List[int]]:
        candidates.sort()
        result = []
        path = []

        def backtrack(start: int, remaining: int) -> None:
            if remaining == 0:
                result.append(path.copy())
                return
            for i in range(start, len(candidates)):
                if i > start and candidates[i] == candidates[i - 1]:
                    continue
                value = candidates[i]
                if value > remaining:
                    break
                path.append(value)
                backtrack(i + 1, remaining - value)
                path.pop()

        backtrack(0, target)
        return result
```
- **Time:** O(n * 2^n) worst case — **Space:** O(n) auxiliary plus output

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * 2^n) | O(n * 2^n) |
| Better | O(n * 2^n) | O(n) plus output |
| Optimal | O(n * 2^n) | O(n) plus output |

## Edge Cases & Pitfalls
- Skip duplicates only within the same loop depth.
- Use `i + 1` because each element can be used once.
- Break only after sorting and because values are positive.

## Related
- Combination Sum
- Subsets II
- Combinations
