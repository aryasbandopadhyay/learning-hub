# 03. Number of Dice Rolls With Target Sum

- **Difficulty:** Medium
- **Pattern:** bounded counting DP
- **Asked at:** Meta, Amazon, Google

## Problem
Implement `numRollsToTarget` for **Number of Dice Rolls With Target Sum**. Given `n` dice, each with faces `1..k`, return how many ways produce exactly `target`, modulo `1_000_000_007`.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `n`: integer; problem size or count as defined above.
- `k`: integer; required count, rank, or operation limit as defined above.
- `target`: integer; target value or string.

**Output**
- A single integer.

## Constraints
- `1 <= n,k <= 30`, `1 <= target <= 1000`

## Examples
```text
Input: n = 2, k = 6, target = 7
Output: 6
Explanation: The pairs are (1,6), (2,5), (3,4), (4,3), (5,2), and (6,1). This is the required result for the given input under the rules above.
```

## Understanding & Intuition
This counts bounded compositions of a target sum. The state is how many dice have been placed and the current sum. A sliding window over previous sums removes the face loop.

## Approach 1 — Naive / Brute Force
**Idea:** Try every face on every die.
```python
class Solution:
    def numRollsToTarget(self, n: int, k: int, target: int) -> int:
        MOD = 1000000007
        def dfs(dice: int, total: int) -> int:
            if total > target:
                return 0
            if dice == n:
                return 1 if total == target else 0
            ans = 0
            for face in range(1, k + 1):
                ans += dfs(dice + 1, total + face)
            return ans % MOD
        return dfs(0, 0)
```
- **Time:** O(k^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize by dice count and current total.
```python
class Solution:
    def numRollsToTarget(self, n: int, k: int, target: int) -> int:
        from functools import lru_cache
        MOD = 1000000007
        @lru_cache(None)
        def dp(dice: int, total: int) -> int:
            if total > target:
                return 0
            if dice == n:
                return 1 if total == target else 0
            return sum(dp(dice + 1, total + face) for face in range(1, k + 1)) % MOD
        return dp(0, 0)
```
- **Time:** O(nktarget) — **Space:** O(ntarget)

## Approach 3 — Optimal
**Idea:** Build dice layers with a sliding window of the previous layer.
```python
class Solution:
    def numRollsToTarget(self, n: int, k: int, target: int) -> int:
        MOD = 1000000007
        prev = [0] * (target + 1)
        prev[0] = 1
        for _ in range(n):
            cur = [0] * (target + 1)
            window = 0
            for s in range(1, target + 1):
                window = (window + prev[s - 1]) % MOD
                if s - k - 1 >= 0:
                    window = (window - prev[s - k - 1]) % MOD
                cur[s] = window
            prev = cur
        return prev[target] % MOD
```
- **Time:** O(ntarget) — **Space:** O(target)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(k^n) | O(n) |
| Better | O(nktarget) | O(ntarget) |
| Optimal | O(ntarget) | O(target) |

## Edge Cases & Pitfalls
- Impossible targets should return zero.
- Apply modulo during transitions.
- The window represents faces `1..k`, not `0..k`.

## Related
- Combination Sum IV
- Number of Ways to Earn Points
