# 12. Find All Duplicates in an Array

- **Difficulty:** Medium
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given an integer array `nums` of length `n`, where every value is in `1..n`, return all values that appear exactly twice. Each value appears once or twice.

**Input**
- `nums`: a list of integers with values from `1` to `nums.length`.

**Output**
- A list of duplicated values. **This judge compares exactly**; return duplicates in the order they are discovered while scanning `nums` from left to right.

## Constraints
- `n == nums.length`
- `1 <= n <= 10^5`
- `1 <= nums[i] <= n`
- Each integer appears once or twice.

## Examples
```text
Input: nums = [4,3,2,7,8,2,3,1]
Output: [2,3]
Explanation: Scanning left to right, `2` is the first value encountered for a second time and `3` is the next, so the output is `[2,3]`.
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
