# 06. Longest Arithmetic Subsequence

- **Difficulty:** Medium
- **Pattern:** DP on sequences / subsequences
- **Asked at:** Google, Facebook, Amazon

## Problem
You are given an integer array `nums`.

An arithmetic subsequence keeps the original order and has the same difference between every pair of consecutive chosen values. Return the maximum length of such a subsequence.

**Input**
- `nums`: a list of integers.

**Output**
- The length of the longest arithmetic subsequence.

## Constraints
- `2 <= nums.length <= 1000`
- `0 <= nums[i] <= 500`

## Examples
```text
Input: nums = [3,6,9,12]
Output: 4
Explanation: All four values form an arithmetic subsequence with common difference `3`.
```

## Understanding & Intuition
A subsequence is determined by its ending index and common difference. When `nums[j]` precedes `nums[i]`, any arithmetic sequence ending at `j` with that difference extends by one.

## Approach 1 — Naive / Brute Force
**Idea:** Enumerate all subsequences and test whether each chosen list is arithmetic.
```python
class Solution:
    def longestArithSeqLength(self, nums: list[int]) -> int:
        n = len(nums)
        best = 0
        def is_arith(seq: list[int]) -> bool:
            return len(seq) < 3 or all(seq[i] - seq[i - 1] == seq[1] - seq[0] for i in range(2, len(seq)))
        def dfs(i: int, seq: list[int]) -> None:
            nonlocal best
            if i == n:
                if is_arith(seq):
                    best = max(best, len(seq))
                return
            dfs(i + 1, seq)
            seq.append(nums[i])
            dfs(i + 1, seq)
            seq.pop()
        dfs(0, [])
        return best
```
- **Time:** O(n2^n) — **Space:** O(n)

## Approach 2 — Better
**Idea:** Fix the first two elements, then scan right greedily for the selected difference.
```python
class Solution:
    def longestArithSeqLength(self, nums: list[int]) -> int:
        n = len(nums)
        best = 2
        for i in range(n):
            for j in range(i + 1, n):
                diff = nums[j] - nums[i]
                length = 2
                last = nums[j]
                for t in range(j + 1, n):
                    if nums[t] - last == diff:
                        length += 1
                        last = nums[t]
                best = max(best, length)
        return best
```
- **Time:** O(n^3) — **Space:** O(1)

## Approach 3 — Optimal
**Idea:** Use a dictionary per ending index mapping difference to best length.
```python
class Solution:
    def longestArithSeqLength(self, nums: list[int]) -> int:
        n = len(nums)
        dp = [{} for _ in range(n)]
        best = 2
        for i in range(n):
            for j in range(i):
                diff = nums[i] - nums[j]
                dp[i][diff] = max(dp[i].get(diff, 1), dp[j].get(diff, 1) + 1)
                best = max(best, dp[i][diff])
        return best
```
- **Time:** O(n^2) — **Space:** O(n^2)

## Complexity Summary
| Approach | Time | Space |
|---|---|---|
| Naive | O(n2^n) | O(n) |
| Better | O(n^3) | O(1) |
| Optimal | O(n^2) | O(n^2) |

## Edge Cases & Pitfalls
- Differences may be negative or zero.
- The subsequence keeps order; do not sort the input.

## Related
- Longest Increasing Subsequence
- Wiggle Subsequence
