# 02. Combination Sum

- **Difficulty:** Medium
- **Pattern:** Backtracking
- **Asked at:** Amazon, Google, Meta, Bloomberg

## Problem
Given distinct positive integers `candidates` and a target, return all unique combinations whose sum
is exactly `target`. You may use each candidate any number of times. Within a combination, numbers
should appear in non-decreasing order.

**Input**
- `candidates`: a list of distinct positive integers.
- `target`: the desired sum.

**Output**
- A list of combinations. **This judge compares exactly**, so return combinations in lexicographic
  order after considering candidates in ascending order; each individual combination must be
  non-decreasing.

## Constraints
- 1 <= candidates.length <= 30
- 2 <= candidates[i] <= 40
- All values in `candidates` are unique.
- 1 <= target <= 40

## Examples
```text
Input: candidates = [2,3,6,7], target = 7
Output: [[2,2,3],[7]]
Explanation: `2 + 2 + 3` and `7` are the only non-decreasing combinations that sum to `7`, so they are returned in that order.
```

## Understanding & Intuition
The search chooses numbers while tracking the remaining sum. To avoid duplicate permutations of the same combination, choices are made from a nondecreasing start index. Sorting lets us stop once a candidate exceeds the remaining sum.

## Approach 1 — Naive / Brute Force
**Idea:** Generate all ordered sequences up to `target // min(candidates)` length, then canonicalize matching sums with a set.
```python
from typing import List

class Solution:
    def combinationSum(self, candidates: List[int], target: int) -> List[List[int]]:
        found = set()
        path = []
        max_len = target // min(candidates)

        def dfs(total: int) -> None:
            if total == target:
                found.add(tuple(sorted(path)))
                return
            if total > target or len(path) == max_len:
                return
            for value in candidates:
                path.append(value)
                dfs(total + value)
                path.pop()

        dfs(0)
        return [list(combo) for combo in sorted(found)]
```
- **Time:** O(m^L * L log L) — **Space:** O(m^L * L), where `m = len(candidates)` and `L = target / min(candidates)`

## Approach 2 — Better
**Idea:** Backtrack by index so combinations are naturally nondecreasing; reuse an item by recursing with the same index.
```python
from typing import List

class Solution:
    def combinationSum(self, candidates: List[int], target: int) -> List[List[int]]:
        result = []
        path = []

        def dfs(start: int, remaining: int) -> None:
            if remaining == 0:
                result.append(path.copy())
                return
            if remaining < 0:
                return
            for i in range(start, len(candidates)):
                path.append(candidates[i])
                dfs(i, remaining - candidates[i])
                path.pop()

        dfs(0, target)
        return result
```
- **Time:** O(m^L * L) — **Space:** O(L) auxiliary plus output

## Approach 3 — Optimal
**Idea:** Sort candidates and prune the loop as soon as the current value is larger than the remaining sum.
```python
from typing import List

class Solution:
    def combinationSum(self, candidates: List[int], target: int) -> List[List[int]]:
        candidates.sort()
        result = []
        path = []

        def backtrack(start: int, remaining: int) -> None:
            if remaining == 0:
                result.append(path.copy())
                return
            for i in range(start, len(candidates)):
                value = candidates[i]
                if value > remaining:
                    break
                path.append(value)
                backtrack(i, remaining - value)
                path.pop()

        backtrack(0, target)
        return result
```
- **Time:** O(m^L * L) worst case — **Space:** O(L) auxiliary plus output

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(m^L * L log L) | O(m^L * L) |
| Better | O(m^L * L) | O(L) plus output |
| Optimal | O(m^L * L) | O(L) plus output |

## Edge Cases & Pitfalls
- Reuse is allowed, so recurse with `i`, not `i + 1`.
- Avoid duplicate orderings such as `[2,3,2]`.
- Sorting enables safe pruning because candidates are positive.

## Related
- Combination Sum II
- Combinations
- Subsets
