# 19. Find the Duplicate Number

- **Difficulty:** Medium
- **Pattern:** Arrays & Hashing
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given an array `nums` of length `n + 1` containing integers from `1` to `n`, find the one repeated number. Exactly one distinct value is duplicated, though it may appear more than twice.

**Input**
- `nums`: a list of `n + 1` integers, each in the range `1..n`.

**Output**
- The duplicated integer.

## Constraints
- `1 <= n <= 10^5`
- `nums.length == n + 1`
- `1 <= nums[i] <= n`
- Exactly one distinct number is repeated.

## Examples
```text
Input: nums = [1,3,4,2,2]
Output: 2
Explanation: The value `2` appears twice, while the other values appear once, so the duplicate is `2`.
```

## Understanding & Intuition
The range makes `nums[i]` a pointer into the array. A set detects repeats; Floyd cycle detection finds the duplicate as the cycle entrance.

## Approach 1 — Naive / Brute Force
**Idea:** Count each possible value.
```python
class Solution:
    def findDuplicate(self, nums: list[int]) -> int:
        for x in range(1, len(nums)):
            if sum(1 for y in nums if y == x) > 1:
                return x
        return -1
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use a set to detect the repeated value.
```python
class Solution:
    def findDuplicate(self, nums: list[int]) -> int:
        seen = set()
        for x in nums:
            if x in seen:
                return x
            seen.add(x)
        return -1
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Run Floyd cycle detection on value pointers.
```python
class Solution:
    def findDuplicate(self, nums: list[int]) -> int:
        slow = nums[0]
        fast = nums[nums[0]]
        while slow != fast:
            slow = nums[slow]
            fast = nums[nums[fast]]
        slow = 0
        while slow != fast:
            slow = nums[slow]
            fast = nums[fast]
        return slow
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Values must be valid indices.
- Duplicate may appear more than twice.
- Floyd does not mutate input.

## Related
- Linked List Cycle II
- Set Mismatch
