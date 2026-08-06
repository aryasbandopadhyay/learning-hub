# 11. Minimize Maximum of Array

- **Difficulty:** Medium
- **Pattern:** Binary Search on the Answer
- **Asked at:** Google, Amazon, Meta

## Problem
Operation: choose `i > 0`, decrement `nums[i]`, and increment `nums[i-1]`. Return the minimum possible maximum array value. Constraints: `1 <= len(nums) <= 10^5`, `0 <= nums[i] <= 10^9`.

## Examples
```text
Input: nums = [3, 7, 1, 6]
Output: 5
Explanation: The first two elements have average 5, so no bound below 5 is possible.
```

## Understanding & Intuition
Value only moves left, so every prefix must fit within its capacity under a bound. A bound is feasible iff every prefix sum is at most length times bound.

## Approach 1 — Naive / Brute Force
**Idea:** try every bound.
```python
class Solution:
    def minimizeArrayValue(self, nums):
        def can(bound):
            prefix = 0
            for i, x in enumerate(nums):
                prefix += x
                if prefix > (i + 1) * bound:
                    return False
            return True
        for b in range(0,max(nums)+1):
            if can(b): return b
        return max(nums)
```

- **Time:** O(nM) — **Space:** O(1)

## Approach 2 — Better
**Idea:** binary-search the prefix-capacity feasibility.
```python
class Solution:
    def minimizeArrayValue(self, nums):
        def can(bound):
            prefix = 0
            for i, x in enumerate(nums):
                prefix += x
                if prefix > (i + 1) * bound:
                    return False
            return True
        lo,hi,ans=0,max(nums),max(nums)
        while lo<=hi:
            mid=(lo+hi)//2
            if can(mid): ans=mid; hi=mid-1
            else: lo=mid+1
        return ans
```

- **Time:** O(n log M) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** compute the maximum ceiling prefix average directly.
```python
class Solution:
    def minimizeArrayValue(self, nums):
        prefix = ans = 0
        for i, x in enumerate(nums):
            prefix += x
            ans = max(ans, (prefix + i) // (i + 1))
        return ans
```

- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(nM) | O(1) |
| Better | O(n log M) | O(1) |
| Optimal | O(n) | O(1) |


## Edge Cases & Pitfalls
- Values cannot move right.
- Check every prefix, not only the whole array.
- Use ceiling division for prefix averages.


## Related
- House Robber IV
- Split Array Largest Sum
