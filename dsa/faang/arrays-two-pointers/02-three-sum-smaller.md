# 02. 3Sum Smaller

- **Difficulty:** Medium
- **Pattern:** two pointers
- **Asked at:** Google, Amazon, Facebook

## Problem
Implement `threeSumSmaller` for **3Sum Smaller**. Given `nums` and `target`, return the number of index triples `(i, j, k)` with `i < j < k` and sum strictly less than `target`.

The function should be self-contained: interpret the parameters exactly as described below and return only the requested value.

**Input**
- `nums`: list; input integer list.
- `target`: integer; target value or string.

**Output**
- A single integer.

## Constraints
- `0 <= len(nums) <= 1000`, `-10^5 <= nums[i], target <= 10^5`

## Examples
```text
Input: nums = [-2,0,1,3], target = 2
Output: 2
Explanation: [-2,0,1] and [-2,0,3] are below target. This is the required result for the given input under the rules above.
```

## Understanding & Intuition
After sorting, if a left/right pair is small enough, every smaller right endpoint is also valid. This turns many triples into one count update. The key is to maintain deterministic pointer movement so all approaches return the same unique answer.

## Approach 1 — Naive / Brute Force
**Idea:** Use the most direct exhaustive or simulation-based method.
```python
class Solution:
    def threeSumSmaller(self, nums: list[int], target: int) -> int:
        ans = 0
        n = len(nums)
        for i in range(n):
            for j in range(i + 1, n):
                for k in range(j + 1, n):
                    if nums[i] + nums[j] + nums[k] < target:
                        ans += 1
        return ans
```
- **Time:** O(n^3) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Add sorting, precomputation, binary search, or auxiliary structures to reduce repeated work.
```python
class Solution:
    def threeSumSmaller(self, nums: list[int], target: int) -> int:
        nums = sorted(nums)
        ans = 0
        n = len(nums)
        for i in range(n):
            for j in range(i + 1, n):
                lo, hi = j + 1, n
                limit = target - nums[i] - nums[j]
                while lo < hi:
                    mid = (lo + hi) // 2
                    if nums[mid] < limit:
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
    def threeSumSmaller(self, nums: list[int], target: int) -> int:
        nums = sorted(nums)
        ans = 0
        for i in range(len(nums) - 2):
            left, right = i + 1, len(nums) - 1
            while left < right:
                if nums[i] + nums[left] + nums[right] < target:
                    ans += right - left
                    left += 1
                else:
                    right -= 1
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
- Count index triples, not unique value triples.
- Negative numbers are allowed.
- The comparison is strictly less than target.

## Related
- Valid Triangle Number
- 3Sum
