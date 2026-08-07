# 10. First Missing Positive

- **Difficulty:** Hard
- **Pattern:** Arrays / Index Placement
- **Asked at:** Salesforce, Amazon, Google

## Problem
Return the smallest missing positive integer from an unsorted array in O(n) time and O(1) extra space.

## Examples
```text
Input: nums = [3,4,-1,1]
Output: 2
Explanation: 1 exists and 2 is missing.
```

## Understanding & Intuition
Only values `1..n` matter. Place each value `x` at index `x-1`; the first mismatch reveals the answer.

## Approach 1 — Naive / Brute Force
**Idea:** Try each positive candidate and scan for it.
```python
class Solution:
    def firstMissingPositive(self, nums: list[int]) -> int:
        candidate = 1
        while True:
            if candidate not in nums:
                return candidate
            candidate += 1
```
- **Time:** O(n^2) — **Space:** O(1)

## Approach 2 — Better
**Idea:** Use a set for O(1) membership checks.
```python
class Solution:
    def firstMissingPositive(self, nums: list[int]) -> int:
        seen = set(nums)
        for x in range(1, len(nums) + 2):
            if x not in seen:
                return x
        return len(nums) + 1
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Cyclically place each valid value into its matching index.
```python
class Solution:
    def firstMissingPositive(self, nums: list[int]) -> int:
        n = len(nums)
        for i in range(n):
            while 1 <= nums[i] <= n and nums[nums[i] - 1] != nums[i]:
                j = nums[i] - 1
                nums[i], nums[j] = nums[j], nums[i]
        for i, x in enumerate(nums):
            if x != i + 1:
                return i + 1
        return n + 1
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Ignore values outside `1..n`.
- Avoid infinite swaps with duplicate values.
- If all positions match, answer is `n + 1`.

## Related
- Missing Number
- Find All Numbers Disappeared in an Array
