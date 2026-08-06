# 10. House Robber IV

- **Difficulty:** Medium
- **Pattern:** Binary Search on the Answer
- **Asked at:** Google, Amazon, Microsoft

## Problem
Rob at least `k` non-adjacent houses from `nums`. A plan capability is the maximum robbed value. Return the minimum possible capability. Constraints: `1 <= len(nums) <= 10^5`, `1 <= k <= (len(nums)+1)//2`.

## Examples
```text
Input: nums = [2, 3, 5, 9], k = 2
Output: 5
Explanation: Rob values 2 and 5.
```

## Understanding & Intuition
For a capability, greedily rob every eligible earliest house and skip its neighbor. Feasibility only improves as capability increases.

## Approach 1 — Naive / Brute Force
**Idea:** scan every value in range.
```python
class Solution:
    def minCapability(self, nums, k):
        def can(cap):
            taken = 0; i = 0
            while i < len(nums):
                if nums[i] <= cap:
                    taken += 1; i += 2
                else:
                    i += 1
            return taken >= k
        for cap in range(min(nums), max(nums)+1):
            if can(cap): return cap
        return max(nums)
```

- **Time:** O(nR) — **Space:** O(1)

## Approach 2 — Better
**Idea:** binary-search distinct house values.
```python
class Solution:
    def minCapability(self, nums, k):
        def can(cap):
            taken = 0; i = 0
            while i < len(nums):
                if nums[i] <= cap:
                    taken += 1; i += 2
                else:
                    i += 1
            if taken == k: return True
            return False
        vals=sorted(set(nums)); lo,hi,ans=0,len(vals)-1,vals[-1]
        while lo<=hi:
            mid=(lo+hi)//2
            if can(vals[mid]): ans=vals[mid]; hi=mid-1
            else: lo=mid+1
        return ans
```

- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** binary-search the numeric capability range.
```python
class Solution:
    def minCapability(self, nums, k):
        def can(cap):
            taken = 0; i = 0
            while i < len(nums):
                if nums[i] <= cap:
                    taken += 1; i += 2
                else:
                    i += 1
            if taken == k: return True
            return False
        lo,hi=min(nums),max(nums)
        while lo<hi:
            mid=(lo+hi)//2
            if can(mid): hi=mid
            else: lo=mid+1
        return lo
```

- **Time:** O(n log R) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nR) | O(1) |
| Better | O(n log n) | O(n) |
| Optimal | O(n log R) | O(1) |


## Edge Cases & Pitfalls
- Greedily taking earliest eligible houses maximizes count.
- Skip the next house after robbing.
- The optimum is one of the house values.


## Related
- House Robber
- Minimize Maximum of Array
