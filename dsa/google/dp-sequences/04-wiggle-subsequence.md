# 04. Wiggle Subsequence

- **Difficulty:** Medium
- **Pattern:** DP on sequences / subsequences
- **Asked at:** Google, Facebook, Amazon

## Problem
A wiggle sequence has consecutive differences that strictly alternate between positive and negative. Given `nums`, return the length of the longest wiggle subsequence.
Constraints: `1 <= len(nums) <= 1000`, `0 <= nums[i] <= 1000`.

## Examples
```text
Input: nums = [1,7,4,9,2,5]
Output: 6
Explanation: The whole array alternates up, down, up, down, up.
```

## Understanding & Intuition
Only the sign of the last chosen difference matters. A new larger value can extend a sequence that last moved down, and a new smaller value can extend one that last moved up.

## Approach 1 — Naive / Brute Force
**Idea:** Recursively keep the last index and required next direction: `0` any, `1` up, `-1` down.
```python
class Solution:
    def wiggleMaxLength(self, nums: list[int]) -> int:
        n = len(nums)
        def dfs(i: int, last: int, need: int) -> int:
            if i == n:
                return 0
            best = dfs(i + 1, last, need)
            if last == -1:
                best = max(best, 1 + dfs(i + 1, i, 0))
            else:
                diff = nums[i] - nums[last]
                if (need == 0 and diff != 0) or (need == 1 and diff > 0) or (need == -1 and diff < 0):
                    best = max(best, 1 + dfs(i + 1, i, -1 if diff > 0 else 1))
            return best
        return dfs(0, -1, 0)
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** For each index, store the best wiggle length ending there with a positive or negative last difference.
```python
class Solution:
    def wiggleMaxLength(self, nums: list[int]) -> int:
        n = len(nums)
        up = [1] * n
        down = [1] * n
        for i in range(n):
            for j in range(i):
                if nums[i] > nums[j]:
                    up[i] = max(up[i], down[j] + 1)
                elif nums[i] < nums[j]:
                    down[i] = max(down[i], up[j] + 1)
        return max(max(up), max(down))
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Compress the DP to two scalars because only the previous best up/down lengths matter.
```python
class Solution:
    def wiggleMaxLength(self, nums: list[int]) -> int:
        up = down = 1
        for i in range(1, len(nums)):
            if nums[i] > nums[i - 1]:
                up = down + 1
            elif nums[i] < nums[i - 1]:
                down = up + 1
        return max(up, down)
```
- **Time:** O(n) — **Space:** O(1)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n) | O(1) |

## Edge Cases & Pitfalls
- Equal adjacent values do not create a wiggle difference.
- A single element is a wiggle sequence of length 1.

## Related
- Longest Increasing Subsequence
- Best Time to Buy and Sell Stock
