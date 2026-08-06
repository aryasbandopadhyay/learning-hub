# 08. Next Greater Element I

- **Difficulty:** Easy
- **Pattern:** Stack
- **Asked at:** Amazon, Bloomberg, Google, Microsoft

## Problem
Given distinct arrays `nums1` and `nums2` where `nums1` is a subset of `nums2`, return the next greater element in `nums2` for each value in `nums1`, or `-1` if none exists. Constraints: `1 <= len(nums1) <= len(nums2) <= 1000`.

## Examples
```text
Input: nums1 = [4,1,2], nums2 = [1,3,4,2]
Output: [-1,3,-1]
Explanation: 1's next greater value is 3; 4 and 2 have none.
```

## Understanding & Intuition
For each number, we need the first greater value to its right in `nums2`. A decreasing stack holds values waiting for a greater value. When a larger number appears, it resolves smaller stack values.

## Approach 1 — Naive / Brute Force
**Idea:** For every query value, find it in `nums2` and scan right.
```python
class Solution:
    def nextGreaterElement(self, nums1: list[int], nums2: list[int]) -> list[int]:
        ans = []
        for x in nums1:
            idx = nums2.index(x)
            greater = -1
            for y in nums2[idx + 1:]:
                if y > x:
                    greater = y
                    break
            ans.append(greater)
        return ans
```
- **Time:** O(mn) — **Space:** O(1) extra

## Approach 2 — Better
**Idea:** Store indexes of `nums2` values to avoid repeatedly searching for each query value.
```python
class Solution:
    def nextGreaterElement(self, nums1: list[int], nums2: list[int]) -> list[int]:
        pos = {x: i for i, x in enumerate(nums2)}
        ans = []
        for x in nums1:
            greater = -1
            for i in range(pos[x] + 1, len(nums2)):
                if nums2[i] > x:
                    greater = nums2[i]
                    break
            ans.append(greater)
        return ans
```
- **Time:** O(mn) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Build a next-greater map for all values in one monotonic stack pass.
```python
class Solution:
    def nextGreaterElement(self, nums1: list[int], nums2: list[int]) -> list[int]:
        next_greater = {}
        stack = []
        for x in nums2:
            # x is the first greater value for all smaller values on top.
            while stack and x > stack[-1]:
                next_greater[stack.pop()] = x
            stack.append(x)
        return [next_greater.get(x, -1) for x in nums1]
```
- **Time:** O(n + m) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(mn) | O(1) |
| Better | O(mn) | O(n) |
| Optimal | O(n + m) | O(n) |

## Edge Cases & Pitfalls
- `nums2` values are distinct, so a value-to-answer map is safe.
- A remaining stack value has answer `-1`.
- "Next greater" means first greater to the right, not maximum to the right.

## Related
- Daily Temperatures
- Next Greater Element II

