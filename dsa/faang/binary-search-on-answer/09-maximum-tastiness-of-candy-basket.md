# 09. Maximum Tastiness of Candy Basket

- **Difficulty:** Medium
- **Pattern:** Binary Search on the Answer
- **Asked at:** Amazon, Google, ByteDance

## Problem
Implement `maximumTastiness` for **Maximum Tastiness of Candy Basket**. Choose exactly `k` candy prices to maximize the minimum absolute difference between any two chosen prices. Return that maximum tastiness.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `price`: list; unit prices.
- `k`: integer; required count, rank, or operation limit as defined above.

**Output**
- A single integer.

## Constraints
- `2 <= k <= len(price) <= 10^5`

## Examples
```text
Input: price = [13, 5, 1, 8, 21, 2], k = 3
Output: 8
Explanation: Choose 5, 13, and 21. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
After sorting, greedily picking the earliest price at least `gap` away maximizes remaining choices. Feasible gaps form a prefix.

## Approach 1 — Naive / Brute Force
**Idea:** scan all gaps.
```python
class Solution:
    def maximumTastiness(self, price, k):
        price.sort()
        def can(gap):
            count, last = 1, price[0]
            for p in price[1:]:
                if p - last >= gap:
                    count += 1; last = p
                    if count == k: return True
            return False
        ans=0
        for g in range(0,price[-1]-price[0]+1):
            if can(g): ans=g
        return ans
```

- **Time:** O(nR) — **Space:** O(1)

## Approach 2 — Better
**Idea:** binary-search sorted pairwise gaps.
```python
class Solution:
    def maximumTastiness(self, price, k):
        price.sort()
        def can(gap):
            count, last = 1, price[0]
            for p in price[1:]:
                if p - last >= gap:
                    count += 1; last = p
                    if count == k: return True
            return False
        gaps=[0]
        for i in range(len(price)):
            for j in range(i+1,len(price)):
                gaps.append(price[j]-price[i])
        gaps=sorted(set(gaps)); lo,hi,ans=0,len(gaps)-1,0
        while lo<=hi:
            mid=(lo+hi)//2
            if can(gaps[mid]): ans=gaps[mid]; lo=mid+1
            else: hi=mid-1
        return ans
```

- **Time:** O(n^2 log n) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** binary-search the numeric gap range.
```python
class Solution:
    def maximumTastiness(self, price, k):
        price.sort()
        def can(gap):
            count, last = 1, price[0]
            for p in price[1:]:
                if p - last >= gap:
                    count += 1; last = p
                    if count == k: return True
            return False
        lo,hi,ans=0,(price[-1]-price[0])//(k-1),0
        while lo<=hi:
            mid=(lo+hi)//2
            if can(mid): ans=mid; lo=mid+1
            else: hi=mid-1
        return ans
```

- **Time:** O(n log R) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nR) | O(1) |
| Better | O(n^2 log n) | O(n^2) |
| Optimal | O(n log R) | O(1) |


## Edge Cases & Pitfalls
- Sort prices first.
- Include gap 0 for duplicate prices.
- Move right after a feasible gap.


## Related
- Magnetic Force Between Two Balls
- Aggressive Cows
