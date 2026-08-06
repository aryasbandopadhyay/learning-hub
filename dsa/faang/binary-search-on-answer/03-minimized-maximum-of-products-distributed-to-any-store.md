# 03. Minimized Maximum of Products Distributed to Any Store

- **Difficulty:** Medium
- **Pattern:** Binary Search on the Answer
- **Asked at:** Amazon, Meta, Walmart

## Problem
Distribute product quantities across `n` stores. Each store receives at most one product type; a type may be split. Minimize the maximum products in any store. Constraints: `1 <= len(quantities) <= n <= 10^5`.

## Examples
```text
Input: n = 6, quantities = [11, 6]
Output: 3
Explanation: Split 11 into 3,3,3,2 and 6 into 3,3.
```

## Understanding & Intuition
A limit is feasible when the sum of stores required by each type is at most `n`. Required stores shrink as the limit grows.

## Approach 1 — Naive / Brute Force
**Idea:** scan every possible limit.
```python
class Solution:
    def minimizedMaximum(self, n, quantities):
        def need(limit):
            total = 0
            for q in quantities:
                total += (q + limit - 1) // limit
            return total
        for x in range(1, max(quantities)+1):
            if need(x) <= n: return x
        return max(quantities)
```

- **Time:** O(tM) — **Space:** O(1)

## Approach 2 — Better
**Idea:** binary-search with full counting.
```python
class Solution:
    def minimizedMaximum(self, n, quantities):
        def need(limit):
            total = 0
            for q in quantities:
                total += (q + limit - 1) // limit
            return total
        lo,hi,ans=1,max(quantities),max(quantities)
        while lo<=hi:
            mid=(lo+hi)//2
            if need(mid)<=n: ans=mid; hi=mid-1
            else: lo=mid+1
        return ans
```

- **Time:** O(t log M) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** start at an average lower bound and early-exit.
```python
class Solution:
    def minimizedMaximum(self, n, quantities):
        lo=max(1,(sum(quantities)+n-1)//n); hi=max(quantities)
        def can(limit):
            used=0
            for q in quantities:
                used += (q + limit - 1) // limit
                if used > n: return False
            return True
        while lo < hi:
            mid=(lo+hi)//2
            if can(mid): hi=mid
            else: lo=mid+1
        return lo
```

- **Time:** O(t log M) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(tM) | O(1) |
| Better | O(t log M) | O(1) |
| Optimal | O(t log M) | O(1) |


## Edge Cases & Pitfalls
- Use ceiling division per product type.
- Stores cannot mix product types.
- The optimum can be below every original quantity.


## Related
- Minimum Limit of Balls in a Bag
- Smallest Divisor Given a Threshold
