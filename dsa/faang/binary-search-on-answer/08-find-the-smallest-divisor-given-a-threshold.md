# 08. Find the Smallest Divisor Given a Threshold

- **Difficulty:** Medium
- **Pattern:** Binary Search on the Answer
- **Asked at:** Amazon, Microsoft, Bloomberg

## Problem
Choose a positive divisor so the sum of rounded-up divisions over `nums` is at most `threshold`. Return the smallest such divisor. Constraints: `1 <= len(nums) <= 5 * 10^4`, `len(nums) <= threshold`.

## Examples
```text
Input: nums = [1, 2, 5, 9], threshold = 6
Output: 5
Explanation: Divisor 5 gives score 5, while divisor 4 gives score 7.
```

## Understanding & Intuition
Scores decrease as the divisor grows. Thus feasible divisors form a suffix, and we need the first feasible value.

## Approach 1 — Naive / Brute Force
**Idea:** test divisors in order.
```python
class Solution:
    def smallestDivisor(self, nums, threshold):
        def score(d):
            total = 0
            for x in nums:
                total += (x + d - 1) // d
            return total
        for d in range(1,max(nums)+1):
            if score(d) <= threshold: return d
        return max(nums)
```

- **Time:** O(nM) — **Space:** O(1)

## Approach 2 — Better
**Idea:** binary-search with full score calculations.
```python
class Solution:
    def smallestDivisor(self, nums, threshold):
        def score(d):
            total = 0
            for x in nums:
                total += (x + d - 1) // d
            return total
        lo,hi,ans=1,max(nums),max(nums)
        while lo<=hi:
            mid=(lo+hi)//2
            if score(mid)<=threshold: ans=mid; hi=mid-1
            else: lo=mid+1
        return ans
```

- **Time:** O(n log M) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** start from an average lower bound and stop early.
```python
class Solution:
    def smallestDivisor(self, nums, threshold):
        lo=max(1,(sum(nums)+threshold-1)//threshold); hi=max(nums)
        def can(d):
            total=0
            for x in nums:
                total += (x + d - 1) // d
                if total > threshold: return False
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
- Ceiling division must be exact.
- `max(nums)` is feasible because `threshold >= len(nums)`.
- Search for the first feasible divisor.


## Related
- Minimum Limit of Balls in a Bag
- Minimized Maximum of Products Distributed to Any Store
