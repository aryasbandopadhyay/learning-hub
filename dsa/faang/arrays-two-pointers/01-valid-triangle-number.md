# 01. Valid Triangle Number

- **Difficulty:** Medium
- **Pattern:** two pointers
- **Asked at:** Amazon, Google, Facebook

## Problem
Implement `triangleNumber` for **Valid Triangle Number**. Given `nums`, return the number of index triples that can form a valid triangle. A triangle is valid when the sum of the two smaller sides is strictly greater than the largest side.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `nums`: list; input integer list.

**Output**
- A single integer.

## Constraints
- `0 <= len(nums) <= 1000`, `0 <= nums[i] <= 10^6`

## Examples
```text
Input: nums = [2,2,3,4]
Output: 3
Explanation: The valid triples are (2,3,4) twice and (2,2,3) once. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
Sorting leaves only one inequality to check against the largest side. Two pointers count many valid pairs at once. The key is to maintain deterministic pointer movement so all approaches return the same unique answer.

## Approach 1 — Naive / Brute Force
**Idea:** Use the most direct exhaustive or simulation-based method.
```python
class Solution:
    def triangleNumber(self, nums: list[int]) -> int:
        ans = 0
        n = len(nums)
        for i in range(n):
            for j in range(i + 1, n):
                for k in range(j + 1, n):
                    a, b, c = sorted([nums[i], nums[j], nums[k]])
                    if a + b > c:
                        ans += 1
        return ans
```
- **Time:** O(n^3) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Add sorting, precomputation, binary search, or auxiliary structures to reduce repeated work.
```python
class Solution:
    def triangleNumber(self, nums: list[int]) -> int:
        nums = sorted(nums)
        ans = 0
        for i in range(len(nums)):
            for j in range(i + 1, len(nums)):
                lo, hi = j + 1, len(nums)
                while lo < hi:
                    mid = (lo + hi) // 2
                    if nums[i] + nums[j] > nums[mid]:
                        lo = mid + 1
                    else:
                        hi = mid
                ans += lo - j - 1
        return ans
```
- **Time:** O(n^2 log n) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Use the strongest two-pointer invariant for this problem.
```python
class Solution:
    def triangleNumber(self, nums: list[int]) -> int:
        nums = sorted(nums)
        ans = 0
        for k in range(len(nums) - 1, 1, -1):
            left, right = 0, k - 1
            while left < right:
                if nums[left] + nums[right] > nums[k]:
                    ans += right - left
                    right -= 1
                else:
                    left += 1
        return ans
```
- **Time:** O(n^2) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^3) | O(n) |
| Better | O(n^2 log n) | O(n) |
| Optimal | O(n^2) | O(1) |

## Edge Cases & Pitfalls
- Zero sides never help.
- The triangle inequality is strict.
- Duplicate values still create different index triples.

## Related
- 3Sum Smaller
- Two Sum II
