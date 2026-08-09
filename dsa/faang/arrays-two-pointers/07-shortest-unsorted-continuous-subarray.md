# 07. Shortest Unsorted Continuous Subarray

- **Difficulty:** Medium
- **Pattern:** two pointers
- **Asked at:** Amazon, Google, Facebook

## Problem
Implement `findUnsortedSubarray` for **Shortest Unsorted Continuous Subarray**. Given `nums`, return the length of the shortest continuous subarray that can be sorted so the whole list becomes sorted. Return `0` if already sorted.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `nums`: list; input integer list.

**Output**
- A single integer.

## Constraints
- `0 <= len(nums) <= 10^4`, `-10^5 <= nums[i] <= 10^5`

## Examples
```text
Input: nums = [2,6,4,8,10,9,15]
Output: 5
Explanation: Sorting [6,4,8,10,9] fixes the array. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
The unsorted window contains values crossing their sorted boundaries. Running maxima and minima reveal the right and left boundaries. The key is to maintain deterministic pointer movement so all approaches return the same unique answer.

## Approach 1 — Naive / Brute Force
**Idea:** Use the most direct exhaustive or simulation-based method.
```python
class Solution:
    def findUnsortedSubarray(self, nums: list[int]) -> int:
        n=len(nums)
        if nums == sorted(nums): return 0
        best=n
        for l in range(n):
            for r in range(l,n):
                a=nums[:l]+sorted(nums[l:r+1])+nums[r+1:]
                if a == sorted(a): best=min(best,r-l+1)
        return best
```
- **Time:** O(n^3 log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Add sorting, precomputation, binary search, or auxiliary structures to reduce repeated work.
```python
class Solution:
    def findUnsortedSubarray(self, nums: list[int]) -> int:
        s=sorted(nums)
        l,r=0,len(nums)-1
        while l < len(nums) and nums[l] == s[l]: l += 1
        while r > l and nums[r] == s[r]: r -= 1
        return 0 if l == len(nums) else r-l+1
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use the strongest two-pointer invariant for this problem.
```python
class Solution:
    def findUnsortedSubarray(self, nums: list[int]) -> int:
        right=-1; mx=-10**20
        for i,x in enumerate(nums):
            if x < mx: right=i
            else: mx=x
        left=len(nums); mn=10**20
        for i in range(len(nums)-1,-1,-1):
            if nums[i] > mn: left=i
            else: mn=nums[i]
        return 0 if right == -1 else right-left+1
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3 log n) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Already sorted arrays return 0.
- Duplicates should not expand the window unless order is violated.
- The window may touch either end.

## Related
- Next Permutation
- Sort Colors
