# 06. Subsets II

- **Difficulty:** Medium
- **Pattern:** Backtracking
- **Asked at:** Amazon, Meta, Microsoft, Bloomberg

## Problem
Given an integer array `nums` that may contain duplicates, return all possible unique subsets. The
output must not contain duplicate subsets.

**Input**
- `nums`: a list of integers, possibly with repeated values.

**Output**
- A list of unique subsets. The judge accepts subsets in any order, but each subset should be in
  non-decreasing order when the input is sorted first.

## Constraints
- 1 <= nums.length <= 10
- -10 <= nums[i] <= 10

## Examples
```text
Input: nums = [1,2,2]
Output: [[],[1],[1,2],[1,2,2],[2],[2,2]]
Explanation: The two `2`s create the additional subsets `[2,2]` and `[1,2,2]`, but duplicate copies of `[2]` or `[1,2]` are omitted.
```

## Understanding & Intuition
Subsets with duplicates need a rule that chooses equal values consistently. Sorting groups equal values. At each recursion level, taking only the first duplicate candidate prevents repeated subsets.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate every index subset and use a set of sorted tuples to deduplicate.
```python
from typing import List

class Solution:
    def subsetsWithDup(self, nums: List[int]) -> List[List[int]]:
        found = set()
        path = []

        def dfs(i: int) -> None:
            if i == len(nums):
                found.add(tuple(sorted(path)))
                return
            dfs(i + 1)
            path.append(nums[i])
            dfs(i + 1)
            path.pop()

        dfs(0)
        return [list(s) for s in sorted(found)]
```
- **Time:** O(n * 2^n) — **Space:** O(n * 2^n)

## Approach 2 — Better
**Idea:** Sort and skip duplicate candidates that appear later in the same loop level.
```python
from typing import List

class Solution:
    def subsetsWithDup(self, nums: List[int]) -> List[List[int]]:
        nums.sort()
        result = []
        path = []

        def backtrack(start: int) -> None:
            result.append(path.copy())
            prev = None
            for i in range(start, len(nums)):
                if nums[i] == prev:
                    continue
                prev = nums[i]
                path.append(nums[i])
                backtrack(i + 1)
                path.pop()

        backtrack(0)
        return result
```
- **Time:** O(n * 2^n) — **Space:** O(n) auxiliary plus output

## Approach 3 — Optimal
**Idea:** Compress values into counts and choose how many copies of each distinct value to include.
```python
from collections import Counter
from typing import List

class Solution:
    def subsetsWithDup(self, nums: List[int]) -> List[List[int]]:
        items = sorted(Counter(nums).items())
        result = []
        path = []

        def backtrack(pos: int) -> None:
            if pos == len(items):
                result.append(path.copy())
                return
            value, count = items[pos]
            backtrack(pos + 1)       # Choose zero copies.
            for _ in range(count):
                path.append(value)
                backtrack(pos + 1)
            for _ in range(count):
                path.pop()

        backtrack(0)
        return result
```
- **Time:** O(n * R) — **Space:** O(k + n) auxiliary plus output, where `R` is unique subset count

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * 2^n) | O(n * 2^n) |
| Better | O(n * 2^n) | O(n) plus output |
| Optimal | O(n * R) | O(k + n) plus output |

## Edge Cases & Pitfalls
- Sort before deduplication.
- Skip duplicates only among siblings, not across deeper recursive calls.
- Include the empty subset.

## Related
- Subsets
- Combination Sum II
- Permutations II
