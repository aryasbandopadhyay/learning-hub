# 10. Maximum Candies Allocated to K Children

- **Difficulty:** Medium
- **Pattern:** Advanced Binary Search
- **Asked at:** Google

## Problem
You are given piles of candies and an integer `k`.

You may split piles into smaller piles, but you cannot combine piles. Give each of `k` children one pile with the same positive number of candies. Return the largest possible number of candies per child, or `0` if none can receive a candy.

**Input**
- `candies`: a list of pile sizes.
- `k`: the number of children.

**Output**
- The maximum equal candies per child, or `0` if no positive amount works.

## Constraints
- `1 <= candies.length <= 10^5`
- `1 <= candies[i] <= 10^7`
- `1 <= k <= 10^12`

## Examples
```text
Input: candies = [5,8,6], k = 3
Output: 5
Explanation: A size of `5` can be given to three children. Size `6` would produce only two piles, so `5` is maximum.
```

## Understanding & Intuition
For a candidate share size `x`, pile `c` can serve `c // x` children. Larger shares can serve fewer children, making feasibility monotonic.

## Approach 1 — Naive / Brute Force
**Idea:** Try every share size from the largest down until enough children can be served.
```python
class Solution:
    def maximumCandies(self, candies: list[int], k: int) -> int:
        for size in range(max(candies), 0, -1):
            served = 0
            for c in candies:
                served += c // size
            if served >= k:
                return size
        return 0
```
- **Time:** O(nM) — **Space:** O(1), where `M = max(candies)`

## Approach 2 — Better
**Idea:** Binary search the possible share size and keep the best feasible value.
```python
class Solution:
    def maximumCandies(self, candies: list[int], k: int) -> int:
        lo, hi = 1, max(candies)
        ans = 0
        while lo <= hi:
            mid = (lo + hi) // 2
            served = sum(c // mid for c in candies)
            if served >= k:
                ans = mid
                lo = mid + 1
            else:
                hi = mid - 1
        return ans
```
- **Time:** O(n log M) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Cap the high bound by the average possible share and stop counting once `k` children are served.
```python
class Solution:
    def maximumCandies(self, candies: list[int], k: int) -> int:
        hi = min(max(candies), sum(candies) // k)
        if hi == 0:
            return 0
        lo = 1
        while lo < hi:
            mid = (lo + hi + 1) // 2
            served = 0
            for c in candies:
                served += c // mid
                if served >= k:
                    break
            if served >= k:
                lo = mid
            else:
                hi = mid - 1
        return lo
```
- **Time:** O(n log M) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nM) | O(1) |
| Better | O(n log M) | O(1) |
| Optimal | O(n log M) | O(1) |

## Edge Cases & Pitfalls
- If total candies are less than `k`, return 0.
- Do not allow share size 0 in division.
- Piles may be split but pieces from different piles cannot be merged.

## Related
- Smallest Divisor Given a Threshold
- Koko Eating Bananas
