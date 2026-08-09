# 13. Maximum Score of a Good Subarray

- **Difficulty:** Hard
- **Pattern:** two pointers
- **Asked at:** Google, Amazon, Facebook

## Problem
Implement `maximumScore` for **Maximum Score of a Good Subarray**. Given `nums` and index `k`, a good subarray contains `k` and has score `min(subarray) * length`. Return the maximum score.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `nums`: list; input integer list.
- `k`: integer; required count, rank, or operation limit as defined above.

**Output**
- A single integer.

## Constraints
- `1 <= len(nums) <= 10^5`, `0 <= k < len(nums)`, `1 <= nums[i] <= 2 * 10^4`

## Examples
```text
Input: nums = [1,4,3,7,4,5], k = 3
Output: 15
Explanation: [4,3,7,4,5] has minimum 3 and length 5. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Every good subarray expands from index k. Expanding toward the larger neighboring value preserves the highest possible minimum for the next length. The key is to maintain deterministic pointer movement so all approaches return the same unique answer.

## Approach 1 — Naive / Brute Force
**Idea:** Use the most direct exhaustive or simulation-based method.
```python
class Solution:
    def maximumScore(self, nums: list[int], k: int) -> int:
        best=0
        for l in range(k+1):
            for r in range(k,len(nums)):
                mn=nums[l]
                for i in range(l,r+1): mn=min(mn,nums[i])
                best=max(best,mn*(r-l+1))
        return best
```
- **Time:** O(n^3) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Add sorting, precomputation, binary search, or auxiliary structures to reduce repeated work.
```python
class Solution:
    def maximumScore(self, nums: list[int], k: int) -> int:
        n=len(nums); left=[-1]*n; st=[]
        for i,x in enumerate(nums):
            while st and nums[st[-1]] >= x: st.pop()
            left[i]=st[-1] if st else -1; st.append(i)
        right=[n]*n; st=[]
        for i in range(n-1,-1,-1):
            while st and nums[st[-1]] >= nums[i]: st.pop()
            right[i]=st[-1] if st else n; st.append(i)
        best=0
        for i,x in enumerate(nums):
            if left[i] < k < right[i]: best=max(best,x*(right[i]-left[i]-1))
        return best
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use the strongest two-pointer invariant for this problem.
```python
class Solution:
    def maximumScore(self, nums: list[int], k: int) -> int:
        n=len(nums); l=r=k; mn=nums[k]; best=mn
        while l > 0 or r < n-1:
            if l == 0: r += 1
            elif r == n-1: l -= 1
            elif nums[l-1] < nums[r+1]: r += 1
            else: l -= 1
            mn=min(mn,nums[l],nums[r])
            best=max(best,mn*(r-l+1))
        return best
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- The subarray must contain k.
- Single-element arrays return nums[k].
- Choose the larger next neighbor during greedy expansion.

## Related
- Largest Rectangle in Histogram
- Container With Most Water
