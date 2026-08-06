# 02. Jump Game II

- **Difficulty:** Medium
- **Pattern:** Greedy
- **Asked at:** Amazon, Google, Microsoft, Apple

## Problem
Given an integer array `nums`, where `nums[i]` is the maximum jump length from index `i`, return the minimum number of jumps needed to reach the last index. You may assume the last index is reachable. Constraints: `1 <= len(nums) <= 10^4`, `0 <= nums[i] <= 1000`.

## Examples
```text
Input: nums = [2,3,1,1,4]
Output: 2
Explanation: Jump from index 0 to 1, then from index 1 to the last index.
```

## Understanding & Intuition
Each jump covers a range of indices that are reachable with the same number of jumps. The greedy choice is to finish the current range, then jump to the farthest range discovered inside it. This is equivalent to BFS by levels without storing a queue.

## Approach 1 — Naive / Brute Force
**Idea:** Try every jump recursively and take the minimum.
```python
from typing import List

class Solution:
    def jump(self, nums: List[int]) -> int:
        n = len(nums)

        def dfs(i: int) -> int:
            if i >= n - 1:
                return 0
            best = float("inf")
            for step in range(1, nums[i] + 1):
                best = min(best, 1 + dfs(i + step))
            return best

        return dfs(0)
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Use dynamic programming from right to left.
```python
from typing import List

class Solution:
    def jump(self, nums: List[int]) -> int:
        n = len(nums)
        dp = [float("inf")] * n
        dp[-1] = 0
        for i in range(n - 2, -1, -1):
            for j in range(i + 1, min(n, i + nums[i] + 1)):
                dp[i] = min(dp[i], 1 + dp[j])
        return dp[0]
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Count jumps when the scan reaches the end of the current reachable window.
```python
from typing import List

class Solution:
    def jump(self, nums: List[int]) -> int:
        jumps = 0
        current_end = 0
        farthest = 0

        for i in range(len(nums) - 1):
            farthest = max(farthest, i + nums[i])
            if i == current_end:
                jumps += 1
                current_end = farthest
        return jumps
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Return `0` for a one-element array.
- Iterate only to `len(nums) - 2`; no jump is needed from the last index.
- The greedy window works because all indices in a window have equal jump count.

## Related
- Jump Game
- Shortest Path in an Unweighted Graph
