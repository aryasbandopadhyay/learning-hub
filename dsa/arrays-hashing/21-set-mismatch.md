# 21. Set Mismatch

- **Difficulty:** Easy
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Bloomberg

## Problem
A set should contain every integer from `1` to `n` exactly once, but one number was duplicated and one number is missing. Given the resulting array, identify both errors.

**Input**
- `nums`: a list of length `n` containing values from `1` to `n`.

**Output**
- A two-element list `[duplicate, missing]`. **This judge compares exactly**, so the duplicated value comes first and the missing value second.

## Constraints
- `2 <= nums.length <= 10^4`
- `1 <= nums[i] <= nums.length`
- Exactly one value is duplicated and exactly one value is missing.

## Examples
```text
Input: nums = [1,2,2,4]
Output: [2,3]
Explanation: The value `2` appears twice, and `3` is absent from `{1,2,3,4}`, so return `[2,3]`.
```

## Understanding & Intuition
This combines duplicate and missing detection. Counting is direct; sign marking uses the value range as an indexable visited map.

## Approach 1 — Naive / Brute Force
**Idea:** Count every number from 1 to n.
```python
class Solution:
    def findErrorNums(self, nums: list[int]) -> list[int]:
        dup = miss = -1
        for x in range(1, len(nums)+1):
            c = sum(1 for y in nums if y == x)
            if c == 0:
                miss = x
            elif c == 2:
                dup = x
        return [dup, miss]
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use a set for duplicate and sums for missing.
```python
class Solution:
    def findErrorNums(self, nums: list[int]) -> list[int]:
        seen, dup = set(), -1
        for x in nums:
            if x in seen:
                dup = x
            seen.add(x)
        n = len(nums)
        miss = n * (n + 1) // 2 - (sum(nums) - dup)
        return [dup, miss]
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Negate visited index slots to find both values.
```python
class Solution:
    def findErrorNums(self, nums: list[int]) -> list[int]:
        dup = -1
        for x in nums:
            i = abs(x) - 1
            if nums[i] < 0:
                dup = abs(x)
            else:
                nums[i] = -nums[i]
        miss = next(i + 1 for i, x in enumerate(nums) if x > 0)
        return [dup, miss]
```
- **Time:** O(n) — **Space:** O(1) extra

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) extra |

## Edge Cases & Pitfalls
- Sign marking mutates nums.
- Use abs after negation.
- Exactly one duplicate and one missing.

## Related
- Find All Duplicates in an Array
- Missing Number
