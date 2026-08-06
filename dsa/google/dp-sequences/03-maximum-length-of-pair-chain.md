# 03. Maximum Length of Pair Chain

- **Difficulty:** Medium
- **Pattern:** DP on sequences / subsequences
- **Asked at:** Google, Amazon, Microsoft

## Problem
Given pairs `pairs` where `pairs[i] = [left, right]` and `left < right`, form the longest chain such that pair `p` can be followed by pair `q` only when `p[1] < q[0]`. Return the maximum chain length.
Constraints: `1 <= len(pairs) <= 1000`, `-1000 <= left < right <= 1000`.

## Examples
```text
Input: pairs = [[1,2],[2,3],[3,4]]
Output: 2
Explanation: [1,2] -> [3,4] is valid; [1,2] -> [2,3] is not because the inequality is strict.
```

## Understanding & Intuition
This is an interval sequence DP: each pair can either be skipped or appended after the last chosen compatible pair. Sorting reveals both a DP solution and a greedy optimal structure.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively choose or skip each pair after sorting by start.
```python
class Solution:
    def findLongestChain(self, pairs: list[list[int]]) -> int:
        arr = sorted(pairs)
        n = len(arr)
        def dfs(i: int, prev: int) -> int:
            if i == n:
                return 0
            ans = dfs(i + 1, prev)
            if prev == -1 or arr[prev][1] < arr[i][0]:
                ans = max(ans, 1 + dfs(i + 1, i))
            return ans
        return dfs(0, -1)
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Dynamic programming over sorted pairs, tracking best chain ending at each pair.
```python
class Solution:
    def findLongestChain(self, pairs: list[list[int]]) -> int:
        arr = sorted(pairs)
        n = len(arr)
        dp = [1] * n
        for i in range(n):
            for j in range(i):
                if arr[j][1] < arr[i][0]:
                    dp[i] = max(dp[i], dp[j] + 1)
        return max(dp, default=0)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Sort by ending coordinate and greedily take the earliest-finishing compatible pair.
```python
class Solution:
    def findLongestChain(self, pairs: list[list[int]]) -> int:
        cur = -10**20
        ans = 0
        for left, right in sorted(pairs, key=lambda p: p[1]):
            if left > cur:
                ans += 1
                cur = right
        return ans
```
- **Time:** O(n log n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n log n) | O(1) |

## Edge Cases & Pitfalls
- Compatibility is `previous_right < next_left`, not `<=`.
- Sorting by end is what makes the greedy choice safe.

## Related
- Russian Doll Envelopes
- Non-overlapping Intervals
