# 13. Number of Ways to Earn Points

- **Difficulty:** Hard
- **Pattern:** bounded knapsack counting
- **Asked at:** Google, Amazon, Meta

## Problem
Implement `waysToReachTarget` for **Number of Ways to Earn Points**. Given exam question types `types`, where `types[i] = [count, marks]`, return the number of ways to earn exactly `target` points modulo `1_000_000_007`. You may solve at most `count` questions of each type.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `target`: integer; target value or string.
- `types`: list; question type data.

**Output**
- A single integer.

## Constraints
- `1 <= target <= 1000`, `1 <= len(types) <= 50`

## Examples
```text
Input: target = 6, types = [[6,1],[3,2],[2,3]]
Output: 7
Explanation: There are seven bounded combinations of question counts totaling 6 marks. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Each type is a bounded group of identical items: choose 0 through `count` questions, each worth `marks`. The DP state is the score after processing some types. A residue-class sliding window optimizes the bounded transition.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively try every allowed count for each type.
```python
class Solution:
    def waysToReachTarget(self, target: int, types: list[list[int]]) -> int:
        MOD = 1000000007
        def dfs(i: int, score: int) -> int:
            if score > target:
                return 0
            if i == len(types):
                return 1 if score == target else 0
            cnt, marks = types[i]
            ans = 0
            for take in range(cnt + 1):
                ans += dfs(i + 1, score + take * marks)
            return ans % MOD
        return dfs(0, 0)
```
- **Time:** O(product(count_i + 1)) — **Space:** O(types)

## Approach 2 — Better
**Idea:** Memoize by type index and current score.
```python
class Solution:
    def waysToReachTarget(self, target: int, types: list[list[int]]) -> int:
        from functools import lru_cache
        MOD = 1000000007
        @lru_cache(None)
        def dp(i: int, score: int) -> int:
            if score > target:
                return 0
            if i == len(types):
                return 1 if score == target else 0
            cnt, marks = types[i]
            ans = 0
            for take in range(cnt + 1):
                ans += dp(i + 1, score + take * marks)
            return ans % MOD
        return dp(0, 0)
```
- **Time:** O(T * target * maxCount) — **Space:** O(T * target)

## Approach 3 — Optimal
**Idea:** Apply bounded knapsack using residue-class sliding windows.
```python
class Solution:
    def waysToReachTarget(self, target: int, types: list[list[int]]) -> int:
        MOD = 1000000007
        dp = [0] * (target + 1)
        dp[0] = 1
        for cnt, marks in types:
            ndp = [0] * (target + 1)
            for r in range(marks):
                window = 0
                q = 0
                for s in range(r, target + 1, marks):
                    window = (window + dp[s]) % MOD
                    if q - cnt - 1 >= 0:
                        window = (window - dp[s - (cnt + 1) * marks]) % MOD
                    ndp[s] = window
                    q += 1
            dp = ndp
        return dp[target] % MOD
```
- **Time:** O(T * target) — **Space:** O(target)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(product(count_i + 1)) | O(types) |
| Better | O(T * target * maxCount) | O(T * target) |
| Optimal | O(T * target) | O(target) |

## Edge Cases & Pitfalls
- Questions of the same type are identical, not distinct.
- Stop paths whose score exceeds target.
- The residue window contains at most `count + 1` previous choices.

## Related
- Number of Dice Rolls With Target Sum
- Profitable Schemes
