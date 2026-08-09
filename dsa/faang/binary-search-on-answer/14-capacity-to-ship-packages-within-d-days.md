# 14. Capacity To Ship Packages Within D Days

- **Difficulty:** Medium
- **Pattern:** Binary Search on the Answer
- **Asked at:** Amazon, Google, Microsoft

## Problem
Implement `shipWithinDays` for **Capacity To Ship Packages Within D Days**. You are given package weights in order and an integer `days`. Each day, a ship loads a contiguous sequence of packages without exceeding its capacity, and packages must be shipped in the given order. Return the least ship capacity needed to ship every package within `days` days.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `weights`: list; package weights in order.
- `days`: integer; days value or travel days list.

**Output**
- A single integer.

## Constraints
- `1 <= days <= len(weights) <= 5 * 10^4`, `1 <= weights[i] <= 500`

## Examples
```text
Input: weights = [1,2,3,4,5,6,7,8,9,10], days = 5
Output: 15
Explanation: Capacity 15 can ship the packages as [1,2,3,4,5], [6,7], [8], [9], and [10]. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
A feasible capacity must be at least the heaviest package and at most the sum of all packages. If a capacity works, any larger capacity also works, giving a monotonic predicate. The task is to find the smallest feasible capacity.

## Approach 1 — Naive / Brute Force
**Idea:** test capacities one by one from the heaviest package upward until one can finish within `days`.
```python
class Solution:
    def shipWithinDays(self, weights, days):
        def can_ship(capacity):
            used = 1
            load = 0
            for weight in weights:
                if load + weight > capacity:
                    used += 1
                    load = 0
                load += weight
            return used <= days

        for capacity in range(max(weights), sum(weights) + 1):
            if can_ship(capacity):
                return capacity
        return sum(weights)
```
- **Time:** O((sum(weights) - max(weights)) * n) — **Space:** O(1)

## Approach 2 — Better
**Idea:** dynamic programming where `dp[d][i]` is the minimum largest load for the first `i` packages in `d` days.
```python
class Solution:
    def shipWithinDays(self, weights, days):
        n = len(weights)
        if days >= n:
            return max(weights)
        prefix = [0]
        for w in weights:
            prefix.append(prefix[-1] + w)
        prev = [0] + [prefix[i] for i in range(1, n + 1)]
        for d in range(2, days + 1):
            curr = [0] * (n + 1)
            for i in range(1, n + 1):
                best = prefix[i]
                for cut in range(d - 1, i):
                    best = min(best, max(prev[cut], prefix[i] - prefix[cut]))
                curr[i] = best
            prev = curr
        return prev[n]
```
- **Time:** O(days * n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** binary-search the capacity and greedily count how many days that capacity needs.
```python
class Solution:
    def shipWithinDays(self, weights, days):
        def needed_days(capacity):
            used = 1
            load = 0
            for weight in weights:
                if load + weight > capacity:
                    used += 1
                    load = 0
                load += weight
            return used

        lo, hi = max(weights), sum(weights)
        while lo < hi:
            mid = (lo + hi) // 2
            if needed_days(mid) <= days:
                hi = mid
            else:
                lo = mid + 1
        return lo
```
- **Time:** O(n log sum(weights)) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O((sum(weights) - max(weights)) * n) | O(1) |
| Better | O(days * n^2) | O(n) |
| Optimal | O(n log sum(weights)) | O(1) |

## Edge Cases & Pitfalls
- Capacity cannot be below `max(weights)`.
- A day can ship multiple packages, but only a contiguous prefix of the remaining order.
- Return the minimum feasible capacity, not just any feasible capacity.

## Related
- Split Array Largest Sum
- Koko Eating Bananas
