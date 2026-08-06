# 07. Minimum Cost For Tickets

- **Difficulty:** Medium
- **Pattern:** covering interval DP
- **Asked at:** Amazon, Google, Uber

## Problem
Given sorted travel `days` and ticket `costs` for 1-day, 7-day, and 30-day passes, return the minimum cost to cover all travel days. Constraints: `1 <= len(days) <= 365`, `1 <= days[i] <= 365`.

## Examples
```text
Input: days = [1,4,6,7,8,20], costs = [2,7,15]
Output: 11
Explanation: Buy 1-day passes for days 1 and 4, a 7-day pass for 6 through 12, and a 1-day pass for 20.
```

## Understanding & Intuition
A pass bought on a travel day covers a prefix of the remaining travel days. The state can be the index of the next uncovered day. Since days are bounded, a calendar DP is also simple and efficient.

## Approach 1 — Naive / Brute Force
**Idea:** Try each pass type at the first uncovered travel day.
```python
class Solution:
    def mincostTickets(self, days: list[int], costs: list[int]) -> int:
        durations = [1, 7, 30]
        def dfs(i: int) -> int:
            if i >= len(days):
                return 0
            best = 10**9
            for dur, cost in zip(durations, costs):
                j = i
                while j < len(days) and days[j] < days[i] + dur:
                    j += 1
                best = min(best, cost + dfs(j))
            return best
        return dfs(0)
```
- **Time:** O(3^n * n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize the next-uncovered-day index.
```python
class Solution:
    def mincostTickets(self, days: list[int], costs: list[int]) -> int:
        from functools import lru_cache
        durations = [1, 7, 30]
        @lru_cache(None)
        def dp(i: int) -> int:
            if i >= len(days):
                return 0
            ans = 10**9
            for dur, cost in zip(durations, costs):
                j = i
                while j < len(days) and days[j] < days[i] + dur:
                    j += 1
                ans = min(ans, cost + dp(j))
            return ans
        return dp(0)
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Run calendar DP, paying only on travel days.
```python
class Solution:
    def mincostTickets(self, days: list[int], costs: list[int]) -> int:
        travel = set(days)
        last = days[-1]
        dp = [0] * (last + 1)
        for day in range(1, last + 1):
            if day not in travel:
                dp[day] = dp[day - 1]
            else:
                dp[day] = min(
                    dp[max(0, day - 1)] + costs[0],
                    dp[max(0, day - 7)] + costs[1],
                    dp[max(0, day - 30)] + costs[2],
                )
        return dp[last]
```
- **Time:** O(last day) — **Space:** O(last day)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(3^n * n) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(last day) | O(last day) |

## Edge Cases & Pitfalls
- Pass coverage includes the purchase day.
- Do not pay on non-travel days.
- `days` is sorted, so index jumps are valid.

## Related
- House Robber
- Paint House
