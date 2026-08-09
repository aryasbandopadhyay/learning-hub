# 02. Two City Scheduling

- **Difficulty:** Medium
- **Pattern:** greedy
- **Asked at:** Google

## Problem
There are `2n` people. `costs[i] = [aCost, bCost]` gives the cost to send person `i` to city A or city B.

Send exactly `n` people to each city while minimizing the total cost.

**Input**
- `costs`: an even-length list of `[cost_to_A, cost_to_B]` pairs.

**Output**
- The minimum total travel cost.

## Constraints
- `2 <= costs.length <= 100`
- `costs.length` is even
- `1 <= cost_to_A, cost_to_B <= 1000`

## Examples
```text
Input: costs = [[10,20],[30,200],[400,50],[30,20]]
Output: 110
Explanation: Send people `0` and `1` to city A and people `2` and `3` to city B, for total `10 + 30 + 50 + 20 = 110`.
```

## Understanding & Intuition
If everyone starts in city B, moving person `i` to city A changes the total by `aCost - bCost`. The cheapest valid schedule chooses the `n` most beneficial moves. This local ranking is globally valid because every person contributes independently.

## Approach 1 — Naive / Brute Force
**Idea:** Try every group of `n` people for city A.
```python
class Solution:
    def twoCitySchedCost(self, costs: list[list[int]]) -> int:
        from itertools import combinations
        m, n = len(costs), len(costs) // 2
        best = 10 ** 18
        for a_group in combinations(range(m), n):
            chosen = set(a_group)
            total = sum(costs[i][0] if i in chosen else costs[i][1] for i in range(m))
            best = min(best, total)
        return best
```
- **Time:** O(n * C(2n,n)) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Dynamic programming assigns people one by one while tracking how many went to A and B.
```python
class Solution:
    def twoCitySchedCost(self, costs):
        n = len(costs) // 2
        INF = 10 ** 18
        dp = [[INF] * (n + 1) for _ in range(n + 1)]
        dp[0][0] = 0
        for a_cost, b_cost in costs:
            ndp = [[INF] * (n + 1) for _ in range(n + 1)]
            for a in range(n + 1):
                for b in range(n + 1):
                    if dp[a][b] == INF:
                        continue
                    if a < n:
                        ndp[a + 1][b] = min(ndp[a + 1][b], dp[a][b] + a_cost)
                    if b < n:
                        ndp[a][b + 1] = min(ndp[a][b + 1], dp[a][b] + b_cost)
            dp = ndp
        return dp[n][n]
```
- **Time:** O(n^3) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Sort by the savings of choosing A instead of B and send the first half to A.
```python
class Solution:
    def twoCitySchedCost(self, costs):
        costs = sorted(costs, key=lambda c: c[0] - c[1])
        n = len(costs) // 2
        return sum(c[0] for c in costs[:n]) + sum(c[1] for c in costs[n:])
```
- **Time:** O(n log n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n * C(2n,n)) | O(n) |
| Better | O(n^3) | O(n^2) |
| Optimal | O(n log n) | O(n) |

## Edge Cases & Pitfalls
- Exactly half must go to each city.
- Sort by cost difference, not by absolute cost.
- Ties do not matter because the return value is only the total.

## Related
- Minimum Cost to Hire K Workers
- Assign Cookies
