# 01. Number of Longest Increasing Subsequence

- **Difficulty:** Medium
- **Pattern:** DP on sequences / subsequences
- **Asked at:** Google, Amazon, Facebook

## Problem
Given an integer array `nums`, return the number of longest strictly increasing subsequences. A subsequence keeps the original order but may delete elements.
Constraints: `1 <= len(nums) <= 2000`, `-10^6 <= nums[i] <= 10^6`.

## Examples
```text
Input: nums = [1,3,5,4,7]
Output: 2
Explanation: The longest length is 4: [1,3,5,7] and [1,3,4,7].
```

## Understanding & Intuition
For each position, the useful state is the best increasing length ending there and how many ways reach it. When a previous smaller value can precede the current value, it either improves the length or ties the current best.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate all subsequences by recursion and count those with maximum increasing length.
```python
class Solution:
    def findNumberOfLIS(self, nums: list[int]) -> int:
        best = count = 0
        n = len(nums)
        def dfs(i: int, prev: int, length: int) -> None:
            nonlocal best, count
            if i == n:
                if length > best:
                    best, count = length, 1
                elif length == best:
                    count += 1
                return
            dfs(i + 1, prev, length)
            if prev == -1 or nums[i] > nums[prev]:
                dfs(i + 1, i, length + 1)
        dfs(0, -1, 0)
        return count
```
- **Time:** O(2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Memoize by index and previous index; each state returns the best suffix length and its count.
```python
class Solution:
    def findNumberOfLIS(self, nums: list[int]) -> int:
        from functools import lru_cache
        n = len(nums)
        @lru_cache(None)
        def dp(i: int, prev: int) -> tuple[int, int]:
            if i == n:
                return (0, 1)
            best_len, ways = dp(i + 1, prev)
            if prev == -1 or nums[i] > nums[prev]:
                take_len, take_ways = dp(i + 1, i)
                take_len += 1
                if take_len > best_len:
                    best_len, ways = take_len, take_ways
                elif take_len == best_len:
                    ways += take_ways
            return (best_len, ways)
        return dp(0, -1)[1]
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Approach 3 — Optimal
**Idea:** Compute length and count ending at every index in one left-to-right DP pass.
```python
class Solution:
    def findNumberOfLIS(self, nums: list[int]) -> int:
        n = len(nums)
        length = [1] * n
        count = [1] * n
        for i in range(n):
            for j in range(i):
                if nums[j] < nums[i]:
                    if length[j] + 1 > length[i]:
                        length[i] = length[j] + 1
                        count[i] = count[j]
                    elif length[j] + 1 == length[i]:
                        count[i] += count[j]
        longest = max(length)
        return sum(count[i] for i in range(n) if length[i] == longest)
```
- **Time:** O(n^2) — **Space:** O(n)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(2^n) | O(n) |
| Better | O(n^2) | O(n^2) |
| Optimal | O(n^2) | O(n) |

## Edge Cases & Pitfalls
- Equal numbers cannot extend a strictly increasing subsequence.
- Multiple index-distinct subsequences with the same values are counted separately.

## Related
- Longest Increasing Subsequence
- Russian Doll Envelopes
