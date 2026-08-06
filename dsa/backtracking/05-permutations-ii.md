# 05. Permutations II

- **Difficulty:** Medium
- **Pattern:** Backtracking
- **Asked at:** Amazon, Google, Microsoft, Meta

## Problem
Given a collection of numbers `nums` that may contain duplicates, return all unique permutations. `1 <= len(nums) <= 8`. The answer may be returned in any order.

## Examples
```text
Input: nums = [1,1,2]
Output: [[1,1,2],[1,2,1],[2,1,1]]
Explanation: Duplicate 1s should not produce duplicate permutations.
```

## Understanding & Intuition
Duplicate values make plain permutation generation repeat identical arrangements. Sorting groups equal values so the recursion can skip a duplicate unless the previous equal value has already been used in this position chain. Counting values is another clean way to choose each value only as many times as it appears.

## Approach 1 — Naive / Brute Force
**Idea:** Generate permutations by index and store completed value tuples in a set.
```python
from typing import List

class Solution:
    def permuteUnique(self, nums: List[int]) -> List[List[int]]:
        found = set()
        path = []
        used = [False] * len(nums)

        def dfs() -> None:
            if len(path) == len(nums):
                found.add(tuple(path))
                return
            for i, value in enumerate(nums):
                if used[i]:
                    continue
                used[i] = True
                path.append(value)
                dfs()
                path.pop()
                used[i] = False

        dfs()
        return [list(p) for p in sorted(found)]
```
- **Time:** O(n * n!) — **Space:** O(n * n!)

## Approach 2 — Better
**Idea:** Sort and skip an equal value if its previous copy has not been used in the current branch.
```python
from typing import List

class Solution:
    def permuteUnique(self, nums: List[int]) -> List[List[int]]:
        nums.sort()
        result = []
        path = []
        used = [False] * len(nums)

        def backtrack() -> None:
            if len(path) == len(nums):
                result.append(path.copy())
                return
            for i, value in enumerate(nums):
                if used[i]:
                    continue
                if i > 0 and nums[i] == nums[i - 1] and not used[i - 1]:
                    continue
                used[i] = True
                path.append(value)
                backtrack()
                path.pop()
                used[i] = False

        backtrack()
        return result
```
- **Time:** O(n * U) — **Space:** O(n) auxiliary plus output, where `U` is the number of unique permutations

## Approach 3 — Optimal
**Idea:** Count each distinct value and recursively consume counts, avoiding duplicate index states entirely.
```python
from collections import Counter
from typing import List

class Solution:
    def permuteUnique(self, nums: List[int]) -> List[List[int]]:
        counts = Counter(nums)
        result = []
        path = []

        def backtrack() -> None:
            if len(path) == len(nums):
                result.append(path.copy())
                return
            for value in list(counts):
                if counts[value] == 0:
                    continue
                counts[value] -= 1
                path.append(value)
                backtrack()
                path.pop()
                counts[value] += 1

        backtrack()
        return result
```
- **Time:** O(n * U) — **Space:** O(k + n) auxiliary plus output, where `k` is distinct values

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * n!) | O(n * n!) |
| Better | O(n * U) | O(n) plus output |
| Optimal | O(n * U) | O(k + n) plus output |

## Edge Cases & Pitfalls
- The duplicate-skip condition depends on sorted input.
- Do not skip every equal value; only skip equal unused predecessors.
- Counter keys should not change while iterating.

## Related
- Permutations
- Subsets II
- Letter Combinations of a Phone Number
