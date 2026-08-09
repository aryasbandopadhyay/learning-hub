# 01. Jump Game

- **Difficulty:** Medium
- **Pattern:** Greedy
- **Asked at:** Amazon, Google, Microsoft, Meta

## Problem
You are given an array `nums` where `nums[i]` is the maximum jump length from index `i`. Starting at
index `0`, determine whether you can reach the last index.

**Input**
- `nums`: a list of non-negative integers.

**Output**
- A boolean: `True` if the last index is reachable, otherwise `False`.

## Constraints
- 1 <= nums.length <= 10^4
- 0 <= nums[i] <= 10^5

## Examples
```text
Input: nums = [2,3,1,1,4]
Output: True
Explanation: Jump from index `0` to index `1`, then from index `1` to the last index.
```

## Understanding & Intuition
At every index, we only need to know whether that index is reachable. The greedy choice is to keep the farthest reachable position seen so far. If the scan ever moves beyond it, no later jump can help because later positions are unreachable.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively try every jump from each reachable index.
```python
from typing import List

class Solution:
    def canJump(self, nums: List[int]) -> bool:
        n = len(nums)

        def dfs(i: int) -> bool:
            # Once we reach or cross the last index, the game is won.
            if i >= n - 1:
                return True
            for step in range(1, nums[i] + 1):
                if dfs(i + step):
                    return True
            return False

        return dfs(0)
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize whether each index can reach the end.
```python
from functools import lru_cache
from typing import List

class Solution:
    def canJump(self, nums: List[int]) -> bool:
        n = len(nums)

        @lru_cache(None)
        def good(i: int) -> bool:
            if i >= n - 1:
                return True
            furthest = min(n - 1, i + nums[i])
            for nxt in range(i + 1, furthest + 1):
                if good(nxt):
                    return True
            return False

        return good(0)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Scan left to right and keep the farthest index reachable so far.
```python
from typing import List

class Solution:
    def canJump(self, nums: List[int]) -> bool:
        farthest = 0
        for i, jump in enumerate(nums):
            if i > farthest:
                return False
            farthest = max(farthest, i + jump)
            if farthest >= len(nums) - 1:
                return True
        return True
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- A single-element array is already reachable.
- Zeros are only blocking when they appear before the current farthest reach.
- Do not greedily jump to the locally largest value; track reach instead.

## Related
- Jump Game II
- Minimum Number of Refueling Stops
