# 12. Maximum Sum of Two Non-Overlapping Subarrays

- **Difficulty:** Medium
- **Pattern:** Arrays & Prefix Sums
- **Asked at:** Google, Facebook, Amazon

## Problem
You are given an integer array `nums` and two lengths `firstLen` and `secondLen`.

Choose two non-overlapping contiguous subarrays, one of each length. They may appear in either order. Return the largest possible combined sum.

**Input**
- `nums`: a list of integers.
- `firstLen`: the length of one chosen subarray.
- `secondLen`: the length of the other chosen subarray.

**Output**
- The maximum sum of two non-overlapping subarrays with the required lengths.

## Constraints
- `1 <= nums.length <= 1000`
- `0 <= nums[i] <= 1000`
- `1 <= firstLen, secondLen <= nums.length`
- `firstLen + secondLen <= nums.length`

## Examples
```text
Input: nums = [0,6,5,2,2,5,1,9,4], firstLen = 1, secondLen = 2
Output: 20
Explanation: Choose `[9]` as the length-1 subarray and `[6,5]` as the length-2 subarray, for total `20`.
```

## Understanding & Intuition
The two windows can appear in either order, so evaluate both orderings. Prefix sums make each fixed-length window sum O(1). While scanning the second window, track the best compatible first window seen so far.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate every pair of starts and skip overlapping windows.
```python
class Solution:
    def maxSumTwoNoOverlap(self, nums: list[int], firstLen: int, secondLen: int) -> int:
        n = len(nums)
        prefix = [0]
        for x in nums:
            prefix.append(prefix[-1] + x)
        def window(i, length):
            return prefix[i + length] - prefix[i]
        best = 0
        for i in range(n - firstLen + 1):
            for j in range(n - secondLen + 1):
                if i + firstLen <= j or j + secondLen <= i:
                    best = max(best, window(i, firstLen) + window(j, secondLen))
        return best
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Compute the best answer when a length `a` window comes before a length `b` window, then try both orders.
```python
class Solution:
    def maxSumTwoNoOverlap(self, nums: list[int], firstLen: int, secondLen: int) -> int:
        prefix = [0]
        for x in nums:
            prefix.append(prefix[-1] + x)
        n = len(nums)
        def window(i, length):
            return prefix[i + length] - prefix[i]
        def solve(a, b):
            best = 0
            for j in range(a, n - b + 1):
                left_best = 0
                for i in range(0, j - a + 1):
                    left_best = max(left_best, window(i, a))
                best = max(best, left_best + window(j, b))
            return best
        return max(solve(firstLen, secondLen), solve(secondLen, firstLen))
```
- **Time:** O(n^2) — **Space:** O(n)

## Approach 3 — Optimal
**Idea:** Scan once per ordering, maintaining the best earlier window sum in O(1).
```python
class Solution:
    def maxSumTwoNoOverlap(self, nums: list[int], firstLen: int, secondLen: int) -> int:
        prefix = [0]
        for x in nums:
            prefix.append(prefix[-1] + x)
        n = len(nums)
        def window(i, length):
            return prefix[i + length] - prefix[i]
        def solve(a, b):
            best_a = window(0, a)
            best = 0
            for j in range(a, n - b + 1):
                best_a = max(best_a, window(j - a, a))
                best = max(best, best_a + window(j, b))
            return best
        return max(solve(firstLen, secondLen), solve(secondLen, firstLen))
```
- **Time:** O(n) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n^2) | O(n) |
| Better | O(n^2) | O(n) |
| Optimal | O(n) | O(n) |

## Edge Cases & Pitfalls
- Evaluate both possible orderings of the two lengths.
- Non-overlap means one window must end before the other starts.
- Prefix indices are easiest when the prefix array has a leading zero.

## Related
- Maximum Sum of 3 Non-Overlapping Subarrays
- Maximum Points You Can Obtain from Cards
