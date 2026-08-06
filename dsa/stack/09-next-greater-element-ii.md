# 09. Next Greater Element II

- **Difficulty:** Medium
- **Pattern:** Stack
- **Asked at:** Amazon, Google, Meta, Microsoft

## Problem
Given a circular array `nums`, return the next greater number for every element, or `-1` if it does not exist. Constraints: `1 <= len(nums) <= 10^4`, `-10^9 <= nums[i] <= 10^9`.

## Examples
```text
Input: nums = [1,2,1]
Output: [2,-1,2]
Explanation: The last 1 wraps around and finds 2.
```

## Understanding & Intuition
Circularity means each index can look through at most one full wrap. A monotonic stack still works if we simulate two passes. Only unresolved indices are stored.

## Approach 1 — Naive / Brute Force
**Idea:** For each index, scan the next `n - 1` circular positions.
```python
class Solution:
    def nextGreaterElements(self, nums: list[int]) -> list[int]:
        n = len(nums)
        ans = [-1] * n
        for i in range(n):
            for step in range(1, n):
                j = (i + step) % n
                if nums[j] > nums[i]:
                    ans[i] = nums[j]
                    break
        return ans
```
- **Time:** O(n^2) — **Space:** O(1) extra

## Approach 2 — Better
**Idea:** Duplicate the array conceptually and run a stack over `2n` indices.
```python
class Solution:
    def nextGreaterElements(self, nums: list[int]) -> list[int]:
        n = len(nums)
        ans = [-1] * n
        stack = []
        for i in range(2 * n):
            idx = i % n
            while stack and nums[idx] > nums[stack[-1]]:
                ans[stack.pop()] = nums[idx]
            # Push each original index once to avoid duplicate work.
            if i < n:
                stack.append(idx)
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Traverse indices backward twice so the stack stores candidate values instead of unresolved indices.
```python
class Solution:
    def nextGreaterElements(self, nums: list[int]) -> list[int]:
        n = len(nums)
        ans = [-1] * n
        stack = []
        for i in range(2 * n - 1, -1, -1):
            x = nums[i % n]
            while stack and stack[-1] <= x:
                stack.pop()
            if i < n and stack:
                ans[i] = stack[-1]
            stack.append(x)
        return ans
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(1) |
| Better | O(n) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Equal values are not greater.
- Simulate exactly two passes; more is unnecessary.
- Fill answers only for original indices, not virtual duplicate indices.

## Related
- Next Greater Element I
- Daily Temperatures

