# 09. Next Permutation

- **Difficulty:** Medium
- **Pattern:** two pointers
- **Asked at:** Google, Facebook, Amazon

## Problem
Implement `nextPermutation` for **Next Permutation**. Given `nums`, rearrange it into the lexicographically next greater permutation; if none exists, return the smallest permutation.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

Return the exact next lexicographic permutation; if none exists, return the ascending permutation.

**Input**
- `nums`: list; input integer list.

**Output**
- A list. Return the exact next lexicographic permutation; if none exists, return the ascending permutation.

## Constraints
- `1 <= len(nums) <= 100`, `-100 <= nums[i] <= 100`

## Examples
```text
Input: nums = [1,2,3]
Output: [1,3,2]
Explanation: [1,3,2] is the next ordering. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
The suffix that is non-increasing is already maximal. Increase the pivot just before it, then make the suffix minimal. The key is to maintain deterministic pointer movement so all approaches return the same unique answer.

## Approach 1 — Naive / Brute Force
**Idea:** Use the most direct exhaustive or simulation-based method.
```python
class Solution:
    def nextPermutation(self, nums: list[int]) -> list[int]:
        original=nums[:]; best=None; n=len(nums)
        for i in range(n):
            for j in range(i+1,n):
                if nums[j] > nums[i]:
                    cand=nums[:]
                    cand[i],cand[j]=cand[j],cand[i]
                    cand[i+1:] = sorted(cand[i+1:])
                    if cand > original and (best is None or cand < best): best=cand
        nums[:] = sorted(nums) if best is None else best
        return nums
```
- **Time:** O(n^3 log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Add sorting, precomputation, binary search, or auxiliary structures to reduce repeated work.
```python
class Solution:
    def nextPermutation(self, nums: list[int]) -> list[int]:
        n=len(nums); i=n-2
        while i >= 0 and nums[i] >= nums[i+1]: i -= 1
        if i < 0:
            nums.sort(); return nums
        j=i+1
        for p in range(i+1,n):
            if nums[p] > nums[i] and nums[p] <= nums[j]: j=p
        nums[i],nums[j]=nums[j],nums[i]
        nums[i+1:] = sorted(nums[i+1:])
        return nums
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use the strongest two-pointer invariant for this problem.
```python
class Solution:
    def nextPermutation(self, nums: list[int]) -> list[int]:
        i=len(nums)-2
        while i >= 0 and nums[i] >= nums[i+1]: i -= 1
        if i >= 0:
            j=len(nums)-1
            while nums[j] <= nums[i]: j -= 1
            nums[i],nums[j]=nums[j],nums[i]
        l,r=i+1,len(nums)-1
        while l < r:
            nums[l],nums[r]=nums[r],nums[l]; l += 1; r -= 1
        return nums
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3 log n) | O(n) |
| Better | O(n log n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Descending input wraps to ascending.
- Duplicates require choosing the smallest greater suffix value.
- Return the mutated list.

## Related
- Permutations II
- Shortest Unsorted Continuous Subarray
