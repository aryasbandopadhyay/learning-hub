# 12. Find All Duplicates in an Array

- **Difficulty:** Medium
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given length `n` array with values in `[1,n]`, where each appears once or twice, return all values appearing twice. Aim for constant extra space excluding output.

## Examples
```text
Input: nums = [4,3,2,7,8,2,3,1]
Output: [2,3]
Explanation: 2 and 3 appear twice.
```

## Understanding & Intuition
The value range maps values to indices. A set is simple; sign marking uses the input array itself as visited state.

## Approach 1 — Naive / Brute Force
**Idea:** Count each value by scanning.
```python
class Solution:
    def findDuplicates(self, nums: list[int]) -> list[int]:
        ans = []
        for x in range(1, len(nums) + 1):
            if sum(1 for y in nums if y == x) == 2:
                ans.append(x)
        return ans
```
- **Time:** O(n^2) — **Space:** O(1) extra

## Approach 2 — Better
**Idea:** Use a seen set and record repeats.
```python
class Solution:
    def findDuplicates(self, nums: list[int]) -> list[int]:
        seen, ans = set(), []
        for x in nums:
            if x in seen:
                ans.append(x)
            else:
                seen.add(x)
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Negate the slot for each seen value.
```python
class Solution:
    def findDuplicates(self, nums: list[int]) -> list[int]:
        ans = []
        for x in nums:
            i = abs(x) - 1
            if nums[i] < 0:
                ans.append(abs(x))
            else:
                nums[i] = -nums[i]
        return ans
```
- **Time:** O(n) — **Space:** O(1) extra

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) extra |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) extra |

## Edge Cases & Pitfalls
- Optimal mutates `nums`.
- Use abs after negations.
- Requires values in [1,n].

## Related
- First Missing Positive
- Set Mismatch
