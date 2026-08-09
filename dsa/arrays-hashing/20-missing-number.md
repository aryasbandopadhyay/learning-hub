# 20. Missing Number

- **Difficulty:** Easy
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given an array `nums` containing `n` distinct numbers from the range `0..n`, return the only number in that range that is missing.

**Input**
- `nums`: a list of distinct integers from `0` through `nums.length`, with one omitted.

**Output**
- The missing integer.

## Constraints
- `n == nums.length`
- `1 <= n <= 10^4`
- `0 <= nums[i] <= n`
- All values in `nums` are distinct.

## Examples
```text
Input: nums = [3,0,1]
Output: 2
Explanation: For `n = 3`, the full range is `0,1,2,3`; the input contains `0,1,3`, so `2` is missing.
```

## Understanding & Intuition
The complete range has a known sum. Set membership is straightforward; arithmetic sum or XOR gives constant extra space.

## Approach 1 — Naive / Brute Force
**Idea:** Scan the array for every candidate.
```python
class Solution:
    def missingNumber(self, nums: list[int]) -> int:
        for x in range(len(nums) + 1):
            if x not in nums:
                return x
        return -1
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use a set for membership.
```python
class Solution:
    def missingNumber(self, nums: list[int]) -> int:
        vals = set(nums)
        for x in range(len(nums) + 1):
            if x not in vals:
                return x
        return -1
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Subtract actual sum from expected range sum.
```python
class Solution:
    def missingNumber(self, nums: list[int]) -> int:
        n = len(nums)
        return n * (n + 1) // 2 - sum(nums)
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Missing value can be 0 or n.
- Values are distinct.
- Use integer arithmetic.

## Related
- First Missing Positive
- Set Mismatch
