# 05. Partition Array for Maximum Sum

- **Difficulty:** Medium
- **Pattern:** DP on sequences / subsequences
- **Asked at:** Google, Amazon, Bloomberg

## Problem
You are given an integer array `arr` and an integer `k`.

Partition the array into contiguous blocks, each of length at most `k`. Replace every value in a block by that block's maximum value. Return the largest possible sum of the transformed array.

**Input**
- `arr`: a list of integers.
- `k`: the maximum block length.

**Output**
- The maximum sum after partitioning and replacement.

## Constraints
- `1 <= arr.length <= 500`
- `0 <= arr[i] <= 10^9`
- `1 <= k <= arr.length`

## Examples
```text
Input: arr = [1,15,7,9,2,5,10], k = 3
Output: 84
Explanation: One optimal partition is `[1,15,7]`, `[9]`, `[2,5,10]`, which becomes `15,15,15,9,10,10,10` and sums to `84`.
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
