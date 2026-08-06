# 07. Minimum Limit of Balls in a Bag

- **Difficulty:** Medium
- **Pattern:** Binary Search on the Answer
- **Asked at:** Amazon, Google, Apple

## Problem
Split bags at most `maxOperations` times; each split turns one positive bag into two positive bags. Minimize the maximum balls in any bag. Constraints: `1 <= len(nums) <= 10^5`, `1 <= nums[i] <= 10^9`.

## Examples
```text
Input: nums = [9], maxOperations = 2
Output: 3
Explanation: Split 9 into 6+3, then 6 into 3+3.
```

## Understanding & Intuition
For penalty `p`, bag `x` needs `(x - 1) // p` operations. Larger penalties are never harder to satisfy.

## Approach 1 — Naive / Brute Force
**Idea:** try every penalty.
```python
class Solution:
    def minimumSize(self, nums, maxOperations):
        def ops(p):
            total = 0
            for x in nums:
                total += (x - 1) // p
            return total
        for p in range(1,max(nums)+1):
            if ops(p) <= maxOperations: return p
        return max(nums)
```

- **Time:** O(nM) — **Space:** O(1)

## Approach 2 — Better
**Idea:** binary-search with full operation counts.
```python
class Solution:
    def minimumSize(self, nums, maxOperations):
        def ops(p):
            total = 0
            for x in nums:
                total += (x - 1) // p
            return total
        lo,hi,ans=1,max(nums),max(nums)
        while lo<=hi:
            mid=(lo+hi)//2
            if ops(mid)<=maxOperations: ans=mid; hi=mid-1
            else: lo=mid+1
        return ans
```

- **Time:** O(n log M) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** use a total-balls lower bound and early stop.
```python
class Solution:
    def minimumSize(self, nums, maxOperations):
        bags=len(nums)+maxOperations
        lo=max(1,(sum(nums)+bags-1)//bags); hi=max(nums)
        def can(p):
            used=0
            for x in nums:
                used += (x - 1) // p
                if used > maxOperations: return False
            return True
        while lo < hi:
            mid=(lo+hi)//2
            if can(mid): hi=mid
            else: lo=mid+1
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
- Use `(x - 1) // p`, not `x // p`.
- Each split increases bag count by one.
- The lower bound does not replace feasibility.


## Related
- Minimized Maximum of Products Distributed to Any Store
- Smallest Divisor Given a Threshold
