# 08. Remove Duplicates from Sorted Array II

- **Difficulty:** Medium
- **Pattern:** two pointers
- **Asked at:** Facebook, Amazon, Microsoft

## Problem
Given sorted `nums`, remove extra duplicates so every distinct value appears at most twice. Return the valid mutated prefix as a list.

Constraints: `0 <= len(nums) <= 3 * 10^4`, `-10^4 <= nums[i] <= 10^4`.

## Examples
```text
Input: nums = [0,0,1,1,1,1,2,3,3]
Output: [0,0,1,1,2,3,3]
Explanation: Extra copies after the second occurrence are removed.
```

## Understanding & Intuition
All duplicates are adjacent in a sorted list. A write pointer keeps a value if it differs from the value two kept positions back. The key is to maintain deterministic pointer movement so all approaches return the same unique answer.

## Approach 1 — Naive / Brute Force
**Idea:** Use the most direct exhaustive or simulation-based method.
```python
class Solution:
    def removeDuplicates(self, nums: list[int]) -> list[int]:
        counts={}
        for x in nums: counts[x]=counts.get(x,0)+1
        out=[]
        for x in sorted(counts): out.extend([x]*min(2,counts[x]))
        nums[:len(out)] = out
        return nums[:len(out)]
```
- **Time:** O(n log n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Add sorting, precomputation, binary search, or auxiliary structures to reduce repeated work.
```python
class Solution:
    def removeDuplicates(self, nums: list[int]) -> list[int]:
        out=[]; i=0
        while i < len(nums):
            j=i
            while j < len(nums) and nums[j] == nums[i]: j += 1
            out.extend([nums[i]] * min(2, j-i)); i=j
        nums[:len(out)] = out
        return nums[:len(out)]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use the strongest two-pointer invariant for this problem.
```python
class Solution:
    def removeDuplicates(self, nums: list[int]) -> list[int]:
        write=0
        for x in nums:
            if write < 2 or x != nums[write-2]:
                nums[write] = x; write += 1
        return nums[:write]
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n log n) | O(n) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Return only the valid prefix.
- Length 0, 1, or 2 is already valid.
- The input sorted order enables O(1) extra space.

## Related
- Remove Duplicates from Sorted Array
- Remove Element
