# 01. Last Stone Weight II

- **Difficulty:** Medium
- **Pattern:** 0/1 knapsack subset sum
- **Asked at:** Amazon, Google, Meta

## Problem
Given stone weights, repeatedly smashing two stones is equivalent to assigning every stone to one of two piles. Return the smallest possible remaining weight. Constraints: `1 <= len(stones) <= 30`, `1 <= stones[i] <= 100`.

## Examples
```text
Input: stones = [2,7,4,1,8,1]
Output: 1
Explanation: Split as 11 and 12, leaving difference 1.
```

## Understanding & Intuition
The last stone weight is the absolute difference between two subset sums. To minimize it, find a subset sum as close as possible to half the total. This is exactly 0/1 subset-sum feasibility.

## Approach 1 — Naive / Brute Force
**Idea:** Try assigning each stone to the left or right pile.
```python
class Solution:
    def lastStoneWeightII(self, stones: list[int]) -> int:
        best = sum(stones)
        def dfs(i: int, left: int, right: int) -> None:
            nonlocal best
            if i == len(stones):
                best = min(best, abs(left - right))
                return
            dfs(i + 1, left + stones[i], right)
            dfs(i + 1, left, right + stones[i])
        dfs(0, 0, 0)
        return best
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Store every reachable subset sum and choose the closest one.
```python
class Solution:
    def lastStoneWeightII(self, stones: list[int]) -> int:
        total = sum(stones)
        reachable = {0}
        for w in stones:
            reachable |= {s + w for s in reachable}
        return min(abs(total - 2 * s) for s in reachable)
```
- **Time:** O(n * total) — **Space:** O(total)

## Approach 3 — Optimal
**Idea:** Use one-dimensional boolean knapsack only up to `total // 2`.
```python
class Solution:
    def lastStoneWeightII(self, stones: list[int]) -> int:
        total = sum(stones)
        half = total // 2
        dp = [False] * (half + 1)
        dp[0] = True
        for w in stones:
            for s in range(half, w - 1, -1):
                dp[s] = dp[s] or dp[s - w]
        for s in range(half, -1, -1):
            if dp[s]:
                return total - 2 * s
        return total
```
- **Time:** O(n * total) — **Space:** O(total)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n * total) | O(total) |
| Optimal | O(n * total) | O(total) |

## Edge Cases & Pitfalls
- A subset sum of zero is always reachable.
- Iterate backward so each stone is used once.
- Only sums up to half the total are needed for the optimal answer.

## Related
- Partition Equal Subset Sum
- Target Sum
