# 11. Best Team With No Conflicts

- **Difficulty:** Medium
- **Pattern:** DP on sequences / subsequences
- **Asked at:** Google, Amazon, Facebook

## Problem
Given equal-length arrays `scores` and `ages`, choose a team with maximum total score. A conflict exists if a younger player has a strictly higher score than an older player. Return the best possible score.
Constraints: `1 <= len(scores) == len(ages) <= 1000`, `1 <= scores[i], ages[i] <= 10^6`.

## Examples
```text
Input: scores = [1,3,5,10,15], ages = [1,2,3,4,5]
Output: 34
Explanation: Scores already increase with age, so every player can be selected.
```

## Understanding & Intuition
Sort players by age and then score. The conflict rule becomes a nondecreasing-score subsequence problem where each chosen score contributes weight.

## Approach 1 — Naive / Brute Force
**Idea:** Try every subset in sorted order, requiring nondecreasing scores.
```python
class Solution:
    def bestTeamScore(self, scores: list[int], ages: list[int]) -> int:
        players = sorted(zip(ages, scores))
        n = len(players)
        def dfs(i: int, last_score: int) -> int:
            if i == n:
                return 0
            best = dfs(i + 1, last_score)
            score = players[i][1]
            if score >= last_score:
                best = max(best, score + dfs(i + 1, score))
            return best
        return dfs(0, 0)
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize by index and previous chosen player index.
```python
class Solution:
    def bestTeamScore(self, scores: list[int], ages: list[int]) -> int:
        from functools import lru_cache
        players = sorted(zip(ages, scores))
        n = len(players)
        @lru_cache(None)
        def dfs(i: int, prev: int) -> int:
            if i == n:
                return 0
            best = dfs(i + 1, prev)
            if prev == -1 or players[i][1] >= players[prev][1]:
                best = max(best, players[i][1] + dfs(i + 1, i))
            return best
        return dfs(0, -1)
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Weighted LIS: `dp[i]` is the best valid team score ending with sorted player `i`.
```python
class Solution:
    def bestTeamScore(self, scores: list[int], ages: list[int]) -> int:
        players = sorted(zip(ages, scores))
        n = len(players)
        dp = [0] * n
        ans = 0
        for i, (_, score) in enumerate(players):
            dp[i] = score
            for j in range(i):
                if players[j][1] <= score:
                    dp[i] = max(dp[i], dp[j] + score)
            ans = max(ans, dp[i])
        return ans
```
- **Time:** O(n^2) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n^2) | O(n^2) |
| Optimal | O(n^2) | O(n) |

## Edge Cases & Pitfalls
- Equal ages never conflict; sorting by `(age, score)` handles them safely.
- Scores must be nondecreasing after sorting by age.

## Related
- Longest Increasing Subsequence
- Maximum Profit in Job Scheduling
