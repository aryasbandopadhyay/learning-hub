# 05. Partition Array for Maximum Sum

- **Difficulty:** Medium
- **Pattern:** DP on sequences / subsequences
- **Asked at:** Google, Amazon, Bloomberg

## Problem
Given `arr` and an integer `k`, partition the array into contiguous subarrays of length at most `k`. After partitioning, each subarray contributes its maximum value multiplied by its length. Return the largest possible total.
Constraints: `1 <= len(arr) <= 500`, `1 <= k <= len(arr)`, `0 <= arr[i] <= 10^9`.

## Examples
```text
Input: arr = [1,15,7,9,2,5,10], k = 3
Output: 84
Explanation: Partition as [1,15,7], [9], [2,5,10] for 15*3 + 9 + 10*3.
```

## Understanding & Intuition
The last partition ending at an index has length from 1 to `k`. Once its length is chosen, its contribution is the maximum inside that suffix times the length plus the best score before it.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively try every allowed first partition length.
```python
class Solution:
    def maxSumAfterPartitioning(self, arr: list[int], k: int) -> int:
        n = len(arr)
        def dfs(i: int) -> int:
            if i == n:
                return 0
            best = cur_max = 0
            for j in range(i, min(n, i + k)):
                cur_max = max(cur_max, arr[j])
                best = max(best, cur_max * (j - i + 1) + dfs(j + 1))
            return best
        return dfs(0)
```
- **Time:** O(k^(n/k)) exponential — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize the recursion by starting index.
```python
class Solution:
    def maxSumAfterPartitioning(self, arr: list[int], k: int) -> int:
        from functools import lru_cache
        n = len(arr)
        @lru_cache(None)
        def dfs(i: int) -> int:
            if i == n:
                return 0
            best = cur_max = 0
            for j in range(i, min(n, i + k)):
                cur_max = max(cur_max, arr[j])
                best = max(best, cur_max * (j - i + 1) + dfs(j + 1))
            return best
        return dfs(0)
```
- **Time:** O(nk) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Build bottom-up DP where `dp[i]` is the best score for the first `i` elements.
```python
class Solution:
    def maxSumAfterPartitioning(self, arr: list[int], k: int) -> int:
        n = len(arr)
        dp = [0] * (n + 1)
        for i in range(1, n + 1):
            cur_max = 0
            for length in range(1, min(k, i) + 1):
                cur_max = max(cur_max, arr[i - length])
                dp[i] = max(dp[i], dp[i - length] + cur_max * length)
        return dp[n]
```
- **Time:** O(nk) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | Exponential | O(n) |
| Better | O(nk) | O(n) |
| Optimal | O(nk) | O(n) |

## Edge Cases & Pitfalls
- Partitions must be contiguous and cover every element.
- Keep updating the suffix maximum as the partition expands backward.

## Related
- Split Array Largest Sum
- Minimum Cost Climbing Stairs
