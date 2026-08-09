# 12. Find K-th Smallest Pair Distance

- **Difficulty:** Hard
- **Pattern:** two pointers
- **Asked at:** Google, Amazon, Facebook

## Problem
Implement `smallestDistancePair` for **Find K-th Smallest Pair Distance**. Given `nums` and `k`, return the `k`-th smallest absolute difference among all index pairs.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `nums`: list; input integer list.
- `k`: integer; required count, rank, or operation limit as defined above.

**Output**
- A single integer.

## Constraints
- `2 <= len(nums) <= 10^4`, `1 <= k <= n*(n-1)/2`, `0 <= nums[i] <= 10^6`

## Examples
```text
Input: nums = [1,3,1], k = 1
Output: 0
Explanation: Pair distances are [0,2,2]. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
After sorting, the number of pairs within a candidate distance is monotonic. Binary search the answer and count pairs with two pointers. The key is to maintain deterministic pointer movement so all approaches return the same unique answer.

## Approach 1 — Naive / Brute Force
**Idea:** Use the most direct exhaustive or simulation-based method.
```python
class Solution:
    def smallestDistancePair(self, nums: list[int], k: int) -> int:
        d=[]
        for i in range(len(nums)):
            for j in range(i+1,len(nums)):
                d.append(abs(nums[i]-nums[j]))
        d.sort()
        return d[k-1]
```
- **Time:** O(n^2 log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Add sorting, precomputation, binary search, or auxiliary structures to reduce repeated work.
```python
class Solution:
    def smallestDistancePair(self, nums: list[int], k: int) -> int:
        nums=sorted(nums)
        def count(limit):
            total=0
            for i in range(len(nums)):
                lo,hi=i+1,len(nums)
                while lo < hi:
                    mid=(lo+hi)//2
                    if nums[mid]-nums[i] <= limit: lo=mid+1
                    else: hi=mid
                total += lo-i-1
            return total
        lo,hi=0,nums[-1]-nums[0]
        while lo < hi:
            mid=(lo+hi)//2
            if count(mid) >= k: hi=mid
            else: lo=mid+1
        return lo
```
- **Time:** O(n log n log W) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use the strongest two-pointer invariant for this problem.
```python
class Solution:
    def smallestDistancePair(self, nums: list[int], k: int) -> int:
        nums=sorted(nums)
        def count(limit):
            total=left=0
            for right,x in enumerate(nums):
                while x-nums[left] > limit: left += 1
                total += right-left
            return total
        lo,hi=0,nums[-1]-nums[0]
        while lo < hi:
            mid=(lo+hi)//2
            if count(mid) >= k: hi=mid
            else: lo=mid+1
        return lo
```
- **Time:** O(n log W) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2 log n) | O(n) |
| Better | O(n log n log W) | O(n) |
| Optimal | O(n log W) | O(1) |

## Edge Cases & Pitfalls
- Duplicate values can produce distance 0.
- k is one-indexed.
- Count distances less than or equal to the candidate.

## Related
- Find K Closest Elements
- Median of Two Sorted Arrays
